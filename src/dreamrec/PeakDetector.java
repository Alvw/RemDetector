package dreamrec;

import data.DataList;
import data.DataSet;

/**
 * Created by mac on 06/04/15.
 */
public class PeakDetector {
    private static final int THRESHOLD_PERIOD = 20; // sec

    private static final int N = 7; // Threshold to noise ratio
    private DataSet inputData;

    private Peak detectingPeak;
    private Peak previousPeak;
    private boolean isPeakUnderDetection = false;
    private int thresholdPeriodPoints;
    private int threshold;
    private DataList thresholdList = new DataList();
    int sumValue = 0;

   PeakDetector(DataSet inputData) {
       this.inputData = inputData;
       thresholdPeriodPoints = (int) (THRESHOLD_PERIOD * this.inputData.getFrequency());
    }



    private int getNoise(int index) {
        int shift = 1;
        if(index < shift) {
            return Math.abs(inputData.get(index));

        }

        if(index - shift <= thresholdPeriodPoints) {
            sumValue = sumValue + Math.abs(inputData.get(index - shift));
            return sumValue / (index - shift + 1);
        }
        else {
            sumValue = sumValue + Math.abs(inputData.get(index - shift)) - Math.abs(inputData.get(index - shift - thresholdPeriodPoints));
            return sumValue /thresholdPeriodPoints;
        }

    }


    public Peak detect(int index) {
        int noise = getNoise(index);
        Peak resultPeak = null;
        if (!isPeakUnderDetection) {
            int lastPeakEnd = - thresholdPeriodPoints-1;
            if(previousPeak != null) {
                lastPeakEnd = previousPeak.getEndIndex();
            }
            if(index - lastPeakEnd > thresholdPeriodPoints) {
                threshold = noise * N;
                if(index < thresholdPeriodPoints ) {
                    threshold = Integer.MAX_VALUE;
                }
            }

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
                detectingPeak.setPeakRatio(Math.abs(detectingPeak.getPeakValue()) / threshold);
                previousPeak = detectingPeak;
                resultPeak = detectingPeak;
                detectingPeak = null;
            }
        }

        thresholdList.add(threshold);
        return resultPeak;
    }

    public DataSet getThresholds() {
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
