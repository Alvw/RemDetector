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
}
