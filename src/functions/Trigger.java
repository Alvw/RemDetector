package functions;

import data.DataDimension;
import data.DataSeries;

public class Trigger extends Function{
    int digitalLimit;


    public Trigger(DataSeries inputData, double physLimit) {
        super(inputData);
        digitalLimit = (int)(physLimit / inputData.getDataDimension().getGain());
    }

    @Override
    public int get(int index) {
        if(Math.abs(inputData.get(index)) < digitalLimit) {
            return 1;
        }
        return 0;
    }

    @Override
    public DataDimension getDataDimension() {
        return null;
    }
}
