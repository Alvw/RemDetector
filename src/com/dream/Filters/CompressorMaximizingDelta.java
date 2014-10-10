// CompressorMaximizingDelta
package com.dream.Filters;

import com.dream.Data.DataStream;

/**
 *
 */

public class CompressorMaximizingDelta extends Compressor {
    private int deltaNoiceLevel = 35;
    public CompressorMaximizingDelta(DataStream inputData, int compression) {
        super(inputData, compression);
    }

    @Override
    protected int getData(int index) {
        if (index == 0) return 0;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < compression; i++) {
            max = Math.max(max, inputData.get(index * compression + i));
        }

        int y =  max - deltaNoiceLevel;
        if(y < 0){ y = 0; }
        return y;
    }
}

