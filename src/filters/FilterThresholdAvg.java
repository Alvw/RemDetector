package filters;

import data.DataSeries;
import functions.Function;

/**
 *
 */
public class FilterThresholdAvg extends Function {
    private int bufferSize = 10;
    private int shift = 2; // points
    private int indexBefore = -10;
    private int sumBefore = 0;


    public FilterThresholdAvg(DataSeries inputData, int bufferSize, int shift) {
        super(inputData);
        this.bufferSize = bufferSize;
        this.shift = shift;
    }

    public FilterThresholdAvg(DataSeries inputData, int bufferSize) {
        super(inputData);
        this.bufferSize = bufferSize;
    }

    public FilterThresholdAvg(DataSeries inputData) {
        super(inputData);
    }

    @Override
    public int get(int index) {
        int sum = 0;
        int indexShifted = index - shift;

        if (indexShifted <= bufferSize) {
            for (int i = 0; i <= index; i++) {
                sum += Math.abs(inputData.get(i));
            }
            return sum/(index+1);
        }

        if(index == (indexBefore +1)) {
            sum = sumBefore + Math.abs(inputData.get(indexShifted-1)) - Math.abs(inputData.get(indexShifted - bufferSize-1));
            sumBefore = sum;
            indexBefore = index;
        }
        else {
            for (int i = (indexShifted - bufferSize); i < indexShifted; i++) {
                sum += Math.abs(inputData.get(i));
                //sum += inputData.get(i);
            }
        }
        return sum/bufferSize;
    }
}
