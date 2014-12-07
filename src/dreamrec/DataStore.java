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

    private int BUFFER_CAPACITY_SECONDS = 60 * 20; // to protect from OutOfMemoryError
    private int bufferSize;
    private ArrayList<DataStoreListener> updateListeners = new ArrayList<DataStoreListener>();

    private DataList[] channelsList;
    private int[] dividers; // frequency dividers or 0 - if signal is disabled
    private Timer updateTimer;
    private int UPDATE_DELAY = 250;

    private BdfParser bdfParser;
    private BdfConfig bdfConfig;
    private volatile boolean isReadingStopped = false;
    private volatile boolean isReadingStarted = false;
    private volatile  int numberOfDataRecords = -1;
    private volatile long startTime;

    public void clear() {
        updateTimer.stop();
        for (DataList channel : channelsList) {
            if(channel != null) {
                channel.clear();
            }
        }
        dataRecordsBuffer.clear();
    }

    public DataStore(BdfProvider bdfProvider, int[] frequencyDividers) throws ApplicationException {
        bdfConfig = bdfProvider.getBdfConfig();
        int[] numbersOfSamplesInEachDataRecord = bdfConfig.getNumbersOfSamplesInEachDataRecord();
        dividers = new int[bdfConfig.getNumberOfSignals()];
        for (int i = 0; i < dividers.length; i++) {
            dividers[i] = 1;
        }
        if (frequencyDividers != null) {
            int length = Math.min(frequencyDividers.length, dividers.length);
            for (int i = 0; i < length; i++) {
                if (frequencyDividers[i] == 0) {
                    dividers[i] = frequencyDividers[i];
                } else if (numbersOfSamplesInEachDataRecord[i] % frequencyDividers[i] == 0) {
                    dividers[i] = frequencyDividers[i];
                } else {
                    String errorMsg = "Frequency dividers are not compatible with BdfProvider in DataStore";
                    throw new ApplicationException(errorMsg);
                }
            }
        }

        bdfProvider.addBdfDataListener(this);
        bufferSize = (int) (BUFFER_CAPACITY_SECONDS / bdfConfig.getDurationOfDataRecord());
        dataRecordsBuffer = new LinkedBlockingQueue<byte[]>(bufferSize);
        bdfParser = new BdfParser(bdfConfig);
        int numberOfSignals = bdfConfig.getNumberOfSignals();
        channelsList = new DataList[numberOfSignals];
        for (int i = 0; i < numberOfSignals; i++) {
            if (dividers[i] != 0) {
                channelsList[i] = new DataList();
                double frequency = numbersOfSamplesInEachDataRecord[i] / bdfConfig.getDurationOfDataRecord();
                channelsList[i].setFrequency(frequency / frequencyDividers[i]);
            }
        }

        updateTimer = new Timer(UPDATE_DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                processBufferedData();
                if(isReadingStarted) {
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
        updateTimer.start();
    }

    public DataStore(BdfProvider bdfProvider) throws ApplicationException {
        this(bdfProvider, null);
    }

    public int getNumberOfChannels() {
        return signalToChannel(getNumberOfSignals() - 1) + 1;
    }


    @Override
    public void onDataRecordReceived(byte[] dataRecord) {
        numberOfDataRecords++;
        if(numberOfDataRecords == 0) {
            if (startTime == -1) {
                startTime = System.currentTimeMillis() - (long) bdfConfig.getDurationOfDataRecord(); //1 second (1000 msec) duration of a data record
            }
            isReadingStarted = true;
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

    private int getNumberOfSignals() {
        return channelsList.length;
    }

    private int signalToChannel(int signalNumber) {
        int channelNumber = -1;
        for (int i = 0; i < signalNumber; i++) {
            if (signalNumber != 0) {
                channelNumber++;
            }
        }
        return channelNumber;
    }

    private int channelToSignal(int channelNumber) {
        int number = -1;
        for (int i = 0; i < getNumberOfSignals(); i++) {
            if (dividers[i] != 0) {
                number++;
                if (number == channelNumber) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void processDataRecord(byte[] bdfDataRecord) {
        for (int signalNumber = 0; signalNumber < getNumberOfSignals(); signalNumber++) {
            int[] signalData = bdfParser.parseDataRecordSignal(bdfDataRecord, signalNumber);
            int divider = dividers[signalNumber];
            if (divider == 1) {
                channelsList[signalNumber].add(signalData);
            } else if (divider != 0) { // adjust frequency
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
