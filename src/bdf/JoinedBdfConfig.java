package bdf;

/**
 * Created by mac on 01/12/14.
 */
public class JoinedBdfConfig extends BdfConfigWrapper implements BdfConfig {
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
    public int[] getNumberOfSamplesInEachDataRecord() {
        int[] numbersOfSamplesInEachDataRecord = bdfConfig.getNumberOfSamplesInEachDataRecord();
        for(int i = 0; i < numbersOfSamplesInEachDataRecord.length; i++) {
            numbersOfSamplesInEachDataRecord[i]  = numbersOfSamplesInEachDataRecord[i] * numberOfRecordsToJoin;
        }
        return numbersOfSamplesInEachDataRecord;
    }

}
