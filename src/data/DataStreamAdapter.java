package data;

public class DataStreamAdapter implements DataStream {
    private DataSet dataSet;
    private int numberOfTakenElements = 0;

    public DataStreamAdapter(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public int available() {
        return dataSet.size() - numberOfTakenElements;
    }

    @Override
    public int getNext() {
        return dataSet.get(numberOfTakenElements++);
    }

    @Override
    public double getFrequency() {
        return dataSet.getFrequency();
    }

    @Override
    public long getStartTime() {
        return dataSet.getStartTime();
    }

    @Override
    public DataDimension getDataDimension() {
        return dataSet.getDataDimension();
    }
}
