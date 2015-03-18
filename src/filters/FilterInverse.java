package filters;

import data.DataSet;
import functions.Function;

/**
 *
 */

public class FilterInverse extends Function {

    private int derivative = 0;

    public FilterInverse(DataSet inputData) {
        super(inputData);
    }

    @Override
    public int get(int index) {

        return -inputData.get(index);
    }
}

