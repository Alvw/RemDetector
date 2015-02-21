package filters;

import data.DataDimension;
import data.DataSet;

/**
 * Created by mac on 20/02/15.
 */
public class FilterThreshold extends Filter {
    int digitalLimit;


    public FilterThreshold(DataSet inputData, double physLimit) {
        super(inputData);
        digitalLimit = (int)(physLimit / inputData.getDataDimension().getGain());
    }

    @Override
    public int get(int index) {
        if(Math.abs(inputData.get(index)) >= digitalLimit) {
            return 0;
        }
        return 1;
    }

    @Override
    public DataDimension getDataDimension() {
        return new DataDimension();
    }
}

