package device.implementation.impl2ch;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BdfHeaderData {

    private String fileNameToSave;
    private AdsConfiguration adsConfiguration;
    private String patientIdentification = "Default patient";
    private String recordingIdentification = "Default record";
    private long startRecordingTime;
    private double durationOfDataRecord = 1.0;  // duration of EDF data record (in seconds)
    private int numberOfDataRecords = -1;
    List<String> adsChannelNames = new ArrayList<String>();
    List<String> accelerometerChannelNames = new ArrayList<String>();

    public BdfHeaderData(AdsConfiguration adsConfiguration) {
        this.adsConfiguration = adsConfiguration;
        for (int i = 0; i < adsConfiguration.getDeviceType().getNumberOfAdsChannels(); i++) {
              adsChannelNames.add("Channel " + (i+1));
        }
        accelerometerChannelNames.add("Accelerometer X");
        accelerometerChannelNames.add("Accelerometer Y");
        accelerometerChannelNames.add("Accelerometer Z");
    }

    public String getFileNameToSave() {
        return fileNameToSave;
    }

    public void setFileNameToSave(String fileNameToSave) {
        this.fileNameToSave = fileNameToSave;
    }

    public List<String> getAdsChannelNames() {
        return adsChannelNames;
    }

    public List<String> getAccelerometerChannelNames() {
        return accelerometerChannelNames;
    }

    public double getDurationOfDataRecord() {
        return durationOfDataRecord;
    }

    public void setDurationOfDataRecord(double durationOfDataRecord) {
        this.durationOfDataRecord = durationOfDataRecord;
    }

    public int getNumberOfDataRecords() {
        return numberOfDataRecords;
    }

    public void setNumberOfDataRecords(int numberOfDataRecords) {
        this.numberOfDataRecords = numberOfDataRecords;
    }

    public long getStartRecordingTime() {
        return startRecordingTime;
    }

    public void setStartRecordingTime(long startRecordingTime) {
        this.startRecordingTime = startRecordingTime;
    }

    public AdsConfiguration getAdsConfiguration() {
        return adsConfiguration;
    }

    public void setAdsConfiguration(AdsConfiguration adsConfiguration) {
        this.adsConfiguration = adsConfiguration;
    }

    public String getPatientIdentification() {
        return patientIdentification;
    }

    public void setPatientIdentification(String patientIdentification) {
        this.patientIdentification = patientIdentification;
    }

    public String getRecordingIdentification() {
        return recordingIdentification;
    }

    public void setRecordingIdentification(String recordingIdentification) {
        this.recordingIdentification = recordingIdentification;
    }


}
