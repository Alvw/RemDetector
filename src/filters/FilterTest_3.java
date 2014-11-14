// public class FilterTest {
package filters;

import data.DataSet;

/**
 *
 */
// Максимальное значение  модуля за 8 периодов  (32 точки)
// Максимальное значение  модуля за 2 периодов  (8 точки)
public class FilterTest_3 extends Filter {
    private int period = 1;
    private int bufferHalf = period * 4;

    public FilterTest_3(DataSet inputData) {
        super(inputData);
    }

    @Override
    protected int getData(int index) {
        if (index < bufferHalf + 5 || (index >= size()- bufferHalf - 5)) {
            return 0;
        }

//        int max1 = 0;
//        for (int i = -bufferHalf; i < bufferHalf; i += 4) {
//            max1 = Math.max(max1,
//                  Math.abs(inputData.get(index + i - 2)));
//        }
//
//        int max2 = 0;
//        for (int i = -bufferHalf; i < bufferHalf; i += 4) {
//            max2 = Math.max(max2, Math.abs(inputData.get(index + i - 1)));
//        }

//        int y1 = Math.max(max1, max2);
        int max3 = 0;
        for (int i = -bufferHalf; i < bufferHalf; i += 1) {
            max3 = Math.max(max3, Math.abs(inputData.get(index + i )));
        }

//        int y2 = Math.max(y1, max3);
//        int max4 = 0;
//        for (int i = -bufferHalf; i < bufferHalf; i += 4) {
//            max4 = Math.max(max4, Math.abs(inputData.get(index + i + 1)));
//        }

        return max3;

    }

}
