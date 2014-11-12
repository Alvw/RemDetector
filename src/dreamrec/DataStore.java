package dreamrec;

import bdf.*;
import data.DataList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class DataStore implements BdfListener {
    private static final Log log = LogFactory.getLog(DataStore.class);

    //private ConcurrentLinkedQueue<byte[]> dataRecordsBuffer = new ConcurrentLinkedQueue<byte[]>();
    private LinkedBlockingQueue<byte[]> dataRecordsBuffer;

    private int BUFFER_CAPACITY_SECONDS = 60*20; // to protect from OutOfMemoryError
    private int bufferSize;
    private DataList[] signalList;
    private ArrayList<DataStoreListener> updateListeners = new ArrayList<DataStoreListener>();

    private boolean[] activeSignals;
    private Timer updateTimer;
    private int UPDATE_DELAY = 250;
    private int MAX_FREQUENCY = 50; //hz;
    private long startTime;
    private double[] frequencies;
    private int[] dividers;
    private BdfParser bdfParser;


    public DataStore(BdfProvider bdfProvider) {
        bdfProvider.addBdfDataListener(this);
        BdfConfig bdfConfig = bdfProvider.getBdfConfig();
        bufferSize = (int)(BUFFER_CAPACITY_SECONDS/bdfConfig.getDurationOfDataRecord());
        dataRecordsBuffer = new LinkedBlockingQueue<byte[]>(bufferSize);
        bdfParser = new BdfParser(bdfConfig);
        frequencies = bdfConfig.getSignalsFrequencies();
        int numberOfSignals = bdfConfig.getNumberOfSignals();
        dividers = new int[numberOfSignals];
        for (int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++) {
            if (frequencies[signalNumber] > MAX_FREQUENCY) {
                dividers[signalNumber] = (int) (frequencies[signalNumber] / MAX_FREQUENCY);
                frequencies[signalNumber] = MAX_FREQUENCY;
            } else {
                dividers[signalNumber] = 1;
            }
        }
        activeSignals = new boolean[numberOfSignals];
        signalList = new DataList[numberOfSignals];
        for (int i = 0; i < numberOfSignals; i++) {
            signalList[i] = new DataList();
            activeSignals[i] = true;
        }

        updateTimer = new Timer(UPDATE_DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                processBufferedData();
                notifyListeners();
            }
        });
        startTime = bdfConfig.getStartTime();
        updateTimer.start();
    }

    public DataStore(BdfProvider bdfProvider, boolean[] activeSignals) {
        this(bdfProvider);
        int number = Math.min(this.activeSignals.length, activeSignals.length);
        for (int i = 0; i < number; i++) {
            this.activeSignals[i] = activeSignals[i];
        }

    }

    @Override
    public void onDataRecordReceived(byte[] dataRecord) {
        if (SwingUtilities.isEventDispatchThread()){ // if data comes from gui thread we process it at once
            processDataRecord(dataRecord);
        }else{
            try{
                dataRecordsBuffer.put(dataRecord); // if data comes from non-gui thread we just buffer it
            } catch(InterruptedException e) {
                log.error(e);
            }
        }
    }

    @Override
    public void onStopReading() {
        updateTimer.stop();
        processBufferedData();
        notifyListeners();
    }

    private void processBufferedData() {
        while (dataRecordsBuffer.size() > 0) {
            byte[] dataRecord = dataRecordsBuffer.poll();
            processDataRecord(dataRecord);
        }
    }

    private void processDataRecord(byte[] bdfDataRecord) {
        for (int channelNumber = 0; channelNumber < signalList.length; channelNumber++) {
            int[] channelData = bdfParser.parseDataRecordSignal(bdfDataRecord, channelNumber);
            adjustChannelFrequency(channelData, channelNumber);
        }
    }

    private void adjustChannelFrequency(int[] channelDataRecord, int channelNumber) {
        int sum = 0;
        int divider = dividers[channelNumber];
        if (divider == 1) {
            signalList[channelNumber].add(channelDataRecord);
        } else {
            for (int i = 0; i < channelDataRecord.length; i++) {
                sum += channelDataRecord[i];
                if ((i + 1) % divider == 0) {
                    signalList[channelNumber].add(sum / divider);
                    sum = 0;
                }
            }
        }

    }

    public void addListener(DataStoreListener dataStoreListener) {
        updateListeners.add(dataStoreListener);
    }

    private void notifyListeners() {
        for (DataStoreListener listener : updateListeners) {
            listener.onDataStoreUpdate();
        }
    }

    public DataList getSignalData(int signalNumber) {
        return signalList[signalNumber];
    }

    public double getSignalFrequency(int signalNumber) {
        return frequencies[signalNumber];
    }

    public int getNumberOfDataSignals() {
        return signalList.length;
    }

    public long getStartTime() {
        return startTime;
    }
}
