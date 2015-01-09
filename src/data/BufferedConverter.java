package data;

public class BufferedConverter implements DataSet {
    protected Converter converter;
    protected DataList outputData = new DataList();

    public BufferedConverter(Converter converter) {
        this.converter = converter;
    }

    public void setFrequency(double outputFrequency) {
        converter.setFrequency(outputFrequency);
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
}
