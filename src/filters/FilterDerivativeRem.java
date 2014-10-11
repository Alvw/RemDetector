package filters;

import data.DataStream;

/**
 *
 */

public class FilterDerivativeRem extends Filter {

    private int distance = 4;
    
    public FilterDerivativeRem(DataStream inputData) {
        super(inputData);
        bufferSize=4;
    }

    @Override
    protected int getData(int index) {
        if (index < distance) {
            return 0;
        }
        return Math.abs(inputData.get(index)) - Math.abs(inputData.get(index - distance));
        //return Math.abs(inputData.get(index) - inputData.get(index - distance));
    }
}

