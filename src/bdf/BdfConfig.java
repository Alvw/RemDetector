package bdf;

/**
 * Created by Al on 03.11.14.
 */
public class BdfConfig  {

    private final double durationOfDataRecord;    // in seconds
    private final int numberOfBytesInDataFormat; // edf - 2 bytes, bdf - 3 bytes
    private final BdfSignalConfig[] signalsConfigList;

    public BdfConfig(double durationOfDataRecord, int numberOfBytesInDataFormat, BdfSignalConfig... signalsConfigList) {
        this.durationOfDataRecord = durationOfDataRecord;
        this.signalsConfigList = signalsConfigList;
        this.numberOfBytesInDataFormat = numberOfBytesInDataFormat;
    }

    public int getNumberOfBytesInDataFormat() {
        return numberOfBytesInDataFormat;
    }

    public double getDurationOfDataRecord() {
        return durationOfDataRecord;
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

}
