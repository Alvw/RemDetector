package dreamrec;

import data.DataList;
import device.BdfConfig;
import device.BdfDataSourcePassive;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class DataStore  {
    private ArrayList<DataList> signalList = new ArrayList<DataList>();
    private ArrayList<DataStoreListener> updateListeners = new  ArrayList<DataStoreListener>();
    private BdfDataSourcePassive bdfDataSource;
    private boolean[] activeSignals;
    private int numberOfDataRecords;
    private Timer updateTimer;
    private int  UPDATE_DELAY = 500;

    public DataStore(BdfDataSourcePassive bdfDataSource, boolean[] activeSignals) {
        this.bdfDataSource = bdfDataSource;
        this.activeSignals = activeSignals;
        BdfConfig bdfConfig = bdfDataSource.getBdfConfig();
        for(int i = 0; i < bdfConfig.getNumberOfSignals(); i++) {
            signalList.add(new DataList());
        }

        updateTimer = new Timer(UPDATE_DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                update();
                notifyListeners();
            }
        });
    }


    private void addListener(DataStoreListener dataStoreListener) {
              updateListeners.add(dataStoreListener);
    }

    private void notifyListeners() {
            for (DataStoreListener listener : updateListeners) {
                listener.onDataStoreUpdate();
            }
    }

    private void update() {
        while (bdfDataSource.isBdfDataRecordAvailable()) {
            int[] bdfDataRecord = bdfDataSource.readBdfDataRecord();
        /*    for (int i = 0; i < nrOfChannelSamples; i++) {
                model.addCh1Data(frame[i]);
            }

            for (int i = 0; i < nrOfAccelerometerSamples; i++) {
                model.addAcc1Data(frame[nrOfChannelSamples + i]);
            }
            for (int i = 0; i < nrOfAccelerometerSamples; i++) {
                model.addAcc2Data(frame[nrOfChannelSamples + nrOfAccelerometerSamples + i]);
            }
            for (int i = 0; i < nrOfAccelerometerSamples; i++) {
                model.addAcc3Data(frame[nrOfChannelSamples + 2 * nrOfAccelerometerSamples + i]);
            }   */
        }
    }


    public int getNumberOfDataSignals() {
        return signalList.size();
    }
}
