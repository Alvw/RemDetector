package fft;

import data.DataDimension;
import data.DataSet;

public class FFTData implements DataSet{
    double[] fftResult;
    double frequency;

    public FFTData(double[] fftResult, double frequency) {
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

    @Override
    public int get(int index) {
        return (int) getAmplitude(index);
    }

    @Override
    public double getFrequency() {
        return 0;
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
