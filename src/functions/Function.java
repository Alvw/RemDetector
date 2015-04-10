package functions;

import data.DataDimension;
import data.DataSeries;

/**
 *
 */
public abstract class Function implements DataSeries {
    protected DataSeries inputData;


    protected Function(DataSeries inputData) {
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

