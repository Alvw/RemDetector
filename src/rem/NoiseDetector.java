package rem;

import data.DataSeries;

import java.util.LinkedList;

public class NoiseDetector {
    private DataSeries inputData;
    private int counter = 0;
    private LinkedList<Integer> bufferedValues;
    private int numberOfPoints;
    private long sum;

    public NoiseDetector(DataSeries inputData, int periodMsec) {
        this.inputData = inputData;
        double frequency = 1;
        if(inputData.getScaling() != null) {
            frequency = 1 / inputData.getScaling().getSamplingInterval();
        }
        numberOfPoints = Math.round((float)(periodMsec * frequency / 1000));
        if(numberOfPoints < 1) {
            numberOfPoints = 1;
        }
        bufferedValues = new LinkedList<Integer>();
    }

/**
 * Calculate noise as an average energy of inputData for given period.
 * That's why we summarizing squared values (instead of absolute values) of inputData
 * and then finally get the square root of the result sum
 *
 * (Parseval's theorem:  the sum (or integral) of the square of a function is equal
 * to the sum (or integral) of the square of its Fourier transform)
 * So from a physical point of view, more adequately work with squares values (energy)
 */

    public int getNext() {
        int value =  inputData.get(counter++);
        bufferedValues.add(value);
        if(counter < numberOfPoints) {
            sum += value * value;
            return (int)Math.sqrt((sum / counter));
        }
        else {
            int bufferedValue =  0;
            Integer  bufferedObject = bufferedValues.poll();
            if(bufferedObject != null) {
                bufferedValue = bufferedObject;
            }

            sum += value * value - bufferedValue * bufferedValue;
            return (int)Math.sqrt(sum / numberOfPoints);
        }
    }

    /**
     * Skip input data if we dont want calculate it as a noise
     * (to skip artifacts and so on)
     */
    public void skip() {
        counter++;
    }


}
