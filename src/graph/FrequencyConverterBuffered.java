package graph;

import data.BufferedConverter;

public class FrequencyConverterBuffered extends BufferedConverter implements FrequencyConverter {
    public FrequencyConverterBuffered(FrequencyConverter converter) {
        super(converter);
    }

    @Override
    public void setFrequency(double outputFrequency) {
        FrequencyConverter frequencyConverter = (FrequencyConverter) converter;
        frequencyConverter.setFrequency(outputFrequency);
        outputData.clear();
    }
}
