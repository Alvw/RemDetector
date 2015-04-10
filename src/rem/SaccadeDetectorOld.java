package rem;

import data.DataSet;
import filters.FilterDerivative;
import filters.FilterDerivative_N;
import filters.FilterThresholdAvg;

/**
 * Saccade (step):
 * 1) MAX_LEVEL > abs(derivation) > SACCADE_LEVEL
 * 2) derivation don't change sign
 * 3) saccade duration > 40 msec (SACCADE_WIDTH_MIN_MSEC)
 * 4) before and after saccade eyes are in rest (when abs(derivation) < NOISE_LEVEL) > 100 msec (LATENCY_PERIOD_MSEC)
 */
class SaccadeDetectorOld {
    private static final  int THRESHOLD_PERIOD_MSEC = 200;
    private static final  int THRESHOLD_AREA_MSEC = 400;
    private static final int N = 8; // Threshold to sumValue ratio
    private static final int MAX_RATIO = 5; // ratio to calculate max saccade amplitude on the base of velocityThreshold
    private static final int SACCADE_WIDTH_MIN_MSEC = 40;
    private static final int SACCADE_WIDTH_MAX_MSEC = 200;

    private DataSet inputData;
    private DataSet velocityData;
    private DataSet accelerationData;
    private DataSet velocityThresholdData;
    private DataSet accelerationThresholdData;

    private int saccadeBeginIndex = 0;
    private int saccadePeakIndex = 0;
    private int saccadeEndIndex = 0;
    private int saccadePeakVelocity = 0;
    private int saccadeSign = 0;
    private int velocityThreshold = 0;
    private int accelerationThreshold = 0;

    private boolean isUnderDetection = false;

    private int thresholdPeriodPoints;
    private int lastThresholdIndex = - 2* thresholdPeriodPoints;

    SaccadeDetectorOld(DataSet inputData) {
        this.inputData = inputData;
        thresholdPeriodPoints = (int)(THRESHOLD_PERIOD_MSEC * inputData.getFrequency() / 1000);
        velocityData = new FilterDerivative(inputData);
        accelerationData =  new FilterDerivative_N(inputData, 1);
        velocityThresholdData = new FilterThresholdAvg(velocityData, thresholdPeriodPoints);
        accelerationThresholdData = new FilterThresholdAvg(accelerationData, thresholdPeriodPoints);
    }



    private void setThresholds(int index) {
        if(!isUnderDetection && (index - lastThresholdIndex > thresholdPeriodPoints +2)) {
            velocityThreshold = N * velocityThresholdData.get(index);
            accelerationThreshold = N * accelerationThresholdData.get(index);
        }

    }

    private void resetSaccade() {
        saccadeBeginIndex = 0;
        saccadePeakIndex = 0;
        saccadeEndIndex = 0;
        saccadePeakVelocity = 0;
        saccadeSign = 0;
        isUnderDetection = false;
    }

    public boolean isSaccadeDetected(int index) {
        int velocity = velocityData.get(index);
        int acceleration = accelerationData.get(index);
        setThresholds(index);
        if ((Math.abs(velocity) > velocityThreshold)) {
            if(!isUnderDetection) {   // saccade begins
                resetSaccade();
                isUnderDetection = true;
                saccadeBeginIndex = index;
                saccadeSign = getSign(velocity);
            }
            else {      // saccade continues
                int velocityBefore = velocityData.get(index - 1);
                if(Math.abs(velocity) < Math.abs(velocityBefore)) {   // velocity should form the bell with single peak
                    if(saccadePeakIndex == 0) {
                        saccadePeakIndex = index - 1;
                        saccadePeakVelocity = velocityBefore;
                    }
                }
            }

            if((Math.abs(velocity) > MAX_RATIO * velocityThreshold) || !isEqualSign(velocity, saccadeSign)) {
                resetSaccade();
                return false;
            }
        }
        else {
            if(isUnderDetection) {     // saccade finishes
                int saccadeWidthMinPoints = (int)(SACCADE_WIDTH_MIN_MSEC * inputData.getFrequency() / 1000);
                int saccadeWidthMaxPoints = (int)(SACCADE_WIDTH_MAX_MSEC * inputData.getFrequency() / 1000);
                saccadeEndIndex = index - 1;
                int saccadeWidth = saccadeEndIndex - saccadeBeginIndex + 1;
                if(saccadeWidth < saccadeWidthMinPoints || saccadeWidth > saccadeWidthMaxPoints) {
                    resetSaccade();
                    return false;
                }
                lastThresholdIndex = saccadeEndIndex;
                isUnderDetection = false;
                if(saccadePeakIndex == 0) {
                    saccadePeakIndex = saccadeEndIndex;
                    saccadePeakVelocity = velocityData.get(saccadePeakIndex);
                }
                // System.out.println("begin: "+saccadeBeginIndex + " end: "+saccadeEndIndex);
                return true;
            }
        }
        return false;
    }


    public int getSaccadeIndex() {
        return saccadeEndIndex;
    }

    public int getSaccadeValue() {
        return saccadePeakVelocity;
    }


    private int getSign(int a) {
        if (a >= 0) {
            return 1;
        }
        return -1;
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


