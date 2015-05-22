package filters;

import data.DataSeries;
import functions.Function;

/**
 *
 */
public class FilterBandPass_Alfa extends Function {
    private int period = 4;
    private int bufferHalf = period * 4;
    public FilterBandPass_Alfa(DataSeries inputData) {
        super(inputData);
    }

    @Override
    public int get(int index) {
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