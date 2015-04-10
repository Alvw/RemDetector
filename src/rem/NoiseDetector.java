package rem;

import data.DataDimension;
import data.DataSeries;
import data.DataStream;

public class NoiseDetector implements DataStream {
    private DataSeries inputData;
    private int numberOfPoints;
    private long sumValue;
    private int numberOfTakenElements = 0;

    public NoiseDetector(DataSeries inputData, int periodMsec) {
        this.inputData = inputData;
        numberOfPoints = Math.round((float)(periodMsec * inputData.getFrequency() / 1000));
    }

/**
 * Calculate average energy of inputData for given period.
 * That's why we summarizing squared values (instead of absolute values) of inputData
 * and then finally get the square root of the result sum
 *
 * (Parseval's theorem:  the sum (or integral) of the square of a function is equal
 * to the sum (or integral) of the square of its Fourier transform)
 * So from a physical point of view, more adequately work with squares values (energy)
 */
    @Override
    public int getNext() {
        int index = numberOfTakenElements++;
        if(index < numberOfPoints) {
            sumValue = sumValue + inputData.get(index) * inputData.get(index);
            return (int)Math.sqrt((sumValue / (index + 1)));
        }
        else {
            sumValue = sumValue + inputData.get(index) * inputData.get(index) - inputData.get(index - numberOfPoints) * inputData.get(index - numberOfPoints);
            return (int)Math.sqrt(sumValue / numberOfPoints);
        }
    }


    @Override
    public int available() {
        return inputData.size() - numberOfTakenElements;
    }

    @Override
    public double getFrequency() {
        return inputData.getFrequency();
    }

    @Override
    public long getStartTime() {
        return inputData.getStartTime();
    }

    @Override
    public DataDimension getDataDimension() {
        return inputData.getDataDimension();
    }
}
