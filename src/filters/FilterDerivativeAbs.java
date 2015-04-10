package filters;

import data.DataSeries;
import functions.Function;

/**
 *
 */

public class FilterDerivativeAbs extends Function {

    public FilterDerivativeAbs(DataSeries inputData) {
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
