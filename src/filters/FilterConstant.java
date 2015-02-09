package filters;

import data.DataSet;

public class FilterConstant extends Filter {
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
