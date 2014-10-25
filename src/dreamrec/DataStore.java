package dreamrec;

import data.DataList;
import device.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataStore implements BdfDataListener {
    private ConcurrentLinkedQueue<int[]> dataRecordsBuffer = new ConcurrentLinkedQueue<int[]>();

    private DataList[] signalList;
    private ArrayList<DataStoreListener> updateListeners = new ArrayList<DataStoreListener>();
    private BdfDataSource bdfDataSource;
    private boolean[] activeSignals;
    private Timer updateTimer;
    private int UPDATE_DELAY = 500;
    private long startTime;
    private int numberOfDataRecords = 0;

    public DataStore(BdfDataSource bdfDataSource) {
        this.bdfDataSource = bdfDataSource;
        bdfDataSource.addBdfDataListener(this);
        BdfConfig bdfConfig = bdfDataSource.getBdfConfig();
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

    public DataStore(BdfDataSource bdfDataSource, boolean[] activeSignals) {
        this(bdfDataSource);
        int number = Math.min(this.activeSignals.length, activeSignals.length);
        for (int i = 0; i < number; i++) {
            this.activeSignals[i] = activeSignals[i];
        }

    }

    @Override
    public void onDataRecordReceived(int[] bdfDataRecord) {
        dataRecordsBuffer.offer(bdfDataRecord);
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
        BdfConfig bdfConfig = bdfDataSource.getBdfConfig();
        List<BdfSignalConfig> signalConfigList = bdfConfig.getSignalConfigList();
        System.out.println("update begin ");
        while (dataRecordsBuffer.size() > 0) {
            int[] bdfDataRecord = dataRecordsBuffer.poll();
            int bdfDataRecordIndex = 0;
            for (int signalNumber = 0; signalNumber < signalList.length; signalNumber++) {
                int avg = 0;
                for (int i = 0; i < signalConfigList.get(signalNumber).getNrOfSamplesInEachDataRecord(); i++) {
                    if (activeSignals[signalNumber]) {
                        avg += bdfDataRecord[bdfDataRecordIndex];
                        if ((i + 1) % 10 == 0) {
                            signalList[signalNumber].add(avg / 10);
                            avg = 0;
                        }


                    }
                    bdfDataRecordIndex++;
                }
            }
        }
        System.out.println("update end ");
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
        return bdfDataSource.getBdfConfig().getSignalsFrequencies()[signalNumber];
    }

    public int getNumberOfDataSignals() {
        return signalList.length;
    }

    public long getStartTime() {
        return startTime;
    }
}
