package filters;

import data.DataList;
import data.DataSeries;
import data.Scaling;
import data.ScalingImpl;

public class HiPassCollectingFilter implements DataSeries {
    private DataSeries inputData;
    private DataList outputData;
    int bufferSize;
    private int counter;
    private long sum;

    public HiPassCollectingFilter(DataSeries inputData, double cutOffInterval ) {
        this.inputData = inputData;
        outputData = new DataList();

        double samplingInterval = 1;
        if(inputData.getScaling() != null) {
            samplingInterval = inputData.getScaling().getSamplingInterval();
        }
        bufferSize = (int)(cutOffInterval / samplingInterval);
        ScalingImpl scaling = new ScalingImpl(inputData.getScaling());
        if(bufferSize > 0) {
            scaling.setDataOffset(0);
        }
        outputData.setScaling(scaling);
        collectData();
    }

    public int getNext() {
        if(bufferSize == 0) {
            return inputData.get(counter++);
        }
        if (counter <= bufferSize) {
            sum += inputData.get(counter);
            return inputData.get(counter++) - (int) (sum / (counter));
        }
        else {
            sum += inputData.get(counter) - inputData.get(counter - bufferSize - 1);
        }

        return inputData.get(counter++) - (int) (sum / (bufferSize+1));
    }

    private void collectData() {
        if (outputData.size()  < inputData.size()) {
            for (int i = outputData.size(); i < inputData.size(); i++) {
                outputData.add(getNext());
            }
        }
    }

    @Override
    public int get(int index) {
        collectData();
        return outputData.get(index);
    }


    @Override
    public int size() {
        collectData();
        return outputData.size();
    }

    @Override
    public Scaling getScaling() {
        return outputData.getScaling();
    }


}
