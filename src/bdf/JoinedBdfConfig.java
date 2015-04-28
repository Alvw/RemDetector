package bdf;

/**
 * Created by mac on 01/12/14.
 */
public class JoinedBdfConfig extends BdfConfigWrapper {
    private int numberOfRecordsToJoin;

    public JoinedBdfConfig(BdfConfig originalBdfConfig, int numberOfRecordsToJoin) {
        super(originalBdfConfig);
        this.numberOfRecordsToJoin = numberOfRecordsToJoin;
    }

    @Override
    public double getDurationOfDataRecord() {
        return bdfConfig.getDurationOfDataRecord() * numberOfRecordsToJoin;
    }

    @Override
    public SignalConfig[] getSignalConfigs() {
        SignalConfig[] originalSignalConfigs =  bdfConfig.getSignalConfigs();
        int length = originalSignalConfigs.length;
        SignalConfig[] resultingSignalsConfigs = new SignalConfig[length];
        for(int i = 0; i < length; i++) {
            int resultingNumberOfSamples = numberOfRecordsToJoin * originalSignalConfigs[i].getNumberOfSamplesInEachDataRecord();
            Calibration resultingCalibration = originalSignalConfigs[i].getCalibration();
            resultingSignalsConfigs[i]  = new SignalConfig(resultingNumberOfSamples, resultingCalibration);
        }
        return resultingSignalsConfigs;
    }
}
