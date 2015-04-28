package filters;

import data.DataSeries;
import functions.Function;

public class FilterConstant extends Function {
    int digitalConstant;
    public FilterConstant(DataSeries inputData, double physConstant) {
        super(inputData);
        digitalConstant = (int)(physConstant / inputData.getScaling().getDataGain());
    }

    @Override
    public int get(int index) {
        return digitalConstant;
    }
}
