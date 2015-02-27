package data;

/**
 * Created by mac on 27/02/15.
 */
class DataSetDoubleArrayWrapper implements DataSet {
    private double[] dataArray;

    public DataSetDoubleArrayWrapper(double[] dataArray) {
        this.dataArray = dataArray;
    }

    @Override
    public int size() {
        return dataArray.length;
    }

    @Override
    public int get(int index) {
        return (int)dataArray[index];
    }

    @Override
    public double getFrequency() {
        return 0;
    }

    @Override
    public long getStartTime() {
        return 0;
    }

    @Override
    public DataDimension getDataDimension() {
        return new DataDimension();
    }
}
