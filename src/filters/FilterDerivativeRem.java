package filters;

import data.DataSeries;
import functions.Function;

/**
 *
 */

public class FilterDerivativeRem extends Function {
    private static final int DEFAULT_DISTANCE_MSEC = 40;
    private int distance_point;

    public FilterDerivativeRem(DataSeries inputData, int timeMs) {
        super(inputData);
        double samplingRate = 1;
        if(inputData.getScaling() != null) {
            samplingRate = 1 / inputData.getScaling().getSamplingInterval();
        }
        distance_point = Math.round((float)(timeMs * samplingRate / 1000));
        if(distance_point== 0) {
            distance_point = 1;
        }
    }

    public FilterDerivativeRem(DataSeries inputData) {
       this(inputData, DEFAULT_DISTANCE_MSEC);
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

