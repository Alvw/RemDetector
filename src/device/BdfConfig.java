package device;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class BdfConfig implements Cloneable{
    private double durationOfADataRecord;    // in seconds
    private String localPatientIdentification = "Default patient";
    private String localRecordingIdentification = "Default recording";
    private List<BdfSignalConfig> signalsConfigList;
    private long startTime;
    private int numberOfBytesInSamples;

    public int getNumberOfBytesInSamples() {
        return numberOfBytesInSamples;
    }

    public void setNumberOfBytesInSamples(int numberOfBytesInSamples) {
        this.numberOfBytesInSamples = numberOfBytesInSamples;
    }

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
        return signalsConfigList.size();
    }

    public double[] getSignalsFrequencies() {
        double[] signalsFrequencies = new double[getNumberOfSignals()];
        for(int i = 0; i < getNumberOfSignals(); i++) {
            BdfSignalConfig signalConfig = signalsConfigList.get(i);
            signalsFrequencies[i]  = signalConfig.getNrOfSamplesInEachDataRecord()/durationOfADataRecord;
        }
        return  signalsFrequencies;
    }

    public List<BdfSignalConfig> getSignalsConfigList() {
        return signalsConfigList;
    }

    public void setSignalsConfigList(List<BdfSignalConfig> signalsConfigList) {
        this.signalsConfigList = signalsConfigList;
    }

    @Override
    public BdfConfig clone()  {
        try{
            BdfConfig configCopy = (BdfConfig) super.clone();
            List<BdfSignalConfig> signalsConfigListNew = new ArrayList<BdfSignalConfig>(signalsConfigList.size());
            for(BdfSignalConfig signalConfig : signalsConfigList) {
                signalsConfigListNew.add(signalConfig.clone());
            }
            configCopy.setSignalsConfigList(signalsConfigListNew);
            return configCopy;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
