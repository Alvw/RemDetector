package data;

/**
 * Class is designed to collect/store/cache a computed input data  and to give quick access to them.
 * Input data could be a filter, function and so on
 */

public class DataCollector implements DataSeries {
    protected DataSeries inputData;
    protected DataList outputData;

    public DataCollector(DataSeries inputData) {
        this.inputData = inputData;
        outputData = new DataList();
        collectData();
    }


    private void collectData() {
        if (outputData.size()  < inputData.size()) {
            for (int i = outputData.size(); i < inputData.size(); i++) {
                outputData.add(inputData.get(i));
            }
        }
    }

    @Override
    public int get(int index) {
        collectData();
        return outputData.get(index);
    }


    @Override
    public int size() {
        collectData();
        return outputData.size();
    }

    @Override
    public Scaling getScaling() {
        return inputData.getScaling();
    }
}
