package rem;

import data.DataList;
import data.DataSeries;
import filters.FilterDerivative;
import filters.FilterDerivativeRem;
import filters.FilterDerivative_N;

/**
 * Humans and many animals do not look at a scene in fixed steadiness and
 * eye moves not with a smooth, steady movements, but instead, in a series of little rapid jumps (Saccades)
 * separated by pauses - brief periods of relative stability or slow phase movements (Fixations)
 * On average, humans make 2–3 saccades a second.
 * <p/>
 * So SACCADE is quick, simultaneous movement of both eyes between two phases of fixation
 * (from one fixation point to another)
 * The parameters commonly employed in the analysis of saccadic performance are
 * amplitude, the maximum angular velocity, duration, and latency.
 * <p/>
 * SACCADE AMPLITUDES: ranges of 1 - 30°/s.
 * The amplitude of a saccade is the angular distance the eye travels during the movement.
 * Under natural conditions most saccades (83%) are smaller than 15°,
 * 99% of all eye movements smaller then 30° and are within 15 degrees of primary position.
 * In  the  EOG  technique  it is  difficult to measure  saccades  less then  1-2  degree
 * since  the  noise inherent.
 * (See "Characteristics  of  saccades  and  vergence  in  two  kinds  of  sequential looking  tasks"
 * http://www.cis.rit.edu/pelz/lab/papers/malinov_epelboim_et_al_saccades_vergence_look-tap.pdf)
 * <p/>
 * !! Saccades larger than about 20° is accompanied by a head movement !!
 * <p/>
 * SACCADE PEAK VELOCITY (the highest velocity reached during the saccade): 400 - 600 degrees/s
 * For amplitudes up to 15 or 20°, the velocity of a saccade linearly depends on the amplitude
 * (saccadic main sequence). For amplitudes larger than 20°, the peak velocity starts to plateau
 * toward the asymptotic maximum (700° - 900°).
 * Actually for amplitudes more than 30 ° peak velocity is almost independent of the saccade size.
 * For instance, a 10° amplitude is associated with a velocity of 300°/s, and 30° is associated with 500-600°/s
 * Thus although big saccades ( 40-60°) exist and its max speed can reach 700°/s
 * the major (99% )of saccades are less then 30°.
 * Besides we measure the averaged values (for 20-40 ms)
 * AVERAGING_TIME = 40 ms
 * And average velocity peak is at least 20-30 percent less then the instantaneous velocity peak.
 * MAX_PEAK_VELOCITY  = 700 °/s
 * <p/>
 * SACCADE DURATION:  20-200ms. Most of them 30-80ms
 * For example:  2.5° - 37ms, 5° - 45ms,  10° - 55ms... (±10ms)
 * As was said with EOG we can measure only saccades bigger then 1-2 degree
 * it is logical to assume that
 * SACCADE_DURATION_MIN = 37-40 ms
 * <p/>
 * It is frequently found that a big main saccade is followed by a second smaller corrective saccade
 * that brings the eye closer to the target o Glissades.
 * (Glissades - slow drifting eye movements occasionally seen at the end of saccadic eye movements)
 * So it can take another 40-120ms to complete such saccade and
 * SACCADE_DURATION_MAX = 200ms (main saccade) + 120ms (small correcting saccade or glissade) = 320 ms
 * <p/>
 * SACCADE LATENCY: 100-200 ms
 * Saccades to an unexpected stimulus normally take about 200 milliseconds (ms) to initiate.
 * When the target moves suddenly, there is a delay of about 200 ms before the eye begins
 * to move to the new target position.
 * <p/>
 * Humans (and other animals with a fovea) typically alternate Saccades and visual fFixations.
 * (The notable exception is Smooth Pursuit -  eye movements that allow the eyes to closely follow a moving object)
 * Fixations differ in their length but tend to be for about 200-600ms, although much longer fixations can occur
 * Fixation short(50ms), overlong(4900 ms) and major normal (150-900ms). So
 * SACCADE_DISTANCE_NORMAL = 600 - 1000 ms
 * SACCADE_DISTANCE_MAX = 5-6 sec
 * <p/>
 * And almost always before and after every saccade should be a short period of relative tranquility:
 * REST_TIME = 100-200 ms
 */
public class SaccadeDetector1 {
    private static final int SACCADE_DURATION_MIN = 40; // [ms]  (milliseconds)
    private static final int SACCADE_DURATION_MAX = 320; // [ms]
    private static final int SACCADE_PEAK_VELOCITY_MAX = 700; // [°/s]
    private static final int REST_TIME = 100; // [ms]
    private static final int AVERAGING_TIME = 40; // [ms]
    private static final int SACCADE_DISTANCE_NORMAL = 600; // [ms]
    /**
     * The eye maintains a voltage of 0.40 to 1.0 millivolts with respect to the retinal.
     * This corneo-retinal dipole is roughly aligned with the optic axis and rotates correspondingly with the eye.
     * The corneoretinal potential can be measured by surface electrodes on the skin around eyes
     * and ranges:  of 15 to 200 microvolts, according to other sources to 400µV, to 1000µV and even from 50 to 3500µV.
     * !!! So this information has no no credibility and we need test how the potential on the skin around eyes
     * varies from person to person, different skin condition (dry, wet, with gel), illumination and so on !!!
     * <p/>
     * The calibration data obtained with our device:
     * eyes movement from max right to max left gives potential change:
     * about +400 to -400 µV
     * (when electrodes located in the corners of the eyes)
     * about +300 to -300 µV
     * (when electrodes located on the forehead above the eyes)
     * Maximum eye rotation is  ±70°.
     * For horizontal eye movements within the range of ±30 degrees,
     * the potential measured is assumed to be linear to the actual movement of the eye in the orbit.
     * But linearity becomes progressively worse for angles beyond 30°.
     * So max nonlinear ±70° eye rotation could be approximated by linear ±40(50)° rotation
     * And it gives us our signal magnitudes about  10 µV/° (microvolts per degree)
     * SENSITIVITY = 10 µV/°
     * <p/>
     * That coincides with the values for typical EOG-sensitivity ranges:  5-20 µV/°
     * and that in practice, 1° of eye movement evokes an average potential of 10–20 microvolts
     * (this value is more or less the same for all sources)
     * TYPICAL_SENSITIVITY = 10-20 µV/°
     */
    private static final int SENSITIVITY = 14; // [µV/°]
    private static final double SACCADE_VALUE_MAX_PHYSICAL =
            SACCADE_PEAK_VELOCITY_MAX * SENSITIVITY * AVERAGING_TIME / 1000; // [ µV ]

    private static final int THRESHOLD_PERIOD_GLOBAL = 20000; // [ms]
    private static final int THRESHOLD_PERIOD_LOCAL = 100; // [ms]
    private static final double SACCADE_THRESHOLD_RATIO_MIN = 1.3;
    private static final double N = 3; // Threshold to noise ratio

    /**
     * we calculate  global threshold on the base of noise
     * not at the currentIndex (where we take the Value to compare with the threshold)
     * but "THRESHOLD_LAG_POINTS"  before
     * Value almost never grows at once/immediately (it took 1-4 points as a rule)
     * so we need a shift/gap between the  threshold and the currentIndex
     */
    private static final int THRESHOLD_LAG_POINTS = 2;

    private DataSeries velocityData;
    private Saccade detectingSaccade;
    private Saccade previousSaccade;
    private boolean isSaccadeUnderDetection = false;

    private int threshold;
    private DataList thresholdList = new DataList();
    private NoiseDetector noiseDetectorGlobal;
    private NoiseDetector noiseDetectorLocal;
    private int currentIndex;
    private int saccadeValueMaxDigital;

    private int localThreshold;
    private DataList thresholdLocalList = new DataList();


    SaccadeDetector1(DataSeries eogData) {
        velocityData = new SaccadeFunction(new FilterDerivativeRem(eogData));
        DataSeries accelerationData = new FilterDerivativeRem(new FilterDerivativeRem(eogData));
        noiseDetectorGlobal = new NoiseDetector(accelerationData, THRESHOLD_PERIOD_GLOBAL);
        noiseDetectorLocal = new NoiseDetector(velocityData, THRESHOLD_PERIOD_LOCAL);
        double gain = 1;
        if(eogData.getScaling() != null) {
            gain = eogData.getScaling().getDataGain();
        }
        saccadeValueMaxDigital = (int) (SACCADE_VALUE_MAX_PHYSICAL / gain);
    }

    public int getSaccadeValueMaxDigital() {
        return saccadeValueMaxDigital;
    }

    public double getSaccadeValueMaxPhysical() {
        return SACCADE_VALUE_MAX_PHYSICAL;
    }

    private int getThreshold() {
        int thresholdGlobalPeriodPoints = (int) (THRESHOLD_PERIOD_GLOBAL * getFrequency() / 1000);

        if (currentIndex <= THRESHOLD_LAG_POINTS) {
            threshold = Integer.MAX_VALUE;
            return threshold;
        }
        else {
            localThreshold = (int) (noiseDetectorLocal.getNext() * N);
        }


        if (currentIndex < thresholdGlobalPeriodPoints) {
            noiseDetectorGlobal.getNext();
            threshold = Integer.MAX_VALUE;
            return threshold;
        }


        if (localThreshold == 0) {   // means connection problems and  signal failure
            noiseDetectorGlobal.skip();
            threshold = Integer.MAX_VALUE;
            return Integer.MAX_VALUE;
        }

        int lastPeakEnd = -thresholdGlobalPeriodPoints;
        if (previousSaccade != null) {
            lastPeakEnd = previousSaccade.getEndIndex();
        }
        if (!isSaccadeUnderDetection && currentIndex - lastPeakEnd > THRESHOLD_LAG_POINTS + 2) {
            int noise = noiseDetectorGlobal.getNext();
            threshold = (int) (noise * N);
        } else {
            noiseDetectorGlobal.skip();
        }
        return threshold;
    }


    /**
     * To detect saccades we use a variation of Velocity/Acceleration-Threshold Algorithm
     * that separates fixation and saccade points based on their point-to-point velocities/acceleration.
     * The velocity profiles of saccadic eye
     * movements show essentially two distributions of velocities:
     * low velocities for fixations (i.e., <100 deg/sec), and high
     * velocities (i.e., >300 deg/sec) for saccades
     * <p/>
     * Our algorithm:
     * 1) instead of fixed threshold use adaptive threshold calculated from the signal data itself during some period
     * 2) combine acceleration and velocity data: the threshold we calculated from the acceleration data
     * and then use it to detect velocity peaks.
     * (As acceleration actually involves data from more points: velocity = x2 - x1 and
     * acceleration = x3 + x1 - 2*x2, acceleration threshold is more sensitive to noise and
     * permits cut it off more effective, while in REM where "noise" decrease
     * Acceleration and Velocity thresholds are almost equal)
     * 3) instead of  instantaneous velocity and acceleration we use the averaged (for 40ms) ones.
     * That also reduce random noise and emphasize saccades
     * 4) Threshold is calculated on the base of energy, summarizing the squares of the values (instead of absolute values)
     * (Parseval's theorem:  the sum (or integral) of the square of a function is equal to the sum (or integral)
     * of the square of its Fourier transform. So from a physical point of view, more adequately work
     * with squares values (energy) )
     */
    public Saccade getNext() {
        double currentThreshold = getThreshold();
        Saccade resultSaccade = null;
        if (!isSaccadeUnderDetection) {
            if (Math.abs(velocityData.get(currentIndex)) > currentThreshold) {    // saccade begins
                isSaccadeUnderDetection = true;
                detectingSaccade = new Saccade(currentIndex, velocityData.get(currentIndex));
            }
        } else {
            if (Math.abs(velocityData.get(currentIndex)) > currentThreshold && isEqualSign(velocityData.get(currentIndex), detectingSaccade.getPeakValue())) {   // saccade  continues
                detectingSaccade.addPoint(currentIndex, velocityData.get(currentIndex));
            } else {   // saccade  ends
                isSaccadeUnderDetection = false;
                int saccadeDurationMinPoints = Math.round((float) (SACCADE_DURATION_MIN * getFrequency() / 1000));
                int saccadeDurationMaxPoints = Math.round((float) (SACCADE_DURATION_MAX * getFrequency() / 1000));
                double saccadeThresholdRatio = 0;
                if(currentThreshold != 0) {
                    saccadeThresholdRatio = Math.abs( detectingSaccade.getPeakValue() / currentThreshold );
                }
                if (detectingSaccade.getWidth() >= saccadeDurationMinPoints
                        && detectingSaccade.getWidth() <= saccadeDurationMaxPoints
                        && Math.abs(detectingSaccade.getPeakValue()) < saccadeValueMaxDigital
                        && saccadeThresholdRatio >= SACCADE_THRESHOLD_RATIO_MIN) {
                    previousSaccade = detectingSaccade;
                    resultSaccade = detectingSaccade;
                }

                detectingSaccade = null;
            }
        }

        thresholdList.add((int)currentThreshold);
        thresholdLocalList.add(localThreshold);
        currentIndex++;
        return resultSaccade;
    }

    public DataSeries getThresholds() {
        return thresholdList;
    }

    public DataSeries getLocalThresholds() {
        return thresholdLocalList;
    }

    private double getFrequency() {
        double frequency = 1;
        if(velocityData.getScaling() != null) {
            frequency = 1 / velocityData.getScaling().getSamplingInterval();
        }
        return frequency;
    }

    private boolean isEqualSign(int a, int b) {
        if ((a >= 0) && (b >= 0)) {
            return true;
        }

        if ((a <= 0) && (b <= 0)) {
            return true;
        }

        return false;
    }

}
