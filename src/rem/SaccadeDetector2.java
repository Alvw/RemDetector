package rem;

import data.DataDimension;
import data.DataList;
import data.DataSeries;
import dreamrec.FourierAnalizer;
import fft.Fourie;
import filters.FilterDerivative;
import filters.FilterDerivativeRem;
import filters.FilterLowPass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Saccade (step):
 * 1) MAX_LEVEL > abs(derivation) > SACCADE_LEVEL
 * 2) derivation don't change sign
 * 3) saccade duration > 40 msec (SACCADE_WIDTH_MIN_MSEC)
 * 4) before and after saccade eyes are in rest (when abs(derivation) < NOISE_LEVEL) > 100 msec (LATENCY_PERIOD_MSEC)
 */
class SaccadeDetector2 implements DataSeries {
    private static final int THRESHOLD_PERIOD_MSEC = 20000;

    private static final int N = 7; // Threshold to sumValue ratio
    private static final int SACCADE_WIDTH_MIN_MSEC = 40;
    private static final int SACCADE_WIDTH_MAX_MSEC = 200;
    private static final int SACCADE_DISTANCE_MAX = 12; // sec

    private static final int FOURIER_TIME = 6; // sec

    private List<Saccade> saccadeList = new ArrayList<Saccade>();
    private List<Saccade> saccadePackageList = new ArrayList<Saccade>();
    private HashMap<Integer, Integer> output = new HashMap<Integer, Integer>();
    private DataSeries derivativeRem;
    private DataSeries derivative;
    private Saccade detectingSaccade;
    private boolean isSaccadeUnderDetection = false;
    private boolean isPackageApproved = false;
    private boolean hasPackageBigSaccade = false;
    private int saccadeMaxDistancePoints;
    private int thresholdPeriodPoints;
    private int threshold;
    private int currentIndex = -1;
    private DataList thresholdList = new DataList();
    int sumValue = 0;

    SaccadeDetector2(DataSeries inputData) {
        derivativeRem = new FilterLowPass(new FilterDerivativeRem(inputData), 25.0);
        derivative = new FilterDerivative(inputData);
        thresholdPeriodPoints = (int) (THRESHOLD_PERIOD_MSEC * derivativeRem.getFrequency() / 1000);
        saccadeMaxDistancePoints = (int)(SACCADE_DISTANCE_MAX * derivativeRem.getFrequency());
    }



    private int getNoise(int index) {
        int shift = 2;
        if(index < shift) {
            return Math.abs(derivativeRem.get(index));

        }

        if(index - shift <= thresholdPeriodPoints) {
            sumValue = sumValue + Math.abs(derivativeRem.get(index - shift));
            return sumValue / (index - shift + 1);
        }
        else {
            sumValue = sumValue + Math.abs(derivativeRem.get(index - shift)) - Math.abs(derivativeRem.get(index - shift - thresholdPeriodPoints));
            return sumValue /thresholdPeriodPoints;
        }

    }


    public void detectSaccade(int index) {
        currentIndex = index;
        int noise = getNoise(index);
        if (!isSaccadeUnderDetection) {
            int lastSaccadeEnd = - thresholdPeriodPoints-1;
            if(saccadePackageList.size() > 0) {
                Saccade lastSaccade = saccadePackageList.get(saccadePackageList.size() - 1);
                lastSaccadeEnd = lastSaccade.getEndIndex();
            }
            if(index - lastSaccadeEnd > thresholdPeriodPoints) {
                threshold = noise * N;
                if(index < thresholdPeriodPoints ) {
                    threshold = Integer.MAX_VALUE;
                }
            }

            if (Math.abs(derivativeRem.get(index)) > threshold) {    // saccade begins
                isSaccadeUnderDetection = true;
                detectingSaccade = new Saccade();
                detectingSaccade.setBeginIndex(index);
                detectingSaccade.setPeakValue(derivativeRem.get(index));
                detectingSaccade.setPeakIndex(index);
            }
        } else {
            if (Math.abs(derivativeRem.get(index)) > threshold && isEqualSign(derivativeRem.get(index), detectingSaccade.getPeakValue())) {   // saccade  continues
                if (Math.abs(derivativeRem.get(index)) > Math.abs(detectingSaccade.getPeakValue())) {
                    detectingSaccade.setPeakValue(derivativeRem.get(index));
                    detectingSaccade.setPeakIndex(index);
                }
            } else {   // saccade  ends
                isSaccadeUnderDetection = false;
                detectingSaccade.setEndIndex(index);
                if(isSaccadePossible(detectingSaccade)) {
                    addSaccadeToPackage(detectingSaccade);
                }
                detectingSaccade = null;
            }
        }

        thresholdList.add(threshold);
    }

    private boolean isSaccadePossible(Saccade saccade) {
        DataSeries fourier =  Fourie.fftBackward(derivative, saccade.getEndIndex(), FOURIER_TIME);
        if(FourierAnalizer.hasAlfa(fourier)) {
            return false;
        }
        return true;
    }

    private void addSaccadeToPackage(Saccade saccade) {
        int saccadesDistance = 0;
        if(saccadePackageList.size() > 0) {
            Saccade saccadePreviose = saccadePackageList.get(saccadePackageList.size() - 1);
            saccadesDistance = saccade.getBeginIndex() - saccadePreviose.getEndIndex();
        }

        if(saccadesDistance > saccadeMaxDistancePoints) {  // create new package
            saccadePackageList = new ArrayList<Saccade>();
            isPackageApproved = false;
            hasPackageBigSaccade = false;
        }

        saccadePackageList.add(saccade);

        if(!isPackageApproved) { // we are not sure that package has saccades and not sumValue
            if(Math.abs(saccade.getPeakValue()) >= threshold * 2) {
                hasPackageBigSaccade = true;
            }

            if(hasPackageBigSaccade && saccadePackageList.size() > 1) { // approve package as saccades package
                isPackageApproved = true;
                for(Saccade saccadePackage : saccadePackageList) { // copy saccades from approved Package
                    addSaccade(saccadePackage);
                }

            }
        }
        else { // we are  sure that package has saccades and not sumValue
            addSaccade(saccade);
        }

    }

    private void addSaccade(Saccade saccade) {
        saccadeList.add(saccade);
        for(int i = saccade.getBeginIndex(); i <= saccade.getEndIndex(); i++ ) {
           output.put(i, Math.abs(saccade.getPeakValue()));
        }
    }

   @Override
    public int get(int index) {
        if(currentIndex < index){
            for(int i = currentIndex + 1; i <= index; i++) {
                detectSaccade(i);
            }
        }
       Integer value = output.get(index);
       if(value != null) {
           return value;
       }
       return 0;
    }

    public DataSeries getThresholds() {
        return thresholdList;
    }

    @Override
    public int size() {
        return derivativeRem.size();
    }

    @Override
    public double getFrequency() {
        return derivativeRem.getFrequency();
    }

    @Override
    public long getStartTime() {
        return derivativeRem.getStartTime();
    }

    @Override
    public DataDimension getDataDimension() {
        return null;
    }

    protected boolean isEqualSign(int a, int b) {
        if ((a >= 0) && (b >= 0)) {
            return true;
        }

        if ((a <= 0) && (b <= 0)) {
            return true;
        }

        return false;
    }
}


