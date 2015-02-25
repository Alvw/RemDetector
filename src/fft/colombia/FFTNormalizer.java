package fft.colombia;

import data.DataDimension;
import data.DataSet;


public class FFTNormalizer implements DataSet {

    double[] re;
    double[] im;
    double frequency;


    public FFTNormalizer(double[] re, double[] im, double frequency) {
        this.re = re;
        this.im = im;
        this.frequency = frequency;
    }

    private int getN() { // number of data points for FFT
        return re.length;
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
        double amplitude =  Math.sqrt(re[i] * re[i] + im[i] * im[i])/ getN();
        if(i > 0) {
            amplitude = amplitude * 2;
        }

        return Math.abs(amplitude);
    }

    @Override
    public int get(int index) {
        return (int) getAmplitude(index);
    }
    @Override
    public double getFrequency() {
        return 1/getFrequencyStep();
    }
    @Override
    public long getStartTime() {
        return 0;
    }
    @Override
    public DataDimension getDataDimension() {
        return new DataDimension();
    }

}

