package filters;

import data.DataStream;

/**
 *
 */
public class CompressorAveragingAbs extends Compressor {
    public CompressorAveragingAbs(DataStream inputData, int compression) {
        super(inputData, compression);
    }

    @Override
    protected int getData(int index) {
        if (index == 0) return 0;
        int sum = 0;
        for (int i = index * compression; i < (index + 1) * compression; i++) {
            sum += Math.abs(inputData.get(i));
        }
        return sum / compression;
    }


}
