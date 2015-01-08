package data;

public abstract class Converter implements DataSet{
    protected DataSet inputData;
    protected DataList outputData = new DataList();
    protected double outputFrequency;
    protected boolean isBuffering;

    protected Converter(DataSet inputData) {
        this.inputData = inputData;
        outputFrequency = inputData.getFrequency();
    }

    protected Converter(DataSet inputData, double outputFrequency, boolean isBuffering) {
        this.inputData = inputData;
        this.outputFrequency = outputFrequency;
        this.isBuffering = isBuffering;
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

    @Override
    public int get(int index) {
        checkIndexBounds(index);
        if(!isBuffering) {
            return getConverted(index);
        }
        if(outputData.size() > size()) {
             outputData.clear();
        }
        if (outputData.size() <= index) {
            for (int i = outputData.size(); i <= index; i++) {
                outputData.add(getConverted(i));
            }
        }
        return outputData.get(index);

    }

    protected abstract int getConverted(int index);

    private void checkIndexBounds(int index) {
        if (index > size() || index < 0) {
            throw new IndexOutOfBoundsException("index:  " + index + " , available:  " + size());
        }
    }
}
