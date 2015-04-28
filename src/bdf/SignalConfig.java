package bdf;

/**
 *
 */
public class SignalConfig {
    private final int numberOfSamplesInEachDataRecord;
    private final Calibration calibration;
    private String prefiltering = "None";
    private String transducerType = "Unknown";
    private String label = "";

    public SignalConfig(int numberOfSamplesInEachDataRecord, Calibration calibration) {
        this.numberOfSamplesInEachDataRecord = numberOfSamplesInEachDataRecord;
        this.calibration = calibration;
    }

    public int getNumberOfSamplesInEachDataRecord() {
        return numberOfSamplesInEachDataRecord;
    }

    public Calibration getCalibration() {
        return calibration;
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
