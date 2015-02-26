package filters;

import data.DataSet;

/**
 *
 */

public class FilterDerivativeRem extends Filter {
    private int DISTANCE_MSEC = 80;
    private int distance_point;
    
    public FilterDerivativeRem(DataSet inputData) {
        super(inputData);
        distance_point = (int)(DISTANCE_MSEC * inputData.getFrequency() / 1000);
    }

    @Override
    public int get(int index) {
        if (index < distance_point) {
            return 0;
        }
        return Math.abs(inputData.get(index)) - Math.abs(inputData.get(index - distance_point));
        //return Math.abs(inputData.get(index) - inputData.get(index - distance_point));
    }
}

