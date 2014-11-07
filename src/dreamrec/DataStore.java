package dreamrec;

import bdf.*;
import data.DataList;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DataStore implements BdfListener {
    private ConcurrentLinkedQueue<byte[]> dataRecordsBuffer = new ConcurrentLinkedQueue<byte[]>();
    //private LinkedList<byte[]> dataRecordsBuffer = new LinkedList<byte[]>();

    private DataList[] signalList;
    private ArrayList<DataStoreListener> updateListeners = new ArrayList<DataStoreListener>();

    private boolean[] activeSignals;
    private Timer updateTimer;
    private int UPDATE_DELAY = 500;
    private int MAX_FREQUENCY = 50; //hz;
    private long startTime;
    private double[] frequencies;
    private int[] dividers;
    private BdfParser bdfParser;


    public DataStore(BdfSource bdfSource) {
        bdfSource.addBdfDataListener(this);
        BdfConfig bdfConfig = bdfSource.getBdfConfig();
        bdfParser = new BdfParser(bdfConfig);
        frequencies = bdfConfig.getSignalsFrequencies();
        int numberOfSignals = bdfConfig.getNumberOfSignals();
        dividers = new int[numberOfSignals];
        for (int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++) {
            if (frequencies[signalNumber] > MAX_FREQUENCY) {
                dividers[signalNumber] = (int)(frequencies[signalNumber] / MAX_FREQUENCY);
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
                try {
                    update();
                    notifyListeners();
                } catch (ApplicationException e) {
                    System.out.println(e);
                }

            }
        });

        startTime = bdfConfig.getStartTime();
        updateTimer.start();
    }

    public DataStore(BdfSource bdfSource, boolean[] activeSignals) {
        this(bdfSource);
        int number = Math.min(this.activeSignals.length, activeSignals.length);
        for (int i = 0; i < number; i++) {
            this.activeSignals[i] = activeSignals[i];
        }

    }

    @Override
    public void onDataRecordReceived(byte[] dataRecord) {
        dataRecordsBuffer.offer(dataRecord);
    }

    @Override
    public void onStopReading() {
        updateTimer.stop();
        try {
            update();
            notifyListeners();
        } catch (ApplicationException e) {
            System.out.println(e);
        }

    }

    private void update() throws ApplicationException {
        while (dataRecordsBuffer.size() > 0) {
            byte[] dataRecord = dataRecordsBuffer.poll();
            for(int channelNumber = 0; channelNumber < signalList.length; channelNumber++){
                int[] channelData = bdfParser.parseDataRecordSignal(dataRecord, channelNumber);
                adjustChannelFrequency(channelData, channelNumber);
            }
        }
    }

    private void adjustChannelFrequency (int[] channelDataRecord, int channelNumber) {
        int sum = 0;
        int divider = dividers[channelNumber];
        if(divider == 1) {
            signalList[channelNumber].add(channelDataRecord);
        }
        else {
            for(int i = 0; i < channelDataRecord.length; i++){
                sum += channelDataRecord[i];
                if((i + 1)%divider == 0) {
                    signalList[channelNumber].add(sum/divider);
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
