package graph;

import data.DataDimension;
import data.DataSet;

public class FrequencyConverterRuntime implements FrequencyConverter {
    private DataSet inputData;
    private CompressionType compressionType;
    private double compression = 1;


    public FrequencyConverterRuntime(DataSet inputData, CompressionType compressionType) {
        this.inputData = inputData;
        this.compressionType = compressionType;
    }

    @Override
    public int get(int index) {
        long value = 0;
        if(compression == 1) {
            value = inputData.get(index);
        }
        else if(compression < 1) {
            int indexNew = Math.min(inputData.size() - 1, (int) (compression * index));
            value = inputData.get(indexNew);
        }
        else if(compression > 1) {
            if(index == 0) {
                value = inputData.get(index);
            }
            else {
                int indexStart =  (int)((index) * compression);
                int indexEnd = Math.min(inputData.size(), (int)((index+1) * compression));
                for(int i = indexStart; i < indexEnd; i++) {
                    if(inputData.get(i) == UNDEFINED) {
                        return UNDEFINED;
                    }
                    if(compressionType == CompressionType.AVERAGE || compressionType == CompressionType.SUM) {
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


    @Override
    public void setFrequency(double frequency) {
        if(inputData.getFrequency() > 0) {
            compression = inputData.getFrequency() /frequency;
        }
    }

    @Override
    public void setCompression(double compression) {
        this.compression = compression;
    }

    @Override
    public int size() {
        return (int)(inputData.size() / compression);
    }

    @Override
    public double getFrequency() {
        return inputData.getFrequency() / compression;
    }

    @Override
    public long getStartTime() {
        return inputData.getStartTime();
    }

    @Override
    public DataDimension getDataDimension() {
        return inputData.getDataDimension();
    }
}
