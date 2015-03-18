package data;

public class FrequencyConverterBuffered extends BufferedData implements FrequencyConverter {
    public FrequencyConverterBuffered(FrequencyConverterRuntime frequencyConverterRuntime) {
        super(frequencyConverterRuntime);
    }

    @Override
    public void setCompression(double compression) {
        FrequencyConverterRuntime frequencyConverterRuntime = (FrequencyConverterRuntime) inputData;
        frequencyConverterRuntime.setCompression(compression);
        outputData = new DataList();
    }

    @Override
    public void setFrequency(double frequency) {
        FrequencyConverterRuntime frequencyConverterRuntime = (FrequencyConverterRuntime) inputData;
        frequencyConverterRuntime.setFrequency(frequency);
        outputData = new DataList();
    }
}
