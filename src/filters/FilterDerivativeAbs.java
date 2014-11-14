package filters;

import data.DataSet;

/**
 *
 */

public class FilterDerivativeAbs extends Filter {

    public FilterDerivativeAbs(DataSet inputData) {
        super(inputData);
    }

    @Override
    protected int getData(int index) {
        if (index == 0) {
            return 0;
        }

        return  Math.abs(inputData.get(index) - inputData.get(index - 1));
    }
}
