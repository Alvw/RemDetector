package bdf;

import data.DataDimension;

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
    public int[] getSignalNumberOfSamplesInEachDataRecord() {
        int[] numbersOfSamplesInEachDataRecord = new int[getNumberOfSignals()];
        for(int i = 0; i < getNumberOfSignals(); i++) {
            numbersOfSamplesInEachDataRecord[i]  = signalsConfigList[i].getNumberOfSamplesInEachDataRecord();
        }
        return  numbersOfSamplesInEachDataRecord;
    }

    @Override
    public DataDimension[] getSignalDimension() {
        DataDimension[] dataDimensions = new DataDimension[getNumberOfSignals()];
        for(int i = 0; i < getNumberOfSignals(); i++) {
            dataDimensions[i]  = signalsConfigList[i].getDataDimension();
        }
        return  dataDimensions;
    }

    public SignalConfig[] getSignalsConfigList() {
        return signalsConfigList;
    }
}
