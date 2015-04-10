package fft.prinston;

import data.DataDimension;
import data.DataSeries;

public class FFTNormalizer implements DataSeries {

    Complex[] fftResult;
    double frequency;


    public FFTNormalizer(Complex[] fftResult, double frequency) {
        this.fftResult = fftResult;
        this.frequency = frequency;
    }

    private int getN() { // number of data points for FFT
        return fftResult.length;
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
        double amplitude =  Math.sqrt(fftResult[i].re() * fftResult[i].re() + fftResult[i].im() * fftResult[i].im())/ getN();

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
