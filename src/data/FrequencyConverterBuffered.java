package data;

public class FrequencyConverterBuffered implements FrequencyConverter {
    FrequencyConverterRuntime inputData;
    protected DataList outputData = new DataList();

    public FrequencyConverterBuffered(FrequencyConverterRuntime inputData) {
        this.inputData = inputData;
    }

    @Override
    public int get(int index) {
        if(outputData.size() > size()) {
            outputData = new DataList();
        }
        if (outputData.size()  <= index) {
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


    @Override
    public void setCompression(double compression) {
        inputData.setCompression(compression);
        outputData = new DataList();
    }

    @Override
    public void setFrequency(double frequency) {
        inputData.setFrequency(frequency);
        outputData = new DataList();
    }
}
