package filters;

import data.DataSet;

/**
 *
 */
public class CompressorAveraging extends Compressor {
    public CompressorAveraging(DataSet inputData, int compression) {
        super(inputData, compression);
    }

    @Override
    protected int getData(int index) {
        if (index == 0) return 0;
        int sum = 0;
        for (int i = index * compression; i < (index + 1) * compression; i++) {
            if(inputData.get(i) == STAND) {
                return STAND;
            }
            else if(inputData.get(i) == MOVE) {
                 return MOVE;
            }
            if (inputData.get(i) != UNDEFINED)
            sum += inputData.get(i);
        }
        return sum / compression;
    }


}
