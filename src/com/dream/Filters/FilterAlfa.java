package com.dream.Filters;

import com.dream.Functions;
import com.dream.Data.DataStream;

/**
 *
 */
public class FilterAlfa extends Filter {
    private int period = 4;
    private int bufferHalf = period * 4;
    private DataStream alfaData;

    public FilterAlfa(DataStream inputData) {
        super(inputData);
        alfaData = new FilterHiPass(new FilterBandPass_Alfa(inputData), 2);
    }

    @Override
    protected int getData(int index) {
        if(index < 1 ) {
            return 0;
        }
        else {
            return Math.max(Math.abs(alfaData.get(index)) , Math.abs(alfaData.get(index-1)));

        }
    }
}