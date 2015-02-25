package fft;

import data.DataList;
import data.DataSet;

public class FFTNormalizer {
    double[] fftResult;
    double frequency;

    public static DataSet normalize(double[] fftResult, double frequency) {
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

    public DataSet getResultAmplitudes() {
        double step = frequency / fftResult.length;
        double maxFrequency = (frequency / 2);
        int maxIndex = (int)(maxFrequency/step +1);
        double[] resultAmplitudes = new double[maxIndex];
        for(int i = 0; i < size(); i++) {
           int index = (int) (getFrequency(i) / step);
            resultAmplitudes[index] += getAmplitude(i);
            resultAmplitudes[i] += getAmplitude(i);
        }

        DataList result = DataList.wrap(resultAmplitudes);
        result.setFrequency(1/step);
        return result;
    }

}
