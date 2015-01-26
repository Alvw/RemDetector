package graph;

import data.Converter;
import data.DataSet;

public class FrequencyConverter extends Converter {
    CompressionType compressionType = CompressionType.AVERAGE;

    public FrequencyConverter(DataSet inputData, double outputFrequency, CompressionType compressionType) {
        super(inputData, outputFrequency);
        this.compressionType = compressionType;
    }


    @Override
    public int get(int index) {
        long value = 0;
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
                    if(compressionType == CompressionType.AVERAGE) {
                        value += inputData.get(i);
                    }
                    else if(compressionType == CompressionType.MAX) {
                        value = Math.max(value, inputData.get(i));
                    }
                }
                if(compressionType == CompressionType.AVERAGE) {
                    value = value/(indexEnd - indexStart);
                }
            }
        }
        return (int)value;
    }
}
