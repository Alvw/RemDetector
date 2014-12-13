package bdf;

/**
 * Created by mac on 28/11/14.
 */
public class DeviceBdfConfig implements BdfConfig {
    protected final double durationOfDataRecord;    // in seconds
    protected final int numberOfBytesInDataFormat; // edf - 2 bytes, bdf - 3 bytes
    protected final SignalConfig[] signalsConfigList;

    public DeviceBdfConfig(double durationOfDataRecord, int numberOfBytesInDataFormat, SignalConfig... signalsConfigList) {
        this.durationOfDataRecord = durationOfDataRecord;
        this.signalsConfigList = signalsConfigList;
        this.numberOfBytesInDataFormat = numberOfBytesInDataFormat;
    }

    @Override
    public int getNumberOfBytesInDataFormat() {
        return numberOfBytesInDataFormat;
    }

    @Override
    public double getDurationOfDataRecord() {
        return durationOfDataRecord;
    }

    @Override
    public int getNumberOfSignals() {
        return signalsConfigList.length;
    }

    @Override
    public int[] getNumbersOfSamplesInEachDataRecord() {
        int[] NumbersOfSamplesInEachDataRecord = new int[getNumberOfSignals()];
        for(int i = 0; i < getNumberOfSignals(); i++) {
            NumbersOfSamplesInEachDataRecord[i]  = signalsConfigList[i].getNumberOfSamplesInEachDataRecord();
        }
        return  NumbersOfSamplesInEachDataRecord;
    }

    public SignalConfig[] getSignalsConfigList() {
        return signalsConfigList;
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
        if(durationOfDataRecord > 3.0/4) { // case durationOfDataRecord is integer
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
