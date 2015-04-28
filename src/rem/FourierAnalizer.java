package rem;

import data.DataSeries;
import filters.FilterFourierIntegral;

/**
 * Created by mac on 05/04/15.
 */
public class FourierAnalizer {

    public static boolean hasAlfa(DataSeries fourier) {
        DataSeries fourierIntegral = new FilterFourierIntegral(fourier);

        double frequencyStep = fourierIntegral.getScaling().getSamplingInterval();

        int maxBefore = 0;
        int maxAlpha = 0;
        int maxAfter = 0;
        double alphaBeginFrequency = 8; // Hz
        double alphaEndFrequency = 15; // Hz
        double band = 0.3;

        double peakRatio = 1.5;

        for(int i = 0; i < fourierIntegral.size(); i++) {
            double frequency = i * frequencyStep;

            if(frequency < alphaBeginFrequency && frequency > alphaBeginFrequency * (1 - band) ) {
                maxBefore = Math.max(maxBefore, fourierIntegral.get(i));
            }

            if(frequency < alphaEndFrequency && frequency > alphaBeginFrequency ) {
                maxAlpha = Math.max(maxAlpha, fourierIntegral.get(i));
            }

            if(frequency > alphaEndFrequency && frequency < alphaEndFrequency * (1 + band) ) {
                maxAfter = Math.max(maxAfter, fourierIntegral.get(i));
            }
        }

        if(Math.max(maxBefore, maxAfter) > 0 && maxAlpha / Math.max(maxBefore, maxAfter) > peakRatio) {
            return true;
        }
        return false;
    }

    public static int getHighFrequenciesSum(DataSeries fourier) {

        double frequencyStep = fourier.getScaling().getSamplingInterval();
        double highFrequencyBegin = 14; // Hz

        int sum = 0;
        for (int i = 0; i < fourier.size(); i++) {
            double frequency = i * frequencyStep;

            if (frequency > highFrequencyBegin) {
                sum = sum + fourier.get(i);
            }
        }
        return sum;
    }

}
