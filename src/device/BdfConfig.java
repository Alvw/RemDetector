package device;

import java.util.List;

/**
 */
public class BdfConfig {
    private double durationOfADataRecord;    // in seconds
    private String localPatientIdentification = "Default patient";
    private String localRecordingIdentification = "Default recording";
    private List<BdfSignalConfig> signalConfigList;
    private long startTime;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public double getDurationOfADataRecord() {
        return durationOfADataRecord;
    }

    public void setDurationOfADataRecord(double durationOfADataRecord) {
        this.durationOfADataRecord = durationOfADataRecord;
    }

    public String getLocalPatientIdentification() {
        return localPatientIdentification;
    }

    public void setLocalPatientIdentification(String localPatientIdentification) {
        this.localPatientIdentification = localPatientIdentification;
    }

    public String getLocalRecordingIdentification() {
        return localRecordingIdentification;
    }

    public void setLocalRecordingIdentification(String localRecordingIdentification) {
        this.localRecordingIdentification = localRecordingIdentification;
    }

    public int getNumberOfSignals() {
        return signalConfigList.size();
    }

    public double[] getSignalsFrequencies() {
        double[] signalsFrequencies = new double[getNumberOfSignals()];
        for(int i = 0; i < getNumberOfSignals(); i++) {
            BdfSignalConfig signalConfig = signalConfigList.get(i);
            signalsFrequencies[i]  = signalConfig.getNrOfSamplesInEachDataRecord()/durationOfADataRecord;
        }
        return  signalsFrequencies;
    }

    public List<BdfSignalConfig> getSignalConfigList() {
        return signalConfigList;
    }

    public void setSignalConfigList(List<BdfSignalConfig> signalConfigList) {
        this.signalConfigList = signalConfigList;
    }
}
