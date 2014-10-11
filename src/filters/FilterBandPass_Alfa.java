package filters;

import data.DataStream;

/**
 *
 */
public class FilterBandPass_Alfa extends FilterBuffered {
    private int period = 4;
    private int bufferHalf = period * 4;
    public FilterBandPass_Alfa(DataStream inputData) {
        super(inputData);
    }

    @Override
    protected int getData(int index) {
        if ((index < bufferHalf + 2) || (index >= size()- bufferHalf - 2)) {
            return 0;
        }

        int sum = 0;
        for (int i = -bufferHalf; i < bufferHalf; i += 4) {
            sum += (inputData.get(index + i - 1) - inputData.get(index + i + 1));
        }
        return sum/(bufferHalf);
    }
}