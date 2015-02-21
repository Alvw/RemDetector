package fft;

import data.DataList;
import data.DataSet;
import fft.jtransform.DoubleFFT_1D;
import fft.prinston.Complex;

import javax.swing.*;

public class Fourie {

    public FFTData fft(DataSet inputData, int startIndex, int time) {
        double frequency = inputData.getFrequency();
        int N = (int) frequency * time;
        N = Math.min(N, inputData.size() - startIndex);
        N = toPower2(N);  // length of input data for Fourie: must be a power of two

        double[] dataArray = new double[N];
        for(int i = 0; i < N; i++) {
            dataArray[i] = inputData.get(startIndex + i);
        }

        fft(dataArray);
        return new FFTData(dataArray, frequency);
    }

    /*
     * Find the nearest number that is  a power of two and < i
     */
    public static int toPower2(int i) {
        int power = (int) log2(i);
       // return (int)Math.pow(2, power);
        return (1<<power);
    }

    public static double log2(double a) {
         return Math.log(a) / Math.log(2);
    }

    public static double[] getWindow(int n) {
        // Make a blackman window:
        // w(n)=0.42-0.5cos{(2*PI*n)/(N-1)}+0.08cos{(4*PI*n)/(N-1)};
        double[] window = new double[n];
        for (int i = 0; i < window.length; i++)
            window[i] = 0.42 - 0.5 * Math.cos(2 * Math.PI * i / (n - 1))
                    + 0.08 * Math.cos(4 * Math.PI * i / (n - 1));
        return window;
    }


    /**
     * ************************************************************
     * fft.c
     * Douglas L. Jones
     * University of Illinois at Urbana-Champaign
     * January 19, 1992
     * http://cnx.rice.edu/content/m12016/latest/
     * <p/>
     * fft: in-place radix-2 DIT DFT of a complex input
     * <p/>
     * input:
     * n: length of Fourie: must be a power of two
     * m: n = 2**m
     * input/output
     * x: double array of length n with real part of data
     * y: double array of length n with imag part of data
     * <p/>
     * Permission to copy and use this program is granted
     * as long as this header is included.
     * **************************************************************
     */
    public static void fft(double[] x) {
        double[] y = new double[x.length];
        fft(x, y);
    }

    public static void fft(double[] x, double[] y) {
        int n = x.length;
        int m = (int) (Math.log(n) / Math.log(2));

        double[] cos;
        double[] sin;

        // Make sure n is a power of 2
        if (n % 2 != 0) { throw new RuntimeException("N is not a power of 2"); }


        // precompute tables
        cos = new double[n / 2];
        sin = new double[n / 2];

//     for(int i=0; i<n/4; i++) {
//       cos[i] = Math.cos(-2*Math.PI*i/n);
//       sin[n/4-i] = cos[i];
//       cos[n/2-i] = -cos[i];
//       sin[n/4+i] = cos[i];
//       cos[n/2+i] = -cos[i];
//       sin[n*3/4-i] = -cos[i];
//       cos[n-i]   = cos[i];
//       sin[n*3/4+i] = -cos[i];
//     }

        for (int i = 0; i < n / 2; i++) {
            cos[i] = Math.cos(-2 * Math.PI * i / n);
            sin[i] = Math.sin(-2 * Math.PI * i / n);
        }


        int i, j, k, n1, n2, a;
        double c, s, e, t1, t2;


        // Bit-reverse
        j = 0;
        n2 = n / 2;
        for (i = 1; i < n - 1; i++) {
            n1 = n2;
            while (j >= n1) {
                j = j - n1;
                n1 = n1 / 2;
            }
            j = j + n1;

            if (i < j) {
                t1 = x[i];
                x[i] = x[j];
                x[j] = t1;
                t1 = y[i];
                y[i] = y[j];
                y[j] = t1;
            }
        }

        // Fourie
        n1 = 0;
        n2 = 1;

        for (i = 0; i < m; i++) {
            n1 = n2;
            n2 = n2 + n2;
            a = 0;

            for (j = 0; j < n1; j++) {
                c = cos[a];
                s = sin[a];
                a += 1 << (m - i - 1);

                for (k = j; k < n; k = k + n2) {
                    t1 = c * x[k + n1] - s * y[k + n1];
                    t2 = s * x[k + n1] + c * y[k + n1];
                    x[k + n1] = x[k] - t1;
                    y[k + n1] = y[k] - t2;
                    x[k] = x[k] + t1;
                    y[k] = y[k] + t2;
                }
            }
        }
    }

    public static void main(String[] args) {

        double signalFr = 512; //HZ
        double signalTime = 2; // sec
        double sin1Fr = 8; // HZ
        double sin2Fr = 128; // HZ
        double sin3Fr = 1; // HZ
        double sin1Am = 100;
        double sin2Am = 50;
        double sin3Am = 40;


        int  N = 512; //(int)(signalFr * signalTime);
        double[] x = new double[2*N];
        Complex[] c = new Complex[N];

        for (int i = 0; i < N; i++) {
            double t =  i / signalFr;
            x[i] = 100 + sin1Am * Math.sin(t * sin1Fr * Math.PI * 2) + sin2Am * Math.sin(t * sin2Fr * Math.PI * 2) + sin3Am * Math.sin(t * sin3Fr * Math.PI * 2) ;
            //x[i] = (int)(N * Math.sin(t * sin1Fr * Math.PI * 2));
            c[i] = new Complex(x[i], 0);
        }

        View view = new View();
        DataList xData = DataList.wrap(x);
        view.addGraph(xData);
       // xData.setFrequency(512);

        DoubleFFT_1D jfft = new DoubleFFT_1D(N);
        jfft.realForwardFull(x);

        Complex[] prinstonResult = fft.prinston.FFT.fft(c);

        DataSet jtransformSet = DataList.wrap(x);
        DataSet prinstonSet = DataList.wrap(prinstonResult);

        FFTData result = new FFTData(x, 512);



        view.addGraph(result);
     //   view.addGraph(jtransformSet);
     //   view.addGraph(prinstonSet);
        for(int i= 0; i < result.size(); i++) {
            if(result.getAmplitude(i) > 5) {
                System.out.println("frequency: "+result.getFrequency(i) +"  Ampl: "+(int)result.getAmplitude(i));
            }
        }


    }
}
