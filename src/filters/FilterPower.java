package filters;

import data.DataSeries;
import functions.Function;

/**
 * Based on Teager Kaiser Energy operator :
 * E_TKE =  x(n)*x(n) - x(n-1) * x(n+1)
 *
 */

public class FilterPower extends Function {

    private int  distance = 1;

    public FilterPower(DataSeries inputData, int distance) {
        super(inputData);
        this.distance = distance;
    }

    public FilterPower(DataSeries inputData) {
        this(inputData, 1);
    }

    @Override
    public int get(int index) {
        if(index < distance || index >= (size()-distance)) {
            return 0;
        }
        int y = inputData.get(index);
        int y_before = inputData.get(index - distance);
        int y_after = inputData.get(index + distance);

        return (int)Math.sqrt(Math.abs(y*y - y_before * y_after));
    }

    @Override
    public int size() {
        return inputData.size() - distance;
    }
}

