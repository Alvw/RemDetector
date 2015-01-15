package filters;

import data.Converter;
import data.DataSet;
import tmp.Functions;

/**
 *
 */
public class FilterBandPass_Delta extends Converter {
    private int bufferHalf_1 =  64;
    private int bufferHalf_2 =   4;
    private int bufferHalf_Max = Math.max(bufferHalf_1, bufferHalf_2);

    private int bufferHalf =  16;
    private int period = bufferHalf * 2;

    public FilterBandPass_Delta(DataSet inputData) {
        super(inputData);
    }

    @Override
    public int get(int index) {

        if (index < bufferHalf_Max || (index >= size()- bufferHalf_Max)) {
            return 0;
        }

        int sum_1 = inputData.get(index);
        for (int i = 1; i <= bufferHalf_1; i++) {
            sum_1 += (inputData.get(index - i)) + inputData.get(index + i);
        }

        int sum_2 = inputData.get(index);
        for (int i = 1; i <= bufferHalf_2; i++) {
            sum_2 += (inputData.get(index - i)) + inputData.get(index + i);
        }

        int sum_3 = 0;
        for (int i = -bufferHalf; i < bufferHalf; i++) {
            sum_3 += inputData.get(index - i)* Functions.getTriangle(i, period);
        }
          return sum_2/(2*bufferHalf_2 + 1) - sum_1/(2*bufferHalf_1 + 1)
            +((sum_3/(2*bufferHalf * Functions.SCALE))*60)/100;

    }
}
