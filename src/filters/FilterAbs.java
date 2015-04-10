package filters;

import data.DataSeries;
import functions.Function;

/**
 *
 */

public class FilterAbs extends Function {

    private int derivative = 0;

    public FilterAbs(DataSeries inputData) {
        super(inputData);
    }

    @Override
    public int get(int index) {

        return Math.abs(inputData.get(index));
    }
}
