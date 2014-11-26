package dreamrec;

import bdf.BdfConfig;
import bdf.BdfListener;
import bdf.BdfParser;
import bdf.BdfProvider;
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
    private ArrayList<DataStoreListener> updateListeners = new ArrayList<DataStoreListener>();
    
    private DataList[] channelsList;
    private int[] channelsMask; // 0 - if signal is disabled, and frequency divider value if signal is enable
    private Timer updateTimer;
    private int UPDATE_DELAY = 250;
    private int MAX_FREQUENCY = 50; //hz;
    private long startTime;
    private BdfParser bdfParser;
    private volatile boolean isReadingStopped = false;

    public DataStore(BdfProvider bdfProvider, int[] channelsMask) {
        if (channelsMask == null) {
            channelsMask = createDefaultMask(bdfProvider);

        }
        this.channelsMask = channelsMask;
        bdfProvider.addBdfDataListener(this);
        BdfConfig bdfConfig = bdfProvider.getBdfConfig();
       // startTime = bdfConfig.getStartTime();
        bufferSize = (int)(BUFFER_CAPACITY_SECONDS/bdfConfig.getDurationOfDataRecord());
        dataRecordsBuffer = new LinkedBlockingQueue<byte[]>(bufferSize);
        bdfParser = new BdfParser(bdfConfig);
        double[] frequencies = bdfConfig.getSignalsFrequencies();
        int numberOfSignals = bdfConfig.getNumberOfSignals();

        channelsList = new DataList[numberOfSignals];
        for (int i = 0; i < numberOfSignals; i++) {
            if(channelsMask[i] != 0) {
                channelsList[i] = new DataList();
                channelsList[i].setFrequency(frequencies[i]/channelsMask[i]);
            }
        }

        updateTimer = new Timer(UPDATE_DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                processBufferedData();
                notifyListeners();
                if(isReadingStopped) {
                    updateTimer.stop();
                }
            }
        });
        updateTimer.start();
    }

    public DataStore(BdfProvider bdfProvider) {
        this(bdfProvider, null);
    }


    private int[] createDefaultMask(BdfProvider bdfProvider) {
        BdfConfig bdfConfig = bdfProvider.getBdfConfig();
        int numberOfSignals = bdfConfig.getNumberOfSignals();
        int[] channelsMask = new int[numberOfSignals];
        for (int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++) {
            channelsMask[signalNumber] = 1;
        }
 // temporal frequency adjustment
        double[] frequencies = bdfConfig.getSignalsFrequencies();
        for (int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++) {
            if (frequencies[signalNumber] > MAX_FREQUENCY) {
                channelsMask[signalNumber] = (int) (frequencies[signalNumber] / MAX_FREQUENCY);
            } else {
                channelsMask[signalNumber] = 1;
            }
        }
        return channelsMask;
    }

    public int getNumberOfChannels() {
        return signalToChannel(getNumberOfSignals() - 1) + 1;
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
        isReadingStopped = true;
    }

    public void addListener(DataStoreListener dataStoreListener) {
        updateListeners.add(dataStoreListener);
    }

    public DataList getChannelData(int channelNumber) {
        int signalNumber = channelToSignal(channelNumber);
        return channelsList[signalNumber];
    }

    public long getStartTime() {
        return startTime;
    }


    private void notifyListeners() {
        for (DataStoreListener listener : updateListeners) {
            listener.onDataStoreUpdate();
        }
    }

    private void processBufferedData() {
        while (dataRecordsBuffer.size() > 0) {
            byte[] dataRecord = dataRecordsBuffer.poll();
            processDataRecord(dataRecord);
        }
    }

    private int getNumberOfSignals() {
        return channelsList.length;
    }

    private int signalToChannel(int signalNumber) {
        int channelNumber = -1;
        for(int i = 0; i < signalNumber; i++) {
            if(signalNumber != 0) {
                channelNumber++;
            }
        }
        return channelNumber;
    }

    private int channelToSignal(int channelNumber) {
        int number = -1;
        for(int i = 0; i < getNumberOfSignals(); i++) {
            if(channelsMask[i] != 0) {
                number++;
                if(number == channelNumber) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void processDataRecord(byte[] bdfDataRecord) {
        for (int signalNumber = 0; signalNumber < getNumberOfSignals(); signalNumber++) {
            int[] signalData = bdfParser.parseDataRecordSignal(bdfDataRecord, signalNumber);
            int divider = channelsMask[signalNumber];
            if (divider == 1) {
                channelsList[signalNumber].add(signalData);
            } else { // adjust frequency
                int sum = 0;
                for (int i = 0; i < signalData.length; i++) {
                    sum += signalData[i];
                    if ((i + 1) % divider == 0) {
                        channelsList[signalNumber].add(sum / divider);
                        sum = 0;
                    }
                }
            }
        }
    }

}
