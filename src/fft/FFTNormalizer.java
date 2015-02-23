package fft;

import data.DataDimension;
import data.DataSet;

public class FFTNormalizer {
    double[] fftResult;
    double frequency;

    public static double[] normalize(double[] fftResult, double frequency) {
        return new FFTNormalizer(fftResult, frequency).getResultAmplitudes();

    }

    public FFTNormalizer(double[] fftResult, double frequency) {
        this.fftResult = fftResult;
        this.frequency = frequency;

    }

    public int size() {
        return fftResult.length/2;
    }

    public double getFrequency(int i) {
        if(i == 0) {
            return 0;
        }
        return frequency * (i - 1) / (fftResult.length);
    }

    public double getAmplitude(int i) {
        double amplitude = 2 * fftResult[i] / (fftResult.length);
        if(i > 0) {
            amplitude = amplitude * 2;
        }

        return Math.abs(amplitude);
    }

    public double[] getResultAmplitudes() {
        int maxFrequency = (int)(frequency / 2);
        double[] resultAmplitudes = new double[maxFrequency + 1];

        for(int i = 0; i < size(); i++) {
           int index = (int) getFrequency(i);
            resultAmplitudes[index] += getAmplitude(i);
        }
        return resultAmplitudes;
    }

}
