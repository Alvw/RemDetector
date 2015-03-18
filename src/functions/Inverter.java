package functions;

import data.DataSet;

public class Inverter extends Function {
    public Inverter(DataSet inputData) {
        super(inputData);
    }

    @Override
    public int get(int index) {
        if(inputData.get(index) == DataSet.TRUE) {
            return DataSet.FALSE;
        }
        if(inputData.get(index) == DataSet.FALSE) {
            return DataSet.TRUE;
        }
        return -inputData.get(index);
    }
}
