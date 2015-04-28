package fft;

import data.DataSeries;
import fft.colombia.FFT;
import fft.colombia.FFTNormalizer;

public class Fourie {

    /*
     * from [index] to [index + time]
     */
    public static DataSeries fftForward(DataSeries inputData, int index, double time) {
        double frequency = 1;
        if(inputData.getScaling() != null) {
            frequency = 1 / inputData.getScaling().getSamplingInterval();
        }
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
        return new FFTNormalizer(dataRe, dataIm, frequency);
    }

 /*
 * from [index - time] to [index]
 */
    public static DataSeries fftBackward(DataSeries inputData, int index, int time) {
        double frequency = 1;
        if(inputData.getScaling() != null) {
            frequency = inputData.getScaling().getSamplingInterval();
        }
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
        return new FFTNormalizer(dataRe, dataIm, frequency);
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
