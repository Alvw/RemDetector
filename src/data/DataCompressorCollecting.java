package data;

public class DataCompressorCollecting extends DataCollector{

    public DataCompressorCollecting(DataSeries inputData, CompressionType compressionType, int compression) {
        super(new DataCompressor(inputData, compressionType, compression));
    }

    public DataCompressorCollecting(DataSeries inputData, CompressionType compressionType) {
        super(new DataCompressor(inputData, compressionType));
    }

    public void setCompression(double compression) {
        DataCompressor compressedInput = (DataCompressor) inputData;
        compressedInput.setCompression(compression);
        outputData = new DataList();
    }

    public void setSamplingRate(double samplingRate) {
        DataCompressor compressedInput = (DataCompressor) inputData;
        compressedInput.setSamplingRate(samplingRate);
        outputData = new DataList();
    }
}
