package bdf;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Al on 03.11.14.
 */
public class BdfConfig  {
    private String localPatientIdentification;
    private String localRecordingIdentification;
    private double durationOfDataRecord;    // in seconds
    private BdfSignalConfig[] signalsConfigList;
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

    public double getDurationOfDataRecord() {
        return durationOfDataRecord;
    }

    public void setDurationOfDataRecord(double durationOfDataRecord) {
        this.durationOfDataRecord = durationOfDataRecord;
    }

    public int getTotalNumberOfSamplesInEachDataRecord() {
        int result = 0;
        for (int signalNumber = 0; signalNumber < signalsConfigList.length; signalNumber++) {
            result += signalsConfigList[signalNumber].getNumberOfSamplesInEachDataRecord();
        }
        return result;
    }

    public int getNumberOfSignals() {
        return signalsConfigList.length;
    }

    public double[] getSignalsFrequencies() {
        double[] signalsFrequencies = new double[getNumberOfSignals()];
        for(int i = 0; i < getNumberOfSignals(); i++) {
            signalsFrequencies[i]  = signalsConfigList[i].getNumberOfSamplesInEachDataRecord()/ durationOfDataRecord;
        }
        return  signalsFrequencies;
    }

    public BdfSignalConfig[] getSignalsConfigList() {
        return signalsConfigList;
    }

    public void setSignalsConfigList(BdfSignalConfig[] signalsConfigList) {
        this.signalsConfigList = signalsConfigList;
    }
}
