package data;

public class BufferedData implements DataSet {
    protected DataSet inputData;
    protected DataList outputData = new DataList();

    public BufferedData(DataSet inputData) {
        this.inputData = inputData;
    }


    @Override
    public int get(int index) {
        if(outputData.size() > size()) {
            outputData.clear();
        }
        if (outputData.size() <= index) {
            for (int i = outputData.size(); i <= index; i++) {
                outputData.add(inputData.get(i));
            }
        }
        return outputData.get(index);
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
