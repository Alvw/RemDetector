package functions;

import data.DataSeries;
import data.Scaling;

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
    public Scaling getScaling() {
        return inputData.getScaling();
    }
}

