package com.dream.Filters;

import com.dream.Data.DataStream;

/**
 *
 */

public class CompressorMaximizing extends Compressor {
    public CompressorMaximizing(DataStream inputData, int compression) {
        super(inputData, compression);
    }

    @Override
    protected int getData(int index) {
        if (index == 0) return 0;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < compression; i++) {
            max = Math.max(max, inputData.get(index * compression + i));
        }
        return max;
    }
}

