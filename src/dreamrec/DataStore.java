package dreamrec;

import bdf.BdfConfig;
import bdf.BdfListener;
import bdf.BdfParser;
import bdf.BdfProvider;
import data.DataList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import prefilters.PreFilter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class DataStore implements BdfListener {
    private static final Log log = LogFactory.getLog(DataStore.class);

    //private ConcurrentLinkedQueue<byte[]> dataRecordsBuffer = new ConcurrentLinkedQueue<byte[]>();
    private LinkedBlockingQueue<byte[]> dataRecordsBuffer;

    private int BUFFER_CAPACITY_SECONDS = 60 * 30; // to protect from OutOfMemoryError
    private int bufferSize;
    private ArrayList<DataStoreListener> updateListeners = new ArrayList<DataStoreListener>();

    protected DataList[] channelsList;
    private PreFilter[] preFiltersList;
    private boolean[] channelsMask;
    private Timer updateTimer;
    private int UPDATE_DELAY = 250;

    private BdfParser bdfParser;
    private BdfConfig bdfConfig;
    private volatile boolean isReadingStopped = false;
    private volatile boolean isReadingStarted = false;
    private volatile int numberOfDataRecords = -1;
    private volatile long startTime;

    public DataStore(BdfProvider bdfProvider) {
        bdfProvider.addBdfDataListener(this);
        bdfConfig = bdfProvider.getBdfConfig();
        bufferSize = (int) (BUFFER_CAPACITY_SECONDS / bdfConfig.getDurationOfDataRecord());
        bufferSize = bufferSize / bdfConfig.getNumberOfSignals();
        dataRecordsBuffer = new LinkedBlockingQueue<byte[]>(bufferSize);
        bdfParser = new BdfParser(bdfConfig);

        int numberOfSignals = bdfConfig.getNumberOfSignals();
        preFiltersList = new PreFilter[numberOfSignals];
        this.channelsMask = new boolean[numberOfSignals];
        for (int i = 0; i < channelsMask.length; i++) {
            this.channelsMask[i] = true;
        }

        int numbersOfSamplesInEachDataRecord[] = bdfConfig.getNumberOfSamplesInEachDataRecord();
        channelsList = new DataList[numberOfSignals];
        for (int i = 0; i < channelsList.length; i++) {
            double frequency = numbersOfSamplesInEachDataRecord[i] / bdfConfig.getDurationOfDataRecord();
            channelsList[i] = new DataList();
            channelsList[i].setFrequency(frequency);
        }

        updateTimer = new Timer(UPDATE_DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                processBufferedData();
                if (isReadingStarted) {
                    for (DataStoreListener listener : updateListeners) {
                        listener.onStart(startTime);
                    }
                    isReadingStarted = false;
                }

                for (DataStoreListener listener : updateListeners) {
                    listener.onDataUpdate();
                }

                if (isReadingStopped) {
                    updateTimer.stop();
                }
            }
        });
    }


    public void setChannelsMask(boolean[] channelsMask) {
        int length = Math.min(this.channelsMask.length, channelsMask.length);
        for (int i = 0; i < length; i++) {
            this.channelsMask[i] = channelsMask[i];
        }
    }

    public void setPreFilters(PreFilter[] preFilters) throws ApplicationException {
        int numbersOfSamplesInEachDataRecord[] = bdfConfig.getNumberOfSamplesInEachDataRecord();
        int length = Math.min(preFiltersList.length, preFilters.length);
        for (int i = 0; i < length; i++) {
            if (preFilters[i] != null) {
                if (numbersOfSamplesInEachDataRecord[i] % preFilters[i].getDivider() == 0) {
                    preFiltersList[i] = preFilters[i];
                    channelsList[i].setFrequency(channelsList[i].getFrequency() / preFilters[i].getDivider());
                    preFiltersList[i].addListener(channelsList[i]);
                } else {
                    String errorMsg = "Prefilters frequency dividers are not compatible with BdfProvider in DataStore";
                    throw new ApplicationException(errorMsg);
                }
            }
        }
    }

    public int getNumberOfChannels() {
        int numberOfChannels = getNumberOfSignals();
        for(boolean isActive : channelsMask) {
            if(!isActive) {
                numberOfChannels--;
            }
        }
        return numberOfChannels;
    }

    private void start() {
        updateTimer.start();
        if (startTime == -1) {
            startTime = System.currentTimeMillis() - (long) bdfConfig.getDurationOfDataRecord(); //1 second (1000 msec) duration of a data record
        }
        isReadingStarted = true;
    }


    @Override
    public void onDataRecordReceived(byte[] dataRecord) {
        numberOfDataRecords++;
        if (numberOfDataRecords == 0) {
            start();
        }

        if (SwingUtilities.isEventDispatchThread()) { // if data comes from gui thread we process it at once
            processDataRecord(dataRecord);
        } else {
            try {
                dataRecordsBuffer.put(dataRecord); // if data comes from non-gui thread we just buffer it
            } catch (InterruptedException e) {
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


    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }


    private void processBufferedData() {
        while (dataRecordsBuffer.size() > 0) {
            byte[] dataRecord = dataRecordsBuffer.poll();
            processDataRecord(dataRecord);
        }
    }

    private void processDataRecord(byte[] bdfDataRecord) {
        for (int signalNumber = 0; signalNumber < getNumberOfSignals(); signalNumber++) {
            int[] signalData = bdfParser.parseDataRecordSignal(bdfDataRecord, signalNumber);
            if (channelsMask[signalNumber]) {
                for (int dataValue : signalData) {
                    if (preFiltersList[signalNumber] != null) {
                        preFiltersList[signalNumber].add(dataValue);
                    } else {
                        channelsList[signalNumber].add(dataValue);
                    }
                }
            }
        }
    }

    private int getNumberOfSignals() {
        return channelsList.length;
    }



    private int channelToSignal(int channelNumber) {
        int number = -1;
        for (int i = 0; i < getNumberOfSignals(); i++) {
            if (channelsMask[i]) {
                number++;
                if (number == channelNumber) {
                    return i;
                }
            }
        }
        return -1;
    }
}
