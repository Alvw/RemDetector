package filters;

import data.Converter;
import data.DataSet;

/**
 *
 */

public class FilterAbs extends Converter {

    private int derivative = 0;

    public FilterAbs(DataSet inputData) {
        super(inputData);
    }

    @Override
    public int get(int index) {

        return Math.abs(inputData.get(index));
    }
}
