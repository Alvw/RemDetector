package dreamrec;

public class ExperimentInfo {
    private long startTime;
    private String localPatientIdentification;
    private String localRecordIdentification;
    private int numberOfDataRecords;
    // if BdfProvide(device) don't have quartz we can activate
    // Frequency Adjustment (i.e. durationOfDataRecord adjustment)
    // when writing experiment results to Bdf file
    private double durationOfDataRecord;



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

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getLocalPatientIdentification() {
        return localPatientIdentification;
    }

    public void setLocalPatientIdentification(String localPatientIdentification) {
        this.localPatientIdentification = localPatientIdentification;
    }

    public String getLocalRecordIdentification() {
        return localRecordIdentification;
    }

    public void setLocalRecordIdentification(String localRecordIdentification) {
        this.localRecordIdentification = localRecordIdentification;
    }
}
