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

    public double[] getSignalsFrequencies() {
        double[] signalsFrequencies = new double[getNumberOfSignals()];
        for(int i = 0; i < getNumberOfSignals(); i++) {
            signalsFrequencies[i]  = signalsConfigList[i].getNumberOfSamplesInEachDataRecord()/ durationOfDataRecord;
        }
        return  signalsFrequencies;
    }

    public void setSignalsLabels(String[] signalsLabels) {
        SignalConfig[] signalsConfigs = getSignalsConfigList();
        int length = Math.min(signalsConfigs.length, signalsLabels.length);
        for (int i = 0; i < length; i++) {
            if (signalsLabels[i] != null) {
                signalsConfigs[i].setLabel(signalsLabels[i]);
            }
        }
    }

    public String[] getSignalsLabels() {
        String[] signalsLabels = new String[getNumberOfSignals()];
        for(int i = 0; i < getNumberOfSignals(); i++) {
            signalsLabels[i]  = signalsConfigList[i].getLabel();
        }
        return  signalsLabels;
    }

    /*
  * we suppose:
  * 1)  that "ideal/theoretical" number of records per seconds(rps) = 1/durationOfDataRecord is integer. Or durationOfDataRecord is already integer
  * 2) Real durationOfDataRecord is only slightly different from its supposed theoretical value
  * So for example instead of 500 Hz real frequency will be 503 Hz or so on
  *
  * Here we calculate that theoretical normalized DurationOfData record on the base of its real value
  */
    public double getNormalizedDurationOfDataRecord() {
        double normalizedDurationOfDataRecord;
        if(durationOfDataRecord > 3/4) { // case durationOfDataRecord is integer
            normalizedDurationOfDataRecord = Math.round(durationOfDataRecord);
        }
        else { // duration of data record is 1/2, 1/3, 1/4 ....
            long rps = Math.round(1 / durationOfDataRecord);
            normalizedDurationOfDataRecord = (1.0 / rps);
        }
        return normalizedDurationOfDataRecord;
    }

    public int[] getNormalizedSignalsFrequencies() {
        double normalizedDurationOfDataRecord = getNormalizedDurationOfDataRecord();
        int[] numbersOfSamplesInEachDataRecord = getNumbersOfSamplesInEachDataRecord();
        int[] normalizedFrequencies = new int[numbersOfSamplesInEachDataRecord.length];
        for(int i = 0; i < numbersOfSamplesInEachDataRecord.length; i++) {
            normalizedFrequencies[i] = (int) (numbersOfSamplesInEachDataRecord[i] / normalizedDurationOfDataRecord);
        }
        return normalizedFrequencies;
    }
}
