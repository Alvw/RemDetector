package data;

public class FrequencyConverterRuntime implements FrequencyConverter {
    private DataSeries inputData;
    private CompressionType compressionType;
    private double compression = 1;

    public FrequencyConverterRuntime(DataSeries inputData, CompressionType compressionType, double compression) {
        this.inputData = inputData;
        this.compressionType = compressionType;
        this.compression = compression;
    }


    public FrequencyConverterRuntime(DataSeries inputData, CompressionType compressionType) {
       this(inputData, compressionType, 1);
    }

    @Override
    public int get(int index) {

        if(compression == 1) {
            return inputData.get(index);
        }
        if(compression < 1) {
            int indexNew = Math.min(inputData.size() - 1, (int) (compression * index));
            return inputData.get(indexNew);
        }

        long result = 0;
        int indexStart =  (int)((index) * compression);
        int indexEnd = Math.min(inputData.size(), (int)((index+1) * compression));
        for(int i = indexStart; i < indexEnd; i++) {
            if(compressionType == CompressionType.BOOLEAN) {
                if(inputData.get(i) == 0) {
                    return 0;
                }
            }
            else if(compressionType == CompressionType.MAX) {
                result = Math.max(result, Math.abs(inputData.get(i)));
            }
            else if(compressionType == CompressionType.AVERAGE || compressionType == CompressionType.SUM) {
                result += inputData.get(i);
            }
        }
        if(compressionType == CompressionType.AVERAGE) {
            result = result/(indexEnd - indexStart);
        }
        if(compressionType == CompressionType.BOOLEAN) {
            result = 1;
        }
        return (int)result;
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
