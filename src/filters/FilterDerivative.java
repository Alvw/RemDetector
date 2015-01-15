package filters;

import data.Converter;
import data.DataSet;

/**
 *
 */

public class FilterDerivative extends Converter {

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
