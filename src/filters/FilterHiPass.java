package filters;

import data.DataSeries;
import functions.Function;

/**
 * Created by mac on 20/02/15.
 */
public class FilterHiPass extends Function {
    int bufferSize;

    public FilterHiPass(DataSeries inputData, int bufferSize) {
        super(inputData);
        this.bufferSize = bufferSize;
    }

    @Override
    public int get(int index) {
        long sum = 0;
        if( bufferSize == 0) {
            return inputData.get(index);
        }
        if (index <= bufferSize) {
            for (int i = 0; i <= index; i++) {
                sum += inputData.get(i);
            }
            return inputData.get(index) - (int) (sum / (index + 1));
        }

        for (int i = index - bufferSize; i <=index; i++) {
            sum += inputData.get(i);
        }
        return inputData.get(index) - (int) (sum / (bufferSize + 1));
    }
}
