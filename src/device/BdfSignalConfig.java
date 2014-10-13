package device;

/**
 *
 */
public class BdfSignalConfig {
    private String label;
    private int digitalMin;
    private int digitalMax;
    private int physicalMin;
    private int physicalMax;
    private String physicalDimension;
    private int nrOfSamplesInEachDataRecord;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getDigitalMin() {
        return digitalMin;
    }

    public void setDigitalMin(int digitalMin) {
        this.digitalMin = digitalMin;
    }

    public int getDigitalMax() {
        return digitalMax;
    }

    public void setDigitalMax(int digitalMax) {
        this.digitalMax = digitalMax;
    }

    public int getPhysicalMin() {
        return physicalMin;
    }

    public void setPhysicalMin(int physicalMin) {
        this.physicalMin = physicalMin;
    }

    public int getPhysicalMax() {
        return physicalMax;
    }

    public void setPhysicalMax(int physicalMax) {
        this.physicalMax = physicalMax;
    }

    public String getPhysicalDimension() {
        return physicalDimension;
    }

    public void setPhysicalDimension(String physicalDimension) {
        this.physicalDimension = physicalDimension;
    }

    public int getNrOfSamplesInEachDataRecord() {
        return nrOfSamplesInEachDataRecord;
    }

    public void setNrOfSamplesInEachDataRecord(int nrOfSamplesInEachDataRecord) {
        this.nrOfSamplesInEachDataRecord = nrOfSamplesInEachDataRecord;
    }
}
