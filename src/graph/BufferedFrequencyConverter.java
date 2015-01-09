package graph;

import data.BufferedConverter;

public class BufferedFrequencyConverter extends BufferedConverter implements FrequencyConverter {
    public BufferedFrequencyConverter(FrequencyConverter converter) {
        super(converter);
    }

    @Override
    public void setFrequency(double outputFrequency) {
        FrequencyConverter frequencyConverter = (FrequencyConverter) converter;
        frequencyConverter.setFrequency(outputFrequency);
        outputData.clear();
    }
}
