package bdf;

/**
 * Created by mac on 06/11/14.
 */
public class BdfParser {
    private BdfConfig bdfConfig;

    public BdfParser(BdfConfig bdfConfig) {
        this.bdfConfig = bdfConfig;
    }


    public int parseDataRecordSample(byte[] bdfDataRecord, int sampleNumber) {
        int numberOfBytesInDataFormat = bdfConfig.getNumberOfBytesInDataFormat();
        if (numberOfBytesInDataFormat == 3) {  //bdf format
            return convert3BytesToSignedInt(bdfDataRecord[sampleNumber * 3],
                    bdfDataRecord[sampleNumber * 3 + 1], bdfDataRecord[sampleNumber * 3 + 2]);
        }
        if (numberOfBytesInDataFormat == 2) {   // edf format
            return convert2BytesToSignedInt(bdfDataRecord[sampleNumber * 2], bdfDataRecord[sampleNumber * 2 + 1]);
        }
        return 0;
    }

    public int[][] parseDataRecord(byte[] bdfDataRecord) {
        int numberOfSignals = bdfConfig.getNumberOfSignals();
        int[][] result = new int[numberOfSignals][];
        for (int i = 0; i < numberOfSignals; i++) {
            result[i] = parseDataRecordSignal(bdfDataRecord, i);
        }
        return result;
    }

    public int[] parseDataRecordSignal(byte[] bdfDataRecord, int signalNumber) {
        int numberOfSamples = bdfConfig.getNumbersOfSamplesInEachDataRecord()[signalNumber];
        int startIndex = getSignalStartIndexInDataRecord(signalNumber);
        int[] result = new int[numberOfSamples];
        for (int i = 0; i < numberOfSamples; i++) {
            result[i] = parseDataRecordSample(bdfDataRecord, startIndex + i);
        }
        return result;
    }

    private int getSignalStartIndexInDataRecord(int signalNumber) {
        int startIndex = 0;
        for (int i = 0; i < signalNumber; i++) {
            startIndex += bdfConfig.getNumbersOfSamplesInEachDataRecord()[i];
        }
        return startIndex;
    }

    /* Byte order: LITTLE_ENDIAN  */
    public static int convert2BytesToSignedInt(byte b1, byte b2) {
        return (b2 << 8) | (b1 & 0xFF);
    }


    public static int convert2BytesToUnsignedInt(byte b1, byte b2) {
        return (b2 & 0xFF) << 8 | (b1 & 0xFF);
    }

    public static int convert3BytesToSignedInt(byte b1, byte b2, byte b3) {
        return (b3 << 16) | (b2 & 0xFF) << 8 | (b1 & 0xFF);
    }

    public static int convert3BytesToUnsignedInt(byte b1, byte b2, byte b3) {
        return (b3 & 0xFF) << 16 | (b2 & 0xFF) << 8 | (b1 & 0xFF);
    }
}
