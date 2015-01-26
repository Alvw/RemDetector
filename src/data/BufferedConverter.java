package data;

public class BufferedConverter implements DataSet {
    protected DataSet converter;
    protected DataList outputData = new DataList();

    public BufferedConverter(DataSet converter) {
        this.converter = converter;
    }


    @Override
    public int size() {
        return converter.size();
    }

    @Override
    public int get(int index) {
        if(outputData.size() > size()) {
            outputData.clear();
        }
        if (outputData.size() <= index) {
            for (int i = outputData.size(); i <= index; i++) {
                outputData.add(converter.get(i));
            }
        }
        return outputData.get(index);
    }

    @Override
    public double getFrequency() {
        return converter.getFrequency();
    }

    @Override
    public DataDimension getDataDimension() {
        return converter.getDataDimension();
    }

    @Override
    public long getStartTime() {
        return converter.getStartTime();
    }
}
