package fft;

import data.DataList;
import data.DataSet;
import fft.jtransform.DoubleFFT_1D;
import fft.prinston.Complex;

public class Fourie {

    public static DataSet fft(DataSet inputData, int startIndex, int time) {
        double frequency = inputData.getFrequency();
        int N = (int) frequency * time;
        N = Math.min(N, inputData.size() - startIndex);
        N = toPower2(N);  // length of input data for Fourie: must be a power of two

        double[] dataArray = new double[2 * N]; // for jtransform array should have double length, half empty
        for(int i = 0; i < N; i++) {
            dataArray[i] = inputData.get(startIndex + i);
        }

        DoubleFFT_1D jfft = new DoubleFFT_1D(N);
        jfft.realForwardFull(dataArray);
        double[] result = FFTNormalizer.normalize(dataArray, inputData.getFrequency());

        return DataList.wrap(result);
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




    public static void main(String[] args) {

        double signalFr = 1024; //HZ
        double signalTime = 2; // sec
        double sin1Fr = 10; // HZ
        double sin2Fr = 128; // HZ
        double sin3Fr = 5; // HZ
        double sin1Am = 100;
        double sin2Am = 50;
        double sin3Am = 40;


        int  N = 1024; //(int)(signalFr * signalTime);
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

        long time = System.currentTimeMillis();
        DoubleFFT_1D jfft = new DoubleFFT_1D(N);
        jfft.realForwardFull(x);
        time = System.currentTimeMillis() - time;
        System.out.println("jtransform "+time);

        time = System.currentTimeMillis();
        Complex[] prinstonResult = fft.prinston.FFT.fft(c);
        for(Complex value : prinstonResult) {
             Math.sqrt(value.re()*value.re() + value.im()*value.im());
        }
        time = System.currentTimeMillis() - time;
        System.out.println("prinston "+time);

        DataSet jtransformSet = DataList.wrap(x);
        DataSet prinstonSet = DataList.wrap(prinstonResult);

        double[] result = FFTNormalizer.normalize(x, signalFr);



        view.addGraph(DataList.wrap(result));
     //   view.addGraph(jtransformSet);
     //   view.addGraph(prinstonSet);
        for(int i= 0; i < result.length; i++) {
            if(result[i] > 5) {
                System.out.println("frequency: "+i +"  Ampl: "+(int)result[i]);
            }
        }


    }
}
