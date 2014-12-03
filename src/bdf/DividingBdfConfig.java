package bdf;

import dreamrec.ApplicationException;

public class DividingBdfConfig extends BdfConfigWrapper implements BdfConfig {
    private int[] frequencyDividers;

    public DividingBdfConfig(BdfConfig originalBdfConfig, int[] frequencyDividers) throws  ApplicationException {
        super(originalBdfConfig);
        this.frequencyDividers = frequencyDividers;
        int[] originalNumbersOfSamplesInEachDataRecord = originalBdfConfig.getNumbersOfSamplesInEachDataRecord();
        for(int i = 0; i < frequencyDividers.length; i++) {
            if((originalNumbersOfSamplesInEachDataRecord[i] % frequencyDividers[i]) == 0) {
                throw new ApplicationException("NumbersOfSamplesInEachDataRecord should be divisible by its frequency dividers");
            }
        }
    }

    @Override
    public int[] getNumbersOfSamplesInEachDataRecord() {
        int[] numbersOfSamplesInEachDataRecord = bdfConfig.getNumbersOfSamplesInEachDataRecord();
        for(int i = 0; i < numbersOfSamplesInEachDataRecord.length; i++) {
            numbersOfSamplesInEachDataRecord[i]  = numbersOfSamplesInEachDataRecord[i] / frequencyDividers[i];
        }
        return numbersOfSamplesInEachDataRecord;
    }
}

