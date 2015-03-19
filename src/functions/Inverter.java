package functions;

import data.DataSet;

public class Inverter extends Function {
    public Inverter(DataSet inputData) {
        super(inputData);
    }

    @Override
    public int get(int index) {
        return -inputData.get(index);
    }
}
