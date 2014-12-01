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
      /*  byte[] buffer = new byte[4];
        buffer[0] = bdfDataRecord[sampleNumber*3];
        buffer[1] = bdfDataRecord[sampleNumber*3 + 1];
        buffer[2] = bdfDataRecord[sampleNumber*3 + 2];
        return ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getInt();*/
        return (bdfDataRecord[sampleNumber*3 + 2] & 0xff) << 16 |
                (bdfDataRecord[sampleNumber*3 + 1] & 0xff) << 8 | (bdfDataRecord[sampleNumber*3] & 0xff);
    }

    public int[][] parseDataRecord(byte[] bdfDataRecord) {
        int numberOfSignals = bdfConfig.getNumberOfSignals();
        int[][] result = new int[numberOfSignals][];
        for(int i = 0; i < numberOfSignals; i++) {
            result[i] = parseDataRecordSignal(bdfDataRecord, i);
        }
        return result;
    }

    public int[] parseDataRecordSignal(byte[] bdfDataRecord, int signalNumber) {
        int numberOfSamples = bdfConfig.getNumbersOfSamplesInEachDataRecord()[signalNumber];
        int startIndex = getSignalStartIndexInDataRecord(signalNumber);
        int[] result = new int[numberOfSamples];
        for(int i = 0; i < numberOfSamples; i++) {
           result[i] = parseDataRecordSample(bdfDataRecord, startIndex+i);
        }
        return result;
    }

    private int getSignalStartIndexInDataRecord(int signalNumber) {
        int startIndex = 0;
        for(int i = 0; i < signalNumber; i++) {
            startIndex += bdfConfig.getNumbersOfSamplesInEachDataRecord()[i];
        }
        return startIndex;
    }
}
