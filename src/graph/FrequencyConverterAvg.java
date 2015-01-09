package graph;

import data.Converter;
import data.DataSet;

public class FrequencyConverterAvg extends Converter implements FrequencyConverter{
    public FrequencyConverterAvg(DataSet inputData, double outputFrequency) {
        super(inputData, outputFrequency);
    }

    @Override
    public void setFrequency(double outputFrequency) {
        this.outputFrequency = outputFrequency;
    }

    @Override
    public int get(int index) {
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
