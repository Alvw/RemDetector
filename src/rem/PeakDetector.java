package rem;

import data.DataList;
import data.DataSeries;
import filters.FilterDerivativeRem;

/**
 * Created by mac on 06/04/15.
 */
public class PeakDetector {
    private static final int THRESHOLD_PERIOD_MSEC = 20000;
    private static final int THRESHOLD_PERIOD_SHORT_MSEC = 200;

    private static final int N = 1; // Threshold to noise ratio
    private DataSeries inputData;

    private Peak detectingPeak;
    private Peak previousPeak;
    private boolean isPeakUnderDetection = false;
    private int thresholdPeriodPoints;
    private int thresholdPeriodShortPoints;
    private int threshold;
    private int thresholdShort;
    private int thresholdLong;
    private DataList thresholdList = new DataList();
    private NoiseSeries noiseDetector;
    private NoiseSeries noiseDetectorShort;
    private NoiseSeries noiseDetectorShort1;

   PeakDetector(DataSeries inputData) {
       this.inputData = inputData;
       noiseDetector = new NoiseSeries(new FilterDerivativeRem(inputData), THRESHOLD_PERIOD_MSEC);
       thresholdPeriodPoints = (int) (THRESHOLD_PERIOD_MSEC * this.inputData.getFrequency() / 1000);

       noiseDetectorShort = new NoiseSeries(new FilterDerivativeRem(inputData), THRESHOLD_PERIOD_SHORT_MSEC);
       noiseDetectorShort1 = new NoiseSeries(inputData, THRESHOLD_PERIOD_SHORT_MSEC);
       thresholdPeriodShortPoints = (int) (THRESHOLD_PERIOD_SHORT_MSEC * this.inputData.getFrequency() / 1000);
    }

    private int getThreshold(int index) {
        int lastPeakEnd = - thresholdPeriodPoints-1;
        if(previousPeak != null) {
            lastPeakEnd = previousPeak.getEndIndex();
        }
        if(index - lastPeakEnd > thresholdPeriodPoints) {
            if(index < thresholdPeriodPoints ) {
                threshold = Integer.MAX_VALUE;
            }
            else {
                threshold = noiseDetector.get(index - 1) * N;
            }
        }
        return threshold;
    }


    private int getThreshold1(int index) {
        if(index < thresholdPeriodPoints ) {
            threshold = Integer.MAX_VALUE;
            return threshold;
        }

        int lastPeakEnd = - thresholdPeriodPoints-1;
        if(previousPeak != null) {
            lastPeakEnd = previousPeak.getEndIndex();
        }
        if(index - lastPeakEnd > thresholdPeriodPoints) {
            thresholdLong = noiseDetector.get(index - 1)* N;
            thresholdShort = Math.min(noiseDetectorShort.get(index - 2), noiseDetectorShort1.get(index - 3))* N;
            threshold = (int)Math.sqrt((thresholdLong*thresholdLong + thresholdShort*thresholdShort) / 2);
        }
        else {
            thresholdShort = noiseDetectorShort.get(index - 2)* N;
            thresholdShort = Math.min(noiseDetectorShort.get(index - 2), noiseDetectorShort1.get(index - 3))* N;
            threshold = (int)Math.sqrt((thresholdLong*thresholdLong + thresholdShort*thresholdShort) / 2);
        }
       /* if(index - lastPeakEnd > thresholdPeriodShortPoints && index - lastPeakEnd < thresholdPeriodPoints) {
            thresholdShort = noiseDetectorShort.get(index - 2)* N;
            threshold = (thresholdLong + thresholdShort) / 2;
        }  */
        return threshold;
    }


    private int getThreshold2(int index) {
        if(index < thresholdPeriodPoints ) {
            threshold = Integer.MAX_VALUE;
            return threshold;
        }

        int lastPeakEnd = - thresholdPeriodPoints-1;
        if(previousPeak != null) {
            lastPeakEnd = previousPeak.getEndIndex();
        }
        if(index - lastPeakEnd > thresholdPeriodPoints) {
            thresholdLong = noiseDetector.get(index - 1)* N;
            threshold = thresholdLong;
        }
        else {
            thresholdShort = noiseDetectorShort.get(index - 2)* N;
            int thresholdShort1 = noiseDetectorShort1.get(index - 3)* N;
            threshold = (thresholdLong + thresholdShort + thresholdShort1) / 3;
        }
       /* if(index - lastPeakEnd > thresholdPeriodShortPoints && index - lastPeakEnd < thresholdPeriodPoints) {
            thresholdShort = noiseDetectorShort.get(index - 2)* N;
            threshold = (thresholdLong + thresholdShort) / 2;
        }  */
        return threshold;
    }


    public Peak detect(int index) {
        Peak resultPeak = null;
        if (!isPeakUnderDetection) {
            getThreshold1(index);

            if (Math.abs(inputData.get(index)) > threshold) {    // saccade begins
                isPeakUnderDetection = true;
                detectingPeak = new Peak();
                detectingPeak.setBeginIndex(index);
                detectingPeak.setPeakValue(inputData.get(index));
                detectingPeak.setPeakIndex(index);
            }
        } else {
            if (Math.abs(inputData.get(index)) > threshold && isEqualSign(inputData.get(index), detectingPeak.getPeakValue())) {   // saccade  continues
                if (Math.abs(inputData.get(index)) > Math.abs(detectingPeak.getPeakValue())) {
                    detectingPeak.setPeakValue(inputData.get(index));
                    detectingPeak.setPeakIndex(index);
                }
            } else {   // saccade  ends
                isPeakUnderDetection = false;
                detectingPeak.setEndIndex(index);
                if(detectingPeak.getEndIndex() - detectingPeak.getBeginIndex() > 1) {
                   // detectingPeak.setPeakRatio(Math.abs(detectingPeak.getPeakValue()) / threshold);
                    previousPeak = detectingPeak;
                    resultPeak = detectingPeak;
                }

                detectingPeak = null;
            }
        }

        thresholdList.add(threshold);
        return resultPeak;
    }

    public DataSeries getThresholds() {
        return thresholdList;
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
