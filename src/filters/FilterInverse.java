package filters;

import data.DataSet;

/**
 *
 */

public class FilterInverse extends Filter {

    private int derivative = 0;

    public FilterInverse(DataSet inputData) {
        super(inputData);
    }

    @Override
    protected int getData(int index) {

        return -inputData.get(index);
    }
}

