package com.crostec.ads;

import java.util.List;

/**
 */
public class BdfConfig {
    private double durationOfADataRecord;
    private String localPatientIdentification = "Default patient";
    private String localRecordingIdentification = "Default recording";
    private long startTime;
    private int numberOfSignals;
    private List<BdfSignalConfig> signalConfigList;
    private int numberOfDataRecords;
    private String fileNameToSave;

    public String getFileNameToSave() {
        return fileNameToSave;
    }

    public void setFileNameToSave(String fileNameToSave) {
        this.fileNameToSave = fileNameToSave;
    }

    public int getNumberOfDataRecords() {
        return numberOfDataRecords;
    }

    public void setNumberOfDataRecords(int numberOfDataRecords) {
        this.numberOfDataRecords = numberOfDataRecords;
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

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getNumberOfSignals() {
        return numberOfSignals;
    }

    public void setNumberOfSignals(int numberOfSignals) {
        this.numberOfSignals = numberOfSignals;
    }

    public List<BdfSignalConfig> getSignalConfigList() {
        return signalConfigList;
    }

    public void setSignalConfigList(List<BdfSignalConfig> signalConfigList) {
        this.signalConfigList = signalConfigList;
    }
}
