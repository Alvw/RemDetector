package fft;

import data.DataSet;
import fft.colombia.FFT;
import fft.colombia.FFTNormalizer;

public class Fourie {

    /*
     * from [index] to [index + time]
     */
    public static DataSet fftForward(DataSet inputData, int index, double time) {
        double frequency = inputData.getFrequency();
        int N = (int) (frequency * time);
        N = Math.min(N, inputData.size() - index);
        N = toPower2(N);  // length of input data for Fourie: must be a power of two

        double[] dataRe = new double[N];
        double[] dataIm = new double[N];
        for (int i = 0; i < N; i++) {
            dataRe[i] = inputData.get(index + i);
        }

        FFT fft = new FFT(N);
        fft.fft(dataRe, dataIm);
        return new FFTNormalizer(dataRe, dataIm, inputData.getFrequency());
    }

 /*
 * from [index - time] to [index]
 */
    public static DataSet fftBackward(DataSet inputData, int index, int time) {
        double frequency = inputData.getFrequency();
        int N = (int) frequency * time;
        N = Math.min(N, index);
        N = toPower2(N);  // length of input data for Fourie: must be a power of two

        double[] dataRe = new double[N];
        double[] dataIm = new double[N];
        for (int i = 0; i < N; i++) {
            dataRe[i] = inputData.get(index - i);
        }

        FFT fft = new FFT(N);
        fft.fft(dataRe, dataIm);
        return new FFTNormalizer(dataRe, dataIm, inputData.getFrequency());
    }

    /*
     * Find the nearest number that is  a power of two and < i
     */
    public static int toPower2(int i) {
        int power = (int) log2(i);
        // return (int)Math.pow(2, power);
        return (1 << power);
    }

    public static double log2(double a) {
        return Math.log(a) / Math.log(2);
    }
}
