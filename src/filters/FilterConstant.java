package filters;

import data.DataSet;
import functions.Function;

public class FilterConstant extends Function {
    int digitalConstant;
    public FilterConstant(DataSet inputData, double physConstant) {
        super(inputData);
        digitalConstant = (int)(physConstant / inputData.getDataDimension().getGain());
    }

    @Override
    public int get(int index) {
        return digitalConstant;
    }
}
