package com.dream.Filters;

import com.dream.Functions;
import com.dream.Data.DataStream;

/**
 *
 */
public class FilterResonance extends Filter {
    private int period = 5;
    private int bufferHalf = period * 2;

    public FilterResonance(DataStream inputData) {
        super(inputData);
    }

    @Override
    protected int getData(int index) {

        if (index < bufferHalf || (index >= size()- bufferHalf)) {
            return 0;
        }

        int sum = 0;
        for (int i = -bufferHalf; i < bufferHalf; i++) {
            sum += inputData.get(index + i)* Functions.getTriangle(i, period);
        }
       return sum/(2 * bufferHalf * Functions.SCALE);
    }
}
