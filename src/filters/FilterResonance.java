package filters;

import data.DataSeries;
import functions.Function;
import tmp.Functions;

/**
 *
 */
public class FilterResonance extends Function {
    private int period = 5;
    private int bufferHalf = period * 2;

    public FilterResonance(DataSeries inputData) {
        super(inputData);
    }

    @Override
    public int get(int index) {

        if (index < bufferHalf || (index >= size()- bufferHalf)) {
            return 0;
        }

        int sum = 0;
        for (int i = -bufferHalf; i < bufferHalf; i++) {
            sum += inputData.get(index + i)* Functions.getTriangle(i, period);
        }
       return sum/(2 * bufferHalf * Functions.SCALE);
    }
}
