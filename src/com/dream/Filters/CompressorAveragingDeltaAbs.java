// CompressorAveragingDeltaAbs
package com.dream.Filters;

import com.dream.Data.DataStream;

/**
 *
 */
public class CompressorAveragingDeltaAbs extends Compressor {
    private int deltaNoiceLevel = 0;

    public CompressorAveragingDeltaAbs(DataStream inputData, int compression, int deltaNoiceLevel) {
        super(inputData, compression);
        this.deltaNoiceLevel = deltaNoiceLevel;
    }

    @Override
    protected int getData(int index) {
        if (index == 0) return 0;
        int sum = 0;
        for (int i = index * compression; i < (index + 1) * compression; i++) {
            sum += Math.abs(inputData.get(i));
        }
        int y =  sum / compression - deltaNoiceLevel;
//        if(y < 0){ y = 0; }
        return y;
    }


}
