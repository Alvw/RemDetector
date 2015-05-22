package functions;

import data.DataSeries;

/**
 *
 */

public class Abs extends Function {
    public Abs(DataSeries inputData) {
        super(inputData);
    }

    @Override
    public int get(int index) {
        return Math.abs(inputData.get(index));
    }
}
