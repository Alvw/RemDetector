package filters;

import data.DataSet;

/**
 *
 */
public class CompressorAveragingAbs extends Compressor {
    public CompressorAveragingAbs(DataSet inputData, int compression) {
        super(inputData, compression);
    }

    @Override
    protected int getData(int index) {
        if (index == 0) return 0;
        long sum = 0;
        for (int i = index * compression; i < (index + 1) * compression; i++) {
            sum += Math.abs(inputData.get(i));
        }
        return (int)(sum / compression);
    }


}
