package rem;

import data.DataList;
import data.DataSeries;
import filters.FilterDerivativeRem;

/**
 * Created by mac on 06/04/15.
 */
public class SaccadeDetector {
    private static final int THRESHOLD_PERIOD_MSEC = 20000;
    private static final int THRESHOLD_PERIOD_SHORT_MSEC = 200;

    private static final double N = 3.5; // Threshold to noise ratio
    private DataSeries velocityData;

    private Saccade detectingSaccade;
    private Saccade previousSaccade;
    private boolean isSaccadeUnderDetection = false;
    private int thresholdPeriodPoints;
    private int thresholdPeriodShortPoints;
    private int threshold;
    private int thresholdShort;
    private int thresholdLong;
    private DataList thresholdList = new DataList();
    private NoiseDetector noiseDetector;
    private int noiseLag = 2; // points

    private int currentIndex;

   SaccadeDetector(DataSeries eogData) {
       velocityData = new FilterDerivativeRem(eogData);
       DataSeries accelerationData =  new FilterDerivativeRem(velocityData);
       noiseDetector = new NoiseDetector(accelerationData, THRESHOLD_PERIOD_MSEC);
      // noiseDetector = new NoiseDetector(velocityData, THRESHOLD_PERIOD_MSEC);

       thresholdPeriodShortPoints = (int) (THRESHOLD_PERIOD_SHORT_MSEC * this.velocityData.getFrequency() / 1000);
       thresholdPeriodPoints = (int) (THRESHOLD_PERIOD_MSEC * this.velocityData.getFrequency() / 1000);
   }

    private int getThreshold() {
        if(currentIndex <= noiseLag) {
            threshold  = Integer.MAX_VALUE;
            return threshold;
        }
        if(currentIndex < thresholdPeriodPoints) {
            noiseDetector.getNext();
            threshold  = Integer.MAX_VALUE;
            return threshold;
        }

        int lastPeakEnd = - thresholdPeriodPoints-1;
        if(previousSaccade != null) {
            lastPeakEnd = previousSaccade.getEndIndex();
        }
        if(!isSaccadeUnderDetection &&  currentIndex - lastPeakEnd > noiseLag +2 ) {
            int noise = noiseDetector.getNext();
            threshold = (int)(noise * N);
        }
        else {
            noiseDetector.skip();
        }
        return threshold;
    }


 /*   private int getThreshold1(int index) {
        if(index < thresholdPeriodPoints ) {
            threshold = Integer.MAX_VALUE;
            return threshold;
        }

        int lastPeakEnd = - thresholdPeriodPoints-1;
        if(previousSaccade != null) {
            lastPeakEnd = previousSaccade.getEndIndex();
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
      //  return threshold;
   // }





    public Saccade getNext() {
        int currentThreshold = getThreshold();
        Saccade resultSaccade = null;
        if (!isSaccadeUnderDetection) {
            if (Math.abs(velocityData.get(currentIndex)) > currentThreshold) {    // saccade begins
                isSaccadeUnderDetection = true;
                detectingSaccade = new Saccade();
                detectingSaccade.setBeginIndex(currentIndex);
                detectingSaccade.setPeakValue(velocityData.get(currentIndex));
                detectingSaccade.setPeakIndex(currentIndex);
            }
        } else {
            if (Math.abs(velocityData.get(currentIndex)) > currentThreshold && isEqualSign(velocityData.get(currentIndex), detectingSaccade.getPeakValue())) {   // saccade  continues
                if (Math.abs(velocityData.get(currentIndex)) > Math.abs(detectingSaccade.getPeakValue())) {
                    detectingSaccade.setPeakValue(velocityData.get(currentIndex));
                    detectingSaccade.setPeakIndex(currentIndex);
                }
            } else {   // saccade  ends
                isSaccadeUnderDetection = false;
                detectingSaccade.setEndIndex(currentIndex);
                if(threshold > 0) {
                    detectingSaccade.setPeakRatio(Math.abs(detectingSaccade.getPeakValue()) / threshold);
                }
                if(detectingSaccade.getEndIndex() - detectingSaccade.getBeginIndex() > 1 && detectingSaccade.getPeakRatio() >=1.5) {
                    previousSaccade = detectingSaccade;
                    resultSaccade = detectingSaccade;
                }

                detectingSaccade = null;
            }
        }

        thresholdList.add(currentThreshold);
        currentIndex++;
        return resultSaccade;
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
