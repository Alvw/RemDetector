package bdf;


/**
 * Created by Al on 03.11.14.
 */
public class RecordingBdfConfig extends DeviceBdfConfig implements BdfConfig {

    private long startTime = -1;
    private String patientIdentification;
    private String recordingIdentification;
    private int numberOfDataRecords = -1;

    public RecordingBdfConfig(double durationOfDataRecord, int numberOfBytesInDataFormat, SignalConfig... signalsConfigList) {
        super(durationOfDataRecord, numberOfBytesInDataFormat, signalsConfigList);
    }

    public RecordingBdfConfig(DeviceBdfConfig deviceBdfConfig) {
        this(deviceBdfConfig.getDurationOfDataRecord(), deviceBdfConfig.getNumberOfBytesInDataFormat(), deviceBdfConfig.getSignalsConfigList());
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
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

    public int getNumberOfDataRecords() {
        return numberOfDataRecords;
    }

    public void setNumberOfDataRecords(int numberOfDataRecords) {
        this.numberOfDataRecords = numberOfDataRecords;
    }

}
