package data;

public abstract class Converter implements DataSet{
    protected DataSet inputData;
    protected double outputFrequency;

    protected Converter(DataSet inputData) {
        this.inputData = inputData;
        outputFrequency = inputData.getFrequency();
    }


    protected Converter(DataSet inputData, double outputFrequency) {
        this(inputData);
        this.outputFrequency = outputFrequency;
    }

    @Override
    public int size() {
        int size = inputData.size();
        double inputFrequency = inputData.getFrequency();
        if(outputFrequency != inputFrequency && outputFrequency > 0 && inputFrequency > 0) {
            size = (int)(size * outputFrequency / inputFrequency );
        }
        return size;
    }

    @Override
    public double getFrequency() {
        return outputFrequency;
    }
}
