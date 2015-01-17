package bdf;

/**
 * Created by mac on 28/11/14.
 */
public class DeviceBdfConfig implements BdfConfig {
    protected final double durationOfDataRecord;    // in seconds
    protected final int numberOfBytesInDataFormat; // edf - 2 bytes, bdf - 3 bytes
    protected final SignalConfig[] signalConfigs;

    public DeviceBdfConfig(double durationOfDataRecord, int numberOfBytesInDataFormat, SignalConfig... signalConfigs) {
        this.durationOfDataRecord = durationOfDataRecord;
        this.signalConfigs = signalConfigs;
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
    public SignalConfig[] getSignalConfigs() {
        return signalConfigs;
    }
}
