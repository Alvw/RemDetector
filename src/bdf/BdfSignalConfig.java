package bdf;

/**
 *
 */
public class BdfSignalConfig {
    private final int digitalMin;
    private final int digitalMax;
    private final int physicalMin;
    private final int physicalMax;
    private final String physicalDimension;
    private final int numberOfSamplesInEachDataRecord;
    private String label;

    private BdfSignalConfig(Builder builder) {
        digitalMin = builder.digitalMin;
        digitalMax = builder.digitalMax;
        physicalMin = builder.physicalMin;
        physicalMax = builder.physicalMax;
        physicalDimension = builder.physicalDimension;
        numberOfSamplesInEachDataRecord = builder.numberOfSamplesInEachDataRecord;
        label = builder.label;
    }

    public static class Builder {
        private int digitalMin;
        private int digitalMax;
        private int physicalMin;
        private int physicalMax;
        private String physicalDimension;
        private int numberOfSamplesInEachDataRecord;
        private String label;

        public Builder setDigitalMin(int digitalMin) {
            this.digitalMin  =  digitalMin;
            return this;
        }

        public Builder setDigitalMax(int digitalMax) {
            this.digitalMax  =  digitalMax;
            return this;
        }

        public Builder setPhysicalMin(int physicalMin) {
            this.physicalMin  =  physicalMin;
            return this;
        }

        public Builder setPhysicalMax(int physicalMax) {
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

        public Builder setLabel(String label) {
            this.label  =  label;
            return this;
        }

        public BdfSignalConfig build() {
            return new BdfSignalConfig(this);
        }
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

    public int getPhysicalMin() {
        return physicalMin;
    }

    public int getPhysicalMax() {
        return physicalMax;
    }

    public String getPhysicalDimension() {
        return physicalDimension;
    }

    public int getNumberOfSamplesInEachDataRecord() {
        return numberOfSamplesInEachDataRecord;
    }
}
