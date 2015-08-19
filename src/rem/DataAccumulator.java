package rem;

import data.DataList;
import data.DataSeries;
import data.Scaling;


/**
 * Created by mac on 29/06/15.
 */
public class DataAccumulator implements DataSeries {
    private NoiseDetector inputData;
    private DataList outputData = new DataList();
    private int multiplexer;


    public DataAccumulator(NoiseDetector inputData, int multiplexer) {
        this.inputData = inputData;
        this.multiplexer = multiplexer;

    }

    @Override
    public int get(int index) {
       return outputData.get(index);
    }

    @Override
    public int size() {
        accumulate();
        return outputData.size();
    }

    @Override
    public Scaling getScaling() {
        return inputData.getScaling();
    }

    private void accumulate() {
        while(inputData.isAvailable()) {
            outputData.add(inputData.getNext() * multiplexer);
        }
    }
}
