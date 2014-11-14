package filters;

import data.DataSet;

/**
 *
 */

public class FilterAbs extends Filter {

    private int derivative = 0;

    public FilterAbs(DataSet inputData) {
        super(inputData);
    }

    @Override
    protected int getData(int index) {

        return Math.abs(inputData.get(index));
    }
}
