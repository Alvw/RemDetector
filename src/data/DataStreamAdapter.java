package data;

public class DataStreamAdapter implements DataStream {
    private DataSeries dataSeries;
    private int numberOfTakenElements = 0;

    public DataStreamAdapter(DataSeries dataSeries) {
        this.dataSeries = dataSeries;
    }

    @Override
    public int available() {
        return dataSeries.size() - numberOfTakenElements;
    }

    @Override
    public int getNext() {
        return dataSeries.get(numberOfTakenElements++);
    }

    @Override
    public double getFrequency() {
        return dataSeries.getFrequency();
    }

    @Override
    public long getStartTime() {
        return dataSeries.getStartTime();
    }

    @Override
    public DataDimension getDataDimension() {
        return dataSeries.getDataDimension();
    }
}
