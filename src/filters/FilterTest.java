// public class FilterTest {
package filters;

import data.DataSeries;
import functions.Function;

/**
 *
 */

public class FilterTest extends Function {
    private int period = 4;
    private int bufferHalf = period * 2;

    public FilterTest(DataSeries inputData) {
        super(inputData);
    }

    @Override
    public int get(int index) {
        if (index < bufferHalf + 4 || (index >= size()- bufferHalf - 4)) {
            return 0;
        }

        double y = 0;
        for (int i = -bufferHalf; i < bufferHalf; i += 4) {
            y +=
                  inputData.get(index + i - 2) * inputData.get(index + i + 2)
               -  inputData.get(index + i - 1) * inputData.get(index + i + 1)
            ;

        }
        if(y < 0){ y = 0;}
        return (int)Math.sqrt(y);
    }

}
