package filters;

import data.DataSet;

/**
 *
 */
public class Multiplexer extends Filter {
    protected final DataSet selectorData;

    public Multiplexer(DataSet inputData, DataSet selectorData) {
        super(inputData);
        this.selectorData = selectorData;
    }

    @Override
    protected int getData(int index) {
        if (selectorData.get(index) == 0) {
            return inputData.get(index);
        }
        else {
          return Integer.MAX_VALUE - 100;
        }
    }
}
