package filters;

import data.DataSet;

public class FilterLimit extends Filter {
    DataSet inputData1;

    public FilterLimit(DataSet inputData, DataSet inputData1) {
        super(inputData);
        this.inputData1 = inputData1;
    }

    @Override
    public int get(int index) {
        double compression = inputData1.getFrequency() / inputData.getFrequency();
        int indexNew = (int) (compression * index);
        return inputData.get(index) * inputData1.get(indexNew);
    }
}
