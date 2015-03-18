package functions;

import data.DataDimension;
import data.DataSet;

/**
 *
 */
public abstract class Function implements DataSet {
    protected DataSet inputData;


    protected Function(DataSet inputData) {
        this.inputData = inputData;
    }

    @Override
    public int size() {
        return inputData.size();
    }

    @Override
    public double getFrequency() {
        return inputData.getFrequency();
    }

    @Override
    public DataDimension getDataDimension() {
        return inputData.getDataDimension();
    }

    @Override
    public long getStartTime() {
        return inputData.getStartTime();
    }
}

