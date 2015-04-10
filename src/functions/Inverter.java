package functions;

import data.DataSeries;

public class Inverter extends Function {
    public Inverter(DataSeries inputData) {
        super(inputData);
    }

    @Override
    public int get(int index) {
        return -inputData.get(index);
    }
}
