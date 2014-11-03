package dreamrec;

import data.DataList;
import device.BdfConfig;
import device.DataListener;
import device.DataSource;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataStore implements DataListener {
    private ConcurrentLinkedQueue<int[][]> dataRecordsBuffer = new ConcurrentLinkedQueue<int[][]>();

    private DataList[] signalList;
    private ArrayList<DataStoreListener> updateListeners = new ArrayList<DataStoreListener>();

    private boolean[] activeSignals;
    private Timer updateTimer;
    private int UPDATE_DELAY = 500;
    private long startTime;
    private BdfConfig bdfConfig;

    public DataStore(DataSource bdfDataSource) {
        bdfDataSource.addDataListener(this);
        bdfConfig = bdfDataSource.getBdfConfig();
        int numberOfSignals = bdfConfig.getNumberOfSignals();
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

    public DataStore(DataSource bdfDataSource, boolean[] activeSignals) {
        this(bdfDataSource);
        int number = Math.min(this.activeSignals.length, activeSignals.length);
        for (int i = 0; i < number; i++) {
            this.activeSignals[i] = activeSignals[i];
        }

    }

    @Override
    public void onDataRecordReceived(int[][] dataRecord) {
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
            int[][] dataRecord = dataRecordsBuffer.poll();
            for (int signalNumber = 0; signalNumber < dataRecord.length; signalNumber++) {
                for (int sampleNumber = 0; sampleNumber < dataRecord[signalNumber].length; sampleNumber++) {
                    signalList[signalNumber].add(dataRecord[signalNumber][sampleNumber]);
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
        return bdfConfig.getSignalsFrequencies()[signalNumber];
    }

    public int getNumberOfDataSignals() {
        return signalList.length;
    }

    public long getStartTime() {
        return startTime;
    }
}
