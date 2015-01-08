package graph;

import data.Converter;
import data.DataSet;

public class FrequencyConverter extends Converter{

    public FrequencyConverter(DataSet inputData, double outputFrequency, boolean isBuffering) {
        super(inputData, outputFrequency, isBuffering);
    }

    public void setFrequency(double outputFrequency) {
        this.outputFrequency = outputFrequency;
        outputData.clear();
    }

    @Override
    protected int getConverted(int index) {
        int value = 0;
        double inputFrequency = inputData.getFrequency();
        if(outputFrequency == 0 || inputFrequency == 0 || inputFrequency == outputFrequency) {
            value = inputData.get(index);
        }
        else if(inputFrequency < outputFrequency) {
            int indexNew = (int)(inputFrequency * index / outputFrequency);
            value = inputData.get(indexNew);
        }
        else if(inputFrequency > outputFrequency) {
            if(index == 0) {
                value = inputData.get(index);
            }
            else {
                int indexStart = (int)((index-1) * inputFrequency / outputFrequency);
                int indexEnd = (int)(index * inputFrequency / outputFrequency);
                for(int i = indexStart; i < indexEnd; i++) {
                    value += inputData.get(i);
                }
                value = value/(indexEnd - indexStart);
            }
        }
        return value;
    }
}
