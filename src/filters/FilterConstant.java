package filters;

import data.DataSeries;
import functions.Function;

public class FilterConstant extends Function {
    int digitalConstant;
    public FilterConstant(DataSeries inputData, double physConstant) {
        super(inputData);
        digitalConstant = (int)(physConstant / inputData.getDataDimension().getGain());
        System.out.println("phys value "+physConstant);
        System.out.println("dig value "+digitalConstant);
    }

    @Override
    public int get(int index) {
        return digitalConstant;
    }
}
