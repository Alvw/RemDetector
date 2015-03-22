package bdf;


/**
 * Created by Al on 03.11.14.
 */
public class RecordingBdfConfig extends BdfConfigWrapper {

    private long startTime = -1;
    private String patientIdentification;
    private String recordingIdentification;
    private int numberOfDataRecords = -1;

    public RecordingBdfConfig(double durationOfDataRecord, int numberOfBytesInDataFormat, SignalConfig... signalsConfigList) {
        super(new DeviceBdfConfig(durationOfDataRecord, numberOfBytesInDataFormat, signalsConfigList));
    }

    public RecordingBdfConfig(BdfConfig bdfConfig) {
        super(bdfConfig);
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

    public int getNumberOfSignals() {
        return getSignalConfigs().length;
    }

    public double[] getSignalFrequencies() {
        double[] signalsFrequencies = new double[getNumberOfSignals()];
        for(int i = 0; i < getNumberOfSignals(); i++) {
            signalsFrequencies[i]  = getSignalConfigs()[i].getNumberOfSamplesInEachDataRecord()/ getDurationOfDataRecord();
        }
        return  signalsFrequencies;
    }

    public void setSignalsLabels(String[] signalsLabels) {
        if(signalsLabels != null) {
            SignalConfig[] signalsConfigList = getSignalConfigs();
            int length = Math.min(signalsConfigList.length, signalsLabels.length);
            for (int i = 0; i < length; i++) {
                if (signalsLabels[i] != null) {
                    signalsConfigList[i].setLabel(signalsLabels[i]);
                }
            }
        }
    }

    public String[] getSignalsLabels() {
        String[] signalsLabels = new String[getNumberOfSignals()];
        for(int i = 0; i < getNumberOfSignals(); i++) {
            signalsLabels[i]  = getSignalConfigs()[i].getLabel();
        }
        return  signalsLabels;
    }


    public int[] getNormalizedSignalsFrequencies() {
        return BdfNormalizer.getNormalizedSignalsFrequencies(this);
    }
}
