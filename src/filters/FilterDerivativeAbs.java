package filters;

import data.DataSet;
import functions.Function;

/**
 *
 */

public class FilterDerivativeAbs extends Function {

    public FilterDerivativeAbs(DataSet inputData) {
        super(inputData);
    }

    @Override
    public int get(int index) {
        if (index == 0) {
            return 0;
        }

        return  Math.abs(inputData.get(index) - inputData.get(index - 1));
    }
}
