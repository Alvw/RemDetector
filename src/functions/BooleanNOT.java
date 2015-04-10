package functions;

import data.DataSeries;

public class BooleanNOT extends Function {
    public BooleanNOT(DataSeries inputData) {
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
