package filters;

import data.DataDimension;
import data.DataSet;

public class FilterDerivativeRemTreshhold extends Filter{
    int digitalLimit;
    private int distance = 4;
    public FilterDerivativeRemTreshhold(DataSet inputData, double physLimit) {
        super(inputData);
        digitalLimit = (int)(physLimit / inputData.getDataDimension().getGain());
    }

    @Override
    public int get(int index) {
        if (index < distance) {
            return 1;
        }
        int derivativeRem = Math.abs(inputData.get(index)) - Math.abs(inputData.get(index - distance));
        if(Math.abs(derivativeRem) >= digitalLimit) {
            return 0;
        }
        return 1;
    }

    @Override
    public DataDimension getDataDimension() {
        return new DataDimension();
    }
}
