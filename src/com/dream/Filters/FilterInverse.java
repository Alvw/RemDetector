package com.dream.Filters;

import com.dream.Data.DataStream;

/**
 *
 */

public class FilterInverse extends Filter {

    private int derivative = 0;

    public FilterInverse(DataStream inputData) {
        super(inputData);
    }

    @Override
    protected int getData(int index) {

        return -inputData.get(index);
    }
}

