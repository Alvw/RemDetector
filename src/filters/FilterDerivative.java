package filters;

import data.DataStream;

/**
 *
 */

public class FilterDerivative extends Filter {

    public FilterDerivative(DataStream inputData) {
        super(inputData);
    }

    @Override
    protected int getData(int index) {
        if (index == 0) {
            return 0;
        }
        return inputData.get(index) - inputData.get(index - 1);
    }
}
