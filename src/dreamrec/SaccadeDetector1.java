package dreamrec;

import data.DataDimension;
import data.DataList;
import data.DataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Saccade (step):
 * 1) MAX_LEVEL > abs(derivation) > SACCADE_LEVEL
 * 2) derivation don't change sign
 * 3) saccade duration > 40 msec (SACCADE_WIDTH_MIN_MSEC)
 * 4) before and after saccade eyes are in rest (when abs(derivation) < NOISE_LEVEL) > 100 msec (LATENCY_PERIOD_MSEC)
 */
class SaccadeDetector1 implements DataSet{
    private static final int THRESHOLD_PERIOD_MSEC = 120;
    private static final int THRESHOLD_AREA_MSEC = 200;
    private static final double N = 5; // Threshold to sumValue ratio
    private static final int SACCADE_WIDTH_MIN_MSEC = 40;
    private static final int SACCADE_WIDTH_MAX_MSEC = 200;

    private List<Peak> peakList = new ArrayList<Peak>();
    private DataSet inputData;
    private Peak detectingPeak;
    private boolean isUnderDetection = false;
    private int thresholdPeriodPoints;
    private int thresholdAreaPoints;
    private int threshold;
    private int currentIndex = -1;
    private DataList thresholdList = new DataList();

    SaccadeDetector1(DataSet inputData) {
        this.inputData = inputData;
        thresholdPeriodPoints = (int) (THRESHOLD_PERIOD_MSEC * inputData.getFrequency() / 1000);
        thresholdAreaPoints = (int) (THRESHOLD_AREA_MSEC * inputData.getFrequency() / 1000);
    }

    private int getNoise(int index) {
        if (index <= thresholdPeriodPoints) {
            return Integer.MAX_VALUE;
        }
        int max = 0;
        for (int i = 1; i <= thresholdPeriodPoints; i++) {
            max = Math.max(max, Math.abs(inputData.get(index - i)));
        }
        return max;
    }

    private int getThreshold(int index) {
        int threshold = Integer.MAX_VALUE;

        for (int i = Math.max(0, index - thresholdAreaPoints); i <= index; i++) {
            threshold = Math.min(threshold, getNoise(i));
        }
        return (int) (threshold * N);
        //  index = Math.max(0, index - 2);
        //  return (int)(getNoise(index) * N);
    }


    public void detect(int index) {
        currentIndex = index;
        if (!isUnderDetection) {
            threshold = getThreshold(index);
         /*   int lastSaccadeEnd = - thresholdPeriodPoints;
            if(saccadeList.size() > 0) {
                Saccade lastSaccade = saccadeList.get(saccadeList.size() - 1);
                lastSaccadeEnd = lastSaccade.getEndIndex();
            }
            if(index - lastSaccadeEnd >= thresholdPeriodPoints) {
                threshold = getThreshold(index);
            }  */

            if (Math.abs(inputData.get(index)) > threshold) {    // saccade begins
                isUnderDetection = true;
                detectingPeak = new Peak();
                detectingPeak.setBeginIndex(index);
                detectingPeak.setPeakValue(inputData.get(index));
                detectingPeak.setPeakIndex(index);
            }
        } else {
            if (Math.abs(inputData.get(index)) > threshold && isEqualSign(inputData.get(index), detectingPeak.getPeakValue())) {   // saccade  continues
                if (Math.abs(inputData.get(index)) > detectingPeak.getPeakValue()) {
                    detectingPeak.setPeakValue(inputData.get(index));
                    detectingPeak.setPeakIndex(index);
                }
            } else {   // saccade  ends
                isUnderDetection = false;
                detectingPeak.setEndIndex(index);
                peakList.add(detectingPeak);
                detectingPeak = null;
            }
        }
        thresholdList.add(threshold);
    }

    @Override
    public int get(int index) {
        if(currentIndex < index){
            for(int i = currentIndex + 1; i <= index; i++) {
                detect(i);
            }
        }
        for(Peak peak : peakList) {
            if(peak.getBeginIndex() <= index && index <= peak.getEndIndex()) {
                return Math.abs(peak.getPeakValue());
            }
        }
        return 0;
    }

    public DataSet getThresholds() {
        return thresholdList;
    }

    @Override
    public int size() {
        return inputData.size();
    }

    @Override
    public double getFrequency() {
        return inputData.getFrequency();
    }

    @Override
    public long getStartTime() {
        return inputData.getStartTime();
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


