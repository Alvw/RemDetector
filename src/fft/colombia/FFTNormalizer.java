package fft.colombia;

import data.DataSeries;
import data.Scaling;
import data.ScalingImpl;


public class FFTNormalizer implements DataSeries {

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
    public Scaling getScaling() {
        ScalingImpl scaling = new ScalingImpl();
        scaling.setSamplingInterval(getFrequencyStep());
        return scaling;
    }
}

