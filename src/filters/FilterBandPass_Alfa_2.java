package filters;

import data.DataSeries;
import functions.Function;

/**
 *
 */
// 2-я производная + FilterBandPass_Alfa
public class FilterBandPass_Alfa_2 extends Function {
    private int period = 4;
    private int bufferHalf = period * 4;

    public FilterBandPass_Alfa_2(DataSeries inputData) {
        super(inputData);
    }

    @Override
    public int get(int index) {
        if (((index < bufferHalf + 2) || (index >= size()- bufferHalf - 2))) {
            return 0;
        }

        int sum = 0;
        int y;
        for (int i = -bufferHalf; i < bufferHalf; i += 4) {

             y =
                   inputData.get(index + i - 2)
               - 2*inputData.get(index + i - 1)
               + 2*inputData.get(index + i + 1)
               -   inputData.get(index + i + 2)
             ;
            sum += y;
        }
        return sum/(bufferHalf);

    }
}