package filters;

import data.DataSeries;
import functions.Function;

/**
 *
 */

public class FilterDerivativeRem extends Function {
    private int DISTANCE_MSEC = 40;
    private int distance_point;
    
    public FilterDerivativeRem(DataSeries inputData) {
        super(inputData);
        distance_point = Math.round((float)(DISTANCE_MSEC * inputData.getFrequency() / 1000));
        if(distance_point== 0) {
            distance_point = 1;
        }
    }

    @Override
    public int get(int index) {
        if (index < distance_point) {
            return 0;
        }
        return inputData.get(index) - inputData.get(index - distance_point);
        //return Math.abs(inputData.get(index)) - Math.abs(inputData.get(index - distance_point));
    }
}

