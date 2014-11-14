package filters;

import data.DataSet;

/**
 *
 */
public abstract class Filter implements DataSet {
    protected final DataSet inputData;
    protected  int bufferSize = 0;

    public Filter(DataSet inputData) {
        this.inputData = inputData;
    }

    protected abstract int getData(int index);

    @Override
    public double getFrequency() {
        return inputData.getFrequency();
    }

    public int size() {
        return inputData.size();
    }


    public int get(int index) {
        checkIndexBounds(index);
        if(index < bufferSize) {
            return UNDEFINED;
        }
        if(index >= size() - bufferSize) {
            return UNDEFINED;
        }
        for(int i = index - bufferSize; i<= index + bufferSize; i++) {
            if(inputData.get(i) == UNDEFINED) {
                return UNDEFINED;
            }
        }
        return getData(index);
    }

    private void checkIndexBounds(int index){
        if(index > size() || index < 0 ){
            throw  new IndexOutOfBoundsException("index:  "+index+", available:  "+size());
        }
    }
}
