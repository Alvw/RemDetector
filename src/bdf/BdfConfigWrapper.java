package bdf;

/**
 * Created by mac on 02/12/14.
 */
public class BdfConfigWrapper implements BdfConfig{
    protected BdfConfig bdfConfig;

    public BdfConfigWrapper(BdfConfig bdfConfig) {
        this.bdfConfig = bdfConfig;
    }

    @Override
    public double getDurationOfDataRecord() {
        return bdfConfig.getDurationOfDataRecord();
    }

    @Override
    public int getNumberOfBytesInDataFormat() {
        return bdfConfig.getNumberOfBytesInDataFormat();
    }

    @Override
    public int getNumberOfSignals() {
        return bdfConfig.getNumberOfSignals();
    }

    @Override
    public int[] getNumbersOfSamplesInEachDataRecord() {
        return bdfConfig.getNumbersOfSamplesInEachDataRecord();
    }
}
