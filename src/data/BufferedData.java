package data;

public class BufferedData implements DataSet {
    protected DataStream inputData;
    protected DataList outputData = new DataList();

    public BufferedData(DataStream inputData) {
        this.inputData = inputData;
    }

    public BufferedData(DataSet inputData) {
        this.inputData = new DataStreamAdapter(inputData);
    }


    @Override
    public int get(int index) {
        if (outputData.size()  <= index) {
            for (int i = outputData.size(); i <= index; i++) {
                outputData.add(inputData.getNext());
            }
        }
        return outputData.get(index);
    }


    @Override
    public int size() {
        return outputData.size() + inputData.available();
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
