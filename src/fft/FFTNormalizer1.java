package fft;

import data.DataList;
import data.DataSet;

public class FFTNormalizer1 {
    double[] fftResult;
    double frequency;

    public static DataSet normalize(double[] fftResult, double frequency) {
        return new FFTNormalizer(fftResult, frequency).getResultAmplitudes();

    }

    public FFTNormalizer1(double[] fftResult, double frequency) {
        this.fftResult = fftResult;
        this.frequency = frequency;
        System.out.println("Results: "+fftResult.length + " "+getN());

    }

    private int getN() { // number of data points for FFT
        return fftResult.length / 2;
    }

    private double getFrequencyStep() {
        return frequency / getN();
    }

    public int size() {
        return getN()/2;
    }

    public double getFrequency(int i) {
        return i * getFrequencyStep();
    }

    public double getAmplitude(int i) {
        double amplitude =  fftResult[i] / getN();
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
