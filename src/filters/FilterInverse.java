package filters;

import data.DataSeries;
import functions.Function;

/**
 *
 */

public class FilterInverse extends Function {

    private int derivative = 0;

    public FilterInverse(DataSeries inputData) {
        super(inputData);
    }

    @Override
    public int get(int index) {

        return -inputData.get(index);
    }
}

