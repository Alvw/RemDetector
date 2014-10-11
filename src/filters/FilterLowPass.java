package filters;

import data.DataStream;

/**
 *
 */

public class FilterLowPass extends FilterBuffered {
    private int bufferSize;
    private int indexBefore = -10;
    private int sumBefore = 0;

    public FilterLowPass(DataStream inputData, int bufferSize) {
        super(inputData);
        this.bufferSize = bufferSize;
    }

    @Override
    protected int getData(int index) {
        if (index < bufferSize) {
            return 0;
        }
        if (index >= size()- bufferSize) {
            return 0;
        }
        int sum = 0;
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
        return sum/(2*bufferSize);
    }
}
