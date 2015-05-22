package functions;

import data.DataSeries;

public class Constant extends Function {
    int digitalConstant;
    public Constant(DataSeries inputData, double physConstant) {
        super(inputData);
        digitalConstant = (int)(physConstant / inputData.getScaling().getDataGain());
    }

    @Override
    public int get(int index) {
        return digitalConstant;
    }
}
