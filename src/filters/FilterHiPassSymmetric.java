package filters;

import data.DataSet;
import functions.Function;

/**
 *
 */
public class FilterHiPassSymmetric extends Function {
    private int indexBefore = -10;
    private long sumBefore = 0;
    int bufferSize;

    public FilterHiPassSymmetric(DataSet inputData, int bufferSize) {
        super(inputData);
        this.bufferSize = bufferSize;
    }

   // @Override
    protected int getData_new(int index) {
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
        return inputData.get(index) - (int)(sum/(2*bufferSize));
    }

    public int get(int index) {
        if (index < bufferSize) {
            return 0;
        }
        if (index >= size()-bufferSize) {
            return 0;
        }
        long sum = 0;
        for (int i = (index - bufferSize); i < (index + bufferSize); i++) {
            sum += inputData.get(i);
        }
        return inputData.get(index) - (int)(sum/(2*bufferSize));
    }

}