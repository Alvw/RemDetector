package filters;

import data.DataStream;

/**
 *
 */

public class FilterAbs extends Filter {

    private int derivative = 0;

    public FilterAbs(DataStream inputData) {
        super(inputData);
    }

    @Override
    protected int getData(int index) {

        return Math.abs(inputData.get(index));
    }
}
