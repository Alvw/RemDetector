package fft;

import data.DataList;
import data.DataSet;
import fft.colombia.FFT;
import fft.jtransform.DoubleFFT_1D;
import fft.prinston.Complex;

public class Test {
    public static void main(String[] args) {
        double signalFr = 512; //HZ
        double signalTime = 2; // sec
        double sin1Fr = 10; // HZ
        double sin2Fr = 128; // HZ
        double sin3Fr = 1; // HZ
        double sin1Am = 100;
        double sin2Am = 50;
        double sin3Am = 40;


        int  N = 1024; //(int)(signalFr * signalTime);
        double[] x = new double[2*N];
        Complex[] c = new Complex[N];
        double[] re = new double[N];
        double[] im = new double[N];

        for (int i = 0; i < N; i++) {
            double t =  i / signalFr;
            x[i] = 100 + sin1Am * Math.sin(t * sin1Fr * Math.PI * 2) + sin2Am * Math.sin(t * sin2Fr * Math.PI * 2) + sin3Am * Math.sin(t * sin3Fr * Math.PI * 2) ;
            //x[i] = (int)(N * Math.sin(t * sin1Fr * Math.PI * 2));
            c[i] = new Complex(x[i], 0);
            re[i] = x[i];
        }

        View view = new View();
        DataList xData = DataList.wrap(x);
        view.addGraph(xData);
        // xData.setFrequency(512);

        long time = System.currentTimeMillis();
        DoubleFFT_1D jfft = new DoubleFFT_1D(N);
        jfft.realForwardFull(x);
        time = System.currentTimeMillis() - time;
        System.out.println("jtransform " + time);

        time = System.currentTimeMillis();
        FFT fftColombia = new FFT(N);
        fftColombia.fft(re, im);
        for(int i = 0; i < re.length; i++) {
            Math.sqrt(re[i]*re[i] + im[i]*im[i]);
        }
        time = System.currentTimeMillis() - time;
        System.out.println("colombia "+time);

        time = System.currentTimeMillis();
        Complex[] prinstonResult = fft.prinston.FFT.fft(c);
        for(Complex complex : prinstonResult) {
            Math.sqrt(complex.re()*complex.re() + complex.im()*complex.im());
        }
        time = System.currentTimeMillis() - time;
        System.out.println("prinston "+time);



        DataSet jtransformSet = DataList.wrap(x);
        DataSet prinstonSet = DataList.wrap(prinstonResult);

        // DataSet result = FFTNormalizer.normalize(x, signalFr);
        // FFTNormalizer1 result = new FFTNormalizer1(x, signalFr);
        fft.colombia.FFTNormalizer result = new fft.colombia.FFTNormalizer(re, im, signalFr);
        view.addGraph(result);
        // view.addGraph(jtransformSet);
        //  view.addGraph(prinstonSet);
        for(int i= 0; i < result.size(); i++) {
            if(result.get(i) > 5) {
                System.out.println("frequency: "+result.getFrequency(i) +"  Ampl: "+(int)result.get(i));
            }
        }
    }
}
