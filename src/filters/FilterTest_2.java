// public class FilterTest {
package filters;

import data.DataSet;
import functions.Function;

/**
 *
 */
 // Среднее значение модуля за 8 периодов  (32 точки)
public class FilterTest_2 extends Function {
    private int period = 4;
    private int bufferHalf = period * 4;

    public FilterTest_2(DataSet inputData) {
        super(inputData);
    }

    @Override
    public int get(int index) {
        if (index < bufferHalf + 4 || (index >= size()- bufferHalf - 4)) {
            return 0;
        }

        int sum = 0;
        for (int i = -bufferHalf; i < bufferHalf; i += 4) {
//            sum += Math.abs(inputData.get(index + i - 1) - inputData.get(index + i + 1));
            sum += Math.abs(inputData.get(index + i));
        }
        return sum/(bufferHalf);

    }

}
