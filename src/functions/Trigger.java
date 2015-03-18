package functions;

import data.DataDimension;
import data.DataSet;

public class Trigger extends Function{
    int digitalLimit;


    public Trigger(DataSet inputData, double physLimit) {
        super(inputData);
        digitalLimit = (int)(physLimit / inputData.getDataDimension().getGain());
    }

    @Override
    public int get(int index) {
        if(Math.abs(inputData.get(index)) < digitalLimit) {
            return DataSet.TRUE;
        }
        return DataSet.FALSE;
    }

    @Override
    public DataDimension getDataDimension() {
        return new DataDimension();
    }
}
