package filters;

import data.DataSet;

/**
 *
 */

public class FilterDerivative extends Filter {

    public FilterDerivative(DataSet inputData) {
        super(inputData);
    }

    @Override
    public int get(int index) {
        if (index == 0) {
            return 0;
        }
        return inputData.get(index) - inputData.get(index - 1);
    }
}
