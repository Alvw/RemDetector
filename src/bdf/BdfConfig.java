package bdf;

/**
 * Created by mac on 27/11/14.
 */
public interface BdfConfig {
    public double getDurationOfDataRecord();
    public int getNumberOfBytesInDataFormat();
    public int getNumberOfSignals();

    public int[] getNumbersOfSamplesInEachDataRecord();
}
