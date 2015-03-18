package filters;

import data.DataSet;
import functions.Function;

/**
 *
 */

public class FilterDerivative extends Function {

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
