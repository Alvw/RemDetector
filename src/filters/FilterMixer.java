package filters;

import data.DataSet;


public class FilterMixer extends Filter {
    DataSet inputData1;

    public FilterMixer(DataSet inputData, DataSet inputData1) {
        super(inputData);
        this.inputData1 = inputData1;
    }

    @Override
    public int get(int index) {
        double compression = inputData1.getFrequency() / inputData.getFrequency();
        int indexNew = Math.min(inputData1.size() - 1, (int) (compression * index));
        indexNew = (int) (compression * index);
        return inputData.get(index) * inputData1.get(indexNew);
    }
}
