// public class FilterTest {
package com.dream.Filters;

import com.dream.Data.DataStream;

/**
 *
 */
 // Среднее значение модуля за 8 периодов  (32 точки)
public class FilterTest_2 extends Filter {
    private int period = 4;
    private int bufferHalf = period * 4;

    public FilterTest_2(DataStream inputData) {
        super(inputData);
    }

    @Override
    protected int getData(int index) {
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
