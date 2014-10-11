package filters;

import data.DataStream;

/**
 *
 */
public class Multiplexer extends Filter {
    protected final DataStream selectorData;

    public Multiplexer(DataStream inputData, DataStream selectorData) {
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
