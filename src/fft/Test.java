package fft;

import data.DataList;
import data.DataSet;
import data.DataSetAdapter;
import fft.colombia.FFT;
import fft.jtransform.DoubleFFT_1D;
import fft.prinston.Complex;

public class Test {
    public static void main(String[] args) {
        double signalFr = 512; //HZ
        double signalTime = 2; // sec
        double sin1Fr = 10; // HZ
        double sin2Fr = 128; // HZ
        double sin3Fr = 5; // HZ
        double sin1Am = 100;
        double sin2Am = 50;
        double sin3Am = 40;


        int N = 1024; //(int)(signalFr * signalTime);

        double[] x = new double[N];
        double[] re = new double[N];
        double[] im = new double[N];

        for (int i = 0; i < N; i++) {
            double t = i / signalFr;
            x[i] = 100 + sin1Am * Math.sin(t * sin1Fr * Math.PI * 2) + sin2Am * Math.sin(t * sin2Fr * Math.PI * 2) + sin3Am * Math.sin(t * sin3Fr * Math.PI * 2);
            re[i] = x[i];
        }

        View view = new View();
        view.addGraph(DataSetAdapter.wrap(x));

        FFT fftColombia = new FFT(N);
        fftColombia.fft(re, im);

        fft.colombia.FFTNormalizer result = new fft.colombia.FFTNormalizer(re, im, signalFr);
        view.addGraph(result);

        for (int i = 0; i < result.size(); i++) {
            if (result.get(i) > 5) {
                System.out.println("frequency: " + result.getFrequency(i) + "  Ampl: " + result.get(i));
            }
        }
    }
}
