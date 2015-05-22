package filters;

import data.DataSeries;
import functions.Function;

/**
 *
 */

public class FilterLowPass extends Function {
    private int bufferSize;
    private int indexBefore = -10;
    private long sumBefore = 0;

    public FilterLowPass(DataSeries inputData, int bufferSize) {
        super(inputData);
        this.bufferSize = bufferSize;
    }

    public FilterLowPass(DataSeries inputData, double cutOffFrequency) {
        super(inputData);
        double frequency = 1;
        if(inputData.getScaling() != null) {
            frequency = inputData.getScaling().getSamplingInterval();
        }
        bufferSize = (int) (frequency / cutOffFrequency);
    }

    @Override
    public int get(int index) {
        if (index < bufferSize) {
            return 0;
        }
        if (index >= size()- bufferSize) {
            return 0;
        }
        long sum = 0;
        if(index == (indexBefore +1)) {
            sum = sumBefore + inputData.get(index + bufferSize) - inputData.get(index - bufferSize);
            sumBefore = sum;
            indexBefore = index;
        }
        else {
            for (int i = (index - bufferSize); i < (index + bufferSize); i++) {
                sum += inputData.get(i);
            }
        }
        return (int)(sum/(2*bufferSize));
    }
}
