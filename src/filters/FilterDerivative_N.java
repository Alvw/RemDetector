package filters;

import data.DataSeries;
import functions.Function;

/**
 *
 */

public class FilterDerivative_N extends Function {
    private int n = 1;
    public FilterDerivative_N(DataSeries inputData, int n) {
        super(inputData);
        this.n = n;
    }

    @Override
    public int get(int index) {
        if (index < 2 || (index >= size()- 2)) {
            return 0;
        }
        int y = 0;
        int y1 = inputData.get(index - 2);
        int y2 = inputData.get(index - 1);
        int y3 = inputData.get(index    );
        int y4 = inputData.get(index + 1);
        int y5 = inputData.get(index + 2);

        switch (n){
            case 1: y =        -y2 +   y3            ; break;
            case 2: y =         y2 - 2*y3 +   y4     ; break;
            case 3: y = -y1 + 3*y2 - 3*y3 +   y4     ; break;
            case 4: y =  y1 - 4*y2 + 6*y3 - 4*y4 + y5; break;
        }

        return y;
    }

}