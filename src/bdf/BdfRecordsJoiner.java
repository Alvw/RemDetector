package bdf;

import dreamrec.ApplicationException;

import java.util.ArrayList;

/**
 * Created by mac on 02/12/14.
 */
public class BdfRecordsJoiner implements BdfProvider, BdfListener {
    private BdfProvider bdfProvider;
    private int numberOfRecordsToJoin;
    private ArrayList<BdfListener> listeners = new ArrayList<BdfListener>();
    private int recordsCounter;
    private byte[] resultingBdfDataRecords;
    private int numberOfBytesInDataFormat;
    private int resultingBdfDataRecordLength;

    public BdfRecordsJoiner(BdfProvider bdfProvider, int numberOfRecordsToJoin) {
        this.bdfProvider = bdfProvider;
        bdfProvider.addBdfDataListener(this);
        this.numberOfRecordsToJoin = numberOfRecordsToJoin;
        numberOfBytesInDataFormat = bdfProvider.getBdfConfig().getNumberOfBytesInDataFormat();

        for (SignalConfig signalConfig : bdfProvider.getBdfConfig().getSignalConfigs()) {
            resultingBdfDataRecordLength += signalConfig.getNumberOfSamplesInEachDataRecord();
        }
        resultingBdfDataRecordLength = resultingBdfDataRecordLength * numberOfRecordsToJoin * numberOfBytesInDataFormat;
        resultingBdfDataRecords = new byte[resultingBdfDataRecordLength];
    }

    @Override
    public void startReading() throws ApplicationException {
        bdfProvider.startReading();
    }

    @Override
    public void stopReading() throws ApplicationException {
        bdfProvider.stopReading();
    }

    @Override
    public void onDataRecordReceived(byte[] bdfDataRecord) {
        recordsCounter++;
        int pointer = 0;
        SignalConfig[] signalConfigs = bdfProvider.getBdfConfig().getSignalConfigs();
        for (int i = 0; i < signalConfigs.length; i++) {
            int numberOfSamples = signalConfigs[i].getNumberOfSamplesInEachDataRecord();
            int toIndex = (pointer * numberOfRecordsToJoin + numberOfSamples * (recordsCounter - 1)) * numberOfBytesInDataFormat;
            int fromIndex = pointer * numberOfBytesInDataFormat;
            System.arraycopy(bdfDataRecord, fromIndex, resultingBdfDataRecords, toIndex, numberOfSamples * numberOfBytesInDataFormat);
            pointer += numberOfSamples;
        }
        if (recordsCounter == numberOfRecordsToJoin) {
            for (BdfListener listener : listeners) {
                listener.onDataRecordReceived(resultingBdfDataRecords);
            }
            resultingBdfDataRecords = new byte[resultingBdfDataRecordLength];
            recordsCounter = 0;
        }
    }

    @Override
    public void onStopReading() {
        for (BdfListener listener : listeners) {
            listener.onStopReading();
        }
    }

    @Override
    public void addBdfDataListener(BdfListener bdfListener) {
        listeners.add(bdfListener);
    }

    @Override
    public void removeBdfDataListener(BdfListener bdfListener) {
        listeners.remove(bdfListener);
    }

    @Override
    public BdfConfig getBdfConfig() {
        return new JoinedBdfConfig(bdfProvider.getBdfConfig(), numberOfRecordsToJoin);
    }
}
