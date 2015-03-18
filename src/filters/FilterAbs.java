package filters;

import data.DataSet;
import functions.Function;

/**
 *
 */

public class FilterAbs extends Function {

    private int derivative = 0;

    public FilterAbs(DataSet inputData) {
        super(inputData);
    }

    @Override
    public int get(int index) {

        return Math.abs(inputData.get(index));
    }
}
