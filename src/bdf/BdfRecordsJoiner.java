package bdf;

import dreamrec.ApplicationException;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mac on 02/12/14.
 */
public class BdfRecordsJoiner implements BdfProvider, BdfListener {
    private BdfProvider bdfProvider;
    private int numberOfRecordsToJoin;
    private ArrayList<BdfListener> listeners = new ArrayList<BdfListener>();
    private int recordsCounter;
    private byte[] resultingBdfDataRecord;
    private int numberOfBytesInDataFormat;
    private int[] numbersOfSamplesInEachDataRecord;
    private int resultingBdfDataRecordLength;

    public BdfRecordsJoiner(BdfProvider bdfProvider, int numberOfRecordsToJoin) {
        this.bdfProvider = bdfProvider;
        bdfProvider.addBdfDataListener(this);
        this.numberOfRecordsToJoin = numberOfRecordsToJoin;
        numbersOfSamplesInEachDataRecord = bdfProvider.getBdfConfig().getNumbersOfSamplesInEachDataRecord();
        numberOfBytesInDataFormat = bdfProvider.getBdfConfig().getNumberOfBytesInDataFormat();
        for(int i = 0; i < numbersOfSamplesInEachDataRecord.length; i++) {
            resultingBdfDataRecordLength += numbersOfSamplesInEachDataRecord[i];
        }
        resultingBdfDataRecordLength = resultingBdfDataRecordLength * numberOfRecordsToJoin * numberOfBytesInDataFormat;
        resultingBdfDataRecord = new byte[resultingBdfDataRecordLength];
    }

    @Override
    public void startReading() throws ApplicationException {
        bdfProvider.startReading();
    }

    @Override
    public void stopReading() throws ApplicationException {
        bdfProvider.startReading();
    }

    @Override
    public void onDataRecordReceived(byte[] bdfDataRecord) {
        recordsCounter++;
        int pointer = 0;
        for(int i = 0; i < numbersOfSamplesInEachDataRecord.length; i++) {
            int toIndex = (pointer*numberOfRecordsToJoin + numbersOfSamplesInEachDataRecord[i]*(recordsCounter - 1)) * numberOfBytesInDataFormat ;
            int fromIndex = pointer * numberOfBytesInDataFormat;
            System.arraycopy(bdfDataRecord, fromIndex, resultingBdfDataRecord, toIndex, numbersOfSamplesInEachDataRecord[i]*numberOfBytesInDataFormat);
            pointer += numbersOfSamplesInEachDataRecord[i];
        }
        if(recordsCounter == numberOfRecordsToJoin) {
            for(BdfListener listener : listeners) {
                listener.onDataRecordReceived(resultingBdfDataRecord);
            }
            resultingBdfDataRecord = new byte[resultingBdfDataRecordLength];
            recordsCounter = 0;
        }
    }

    @Override
    public void onStopReading() {
           for(BdfListener listener : listeners) {
               listener.onStopReading();
           }
    }

    @Override
    public void addBdfDataListener(BdfListener bdfListener) {
        listeners.add(bdfListener);
    }

    @Override
    public BdfConfig getBdfConfig() {
        return new JoinedBdfConfig(bdfProvider.getBdfConfig(), numberOfRecordsToJoin);
    }
}
