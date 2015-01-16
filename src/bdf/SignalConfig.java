package bdf;

import data.DataDimension;

/**
 *
 */
public class SignalConfig {
    private final int numberOfSamplesInEachDataRecord;
    private final DataDimension dataDimension;
    private String prefiltering = "None";
    private String transducerType = "Unknown";
    private String label = "";

    public SignalConfig(int numberOfSamplesInEachDataRecord, DataDimension dataDimension) {
        this.numberOfSamplesInEachDataRecord = numberOfSamplesInEachDataRecord;
        this.dataDimension = dataDimension;
    }

    public int getNumberOfSamplesInEachDataRecord() {
        return numberOfSamplesInEachDataRecord;
    }

    public DataDimension getDataDimension() {
        return dataDimension;
    }

    public String getPrefiltering() {
        return prefiltering;
    }

    public void setPrefiltering(String prefiltering) {
        this.prefiltering = prefiltering;
    }

    public String getTransducerType() {
        return transducerType;
    }

    public void setTransducerType(String transducerType) {
        this.transducerType = transducerType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
