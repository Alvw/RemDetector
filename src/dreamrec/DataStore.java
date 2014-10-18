package dreamrec;

import data.DataList;
import device.BdfConfig;
import device.BdfDataSourcePassive;
import device.BdfSignalConfig;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class DataStore  {
    private ArrayList<DataList> signalList = new ArrayList<DataList>();
    private ArrayList<DataStoreListener> updateListeners = new  ArrayList<DataStoreListener>();
    private BdfDataSourcePassive bdfDataSource;
    private boolean[] activeSignals;
    private int numberOfDataRecords;
    private Timer updateTimer;
    private int  UPDATE_DELAY = 500;
    private long startTime;

    public DataStore() {
        updateTimer = new Timer(UPDATE_DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                update();
                notifyListeners();
            }
        });
    }

    public void setBdfDataSource(BdfDataSourcePassive bdfDataSource, boolean[] activeSignals) {
        clear();
        this.bdfDataSource = bdfDataSource;
        this.activeSignals = activeSignals;
        BdfConfig bdfConfig = bdfDataSource.getBdfConfig();
        for(int i = 0; i < bdfConfig.getNumberOfSignals(); i++) {
            signalList.add(new DataList());
        }
    }

    private void clear() {
        signalList = new ArrayList<DataList>();
        startTime = 0;
        numberOfDataRecords = 0;
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
        BdfConfig bdfConfig = bdfDataSource.getBdfConfig();
        List<BdfSignalConfig> signalConfigList = bdfConfig.getSignalConfigList();
        while (bdfDataSource.isBdfDataRecordAvailable()) {
            if(numberOfDataRecords == 0) {
                startTime = System.currentTimeMillis();
            }
            numberOfDataRecords++;
            int[] bdfDataRecord = bdfDataSource.readBdfDataRecord();
            int bdfDataRecordIndex = 0;
            for(int signalNumber = 0; signalNumber < signalList.size(); signalNumber++) {
                for(int i = 0; i < signalConfigList.get(signalNumber).getNrOfSamplesInEachDataRecord(); i++){
                    if(activeSignals[signalNumber]) {
                        signalList.get(signalNumber).add(bdfDataRecord[bdfDataRecordIndex]);
                    }
                    bdfDataRecordIndex++;
                }
            }
        }
    }


    public int getNumberOfDataSignals() {
        return signalList.size();
    }
}
