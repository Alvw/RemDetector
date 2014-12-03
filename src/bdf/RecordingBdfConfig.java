package bdf;


/**
 * Created by Al on 03.11.14.
 */
public class RecordingBdfConfig extends DeviceBdfConfig implements BdfConfig {

    private long startTime;
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

}
