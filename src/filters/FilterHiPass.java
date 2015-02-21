package filters;

import data.DataSet;

import javax.swing.*;

/**
 * Created by mac on 20/02/15.
 */
public class FilterHiPass extends Filter {
    int bufferSize;

    public FilterHiPass(DataSet inputData, int bufferSize) {
        super(inputData);
        this.bufferSize = bufferSize;
    }

    @Override
    public int get(int index) {
        long sum = 0;
        if(index == 0 || bufferSize == 0) {
            return inputData.get(index);
        }
        if (index <= bufferSize) {
            for (int i = 0; i < index; i++) {
                sum += inputData.get(i);
            }
            return inputData.get(index) - (int) (sum / index);
        }

        for (int i = index - bufferSize; i < index; i++) {
            sum += inputData.get(i);
        }
        return inputData.get(index) - (int) (sum / bufferSize);
    }
}
