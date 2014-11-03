package device;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Al on 03.11.14.
 */
public class BdfConfig implements Cloneable {
    private String localPatientIdentification;
    private String localRecordingIdentification;
    private double durationOfADataRecord;    // in seconds
    private List<BdfSignalConfig> signalsConfigList;
    private long startTime;
    private int numberOfBytesInDataFormat; // edf - 2 bytes, bdf - 3 bytes


    public BdfConfig() {
         localPatientIdentification = "Default patient";
         localRecordingIdentification = "Default recording";
        // to do: read from property file
    }

    public void setLocalPatientIdentification(String localPatientIdentification) {
        this.localPatientIdentification = localPatientIdentification;
    }

    public String getLocalPatientIdentification() {
        return localPatientIdentification;
    }

    public String getLocalRecordingIdentification() {
        return localRecordingIdentification;
    }

    public void setLocalRecordingIdentification(String localRecordingIdentification) {
        this.localRecordingIdentification = localRecordingIdentification;
    }

    public int getNumberOfBytesInDataFormat() {
        return numberOfBytesInDataFormat;
    }

    public void setNumberOfBytesInDataFormat(int numberOfBytesInDataFormat) {
        this.numberOfBytesInDataFormat = numberOfBytesInDataFormat;
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
