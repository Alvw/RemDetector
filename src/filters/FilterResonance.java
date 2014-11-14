package filters;

import data.DataSet;
import tmp.Functions;

/**
 *
 */
public class FilterResonance extends Filter {
    private int period = 5;
    private int bufferHalf = period * 2;

    public FilterResonance(DataSet inputData) {
        super(inputData);
    }

    @Override
    protected int getData(int index) {

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
