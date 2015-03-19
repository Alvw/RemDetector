package functions;

import data.DataSet;

public class BooleanNOT extends Function {
    public BooleanNOT(DataSet inputData) {
        super(inputData);
    }

    @Override
    public int get(int index) {
        if(inputData.get(index) == 0) {
            return 1;
        }
        return 0;
    }
}
