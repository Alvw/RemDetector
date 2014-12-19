package bdf;

/**
 *
 */
public class SignalConfig {
    private final int digitalMin;
    private final int digitalMax;
    private final double physicalMin;
    private final double physicalMax;
    private final String physicalDimension;
    private final int numberOfSamplesInEachDataRecord;
    private final String prefiltering;
    private String transducerType = "Unknown";
    private String label = "";

    private SignalConfig(Builder builder) {
        digitalMin = builder.digitalMin;
        digitalMax = builder.digitalMax;
        physicalMin = builder.physicalMin;
        physicalMax = builder.physicalMax;
        physicalDimension = builder.physicalDimension;
        numberOfSamplesInEachDataRecord = builder.numberOfSamplesInEachDataRecord;
        prefiltering = builder.prefiltering;
        transducerType = builder.transducerType;
        label = builder.label;
    }

    public static class Builder {
        private int digitalMin;
        private int digitalMax;
        private double physicalMin;
        private double physicalMax;
        private String physicalDimension;
        private int numberOfSamplesInEachDataRecord;
        private String prefiltering;
        private String transducerType;
        private String label;

        public Builder setDigitalMin(int digitalMin) {
            this.digitalMin  =  digitalMin;
            return this;
        }

        public Builder setDigitalMax(int digitalMax) {
            this.digitalMax  =  digitalMax;
            return this;
        }

        public Builder setPhysicalMin(double physicalMin) {
            this.physicalMin  =  physicalMin;
            return this;
        }

        public Builder setPhysicalMax(double physicalMax) {
            this.physicalMax  =  physicalMax;
            return this;
        }

        public Builder setNumberOfSamplesInEachDataRecord(int numberOfSamplesInEachDataRecord) {
            this.numberOfSamplesInEachDataRecord  =  numberOfSamplesInEachDataRecord;
            return this;
        }

        public Builder setPhysicalDimension(String physicalDimension) {
            this.physicalDimension  =  physicalDimension;
            return this;
        }

        public Builder setPrefiltering(String prefiltering) {
            this.prefiltering  =  prefiltering;
            return this;
        }

        public Builder setTransducerType(String transducerType) {
            this.transducerType  =  transducerType;
            return this;
        }

        public Builder setLabel(String label) {
            this.label  =  label;
            return this;
        }

        public SignalConfig build() {
            return new SignalConfig(this);
        }
    }

    public String getPrefiltering() {
        return prefiltering;
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

    public int getDigitalMin() {
        return digitalMin;
    }

    public int getDigitalMax() {
        return digitalMax;
    }

    public double getPhysicalMin() {
        return physicalMin;
    }

    public double getPhysicalMax() {
        return physicalMax;
    }

    public String getPhysicalDimension() {
        return physicalDimension;
    }

    public int getNumberOfSamplesInEachDataRecord() {
        return numberOfSamplesInEachDataRecord;
    }
}
