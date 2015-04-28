package dreamrec;

import bdf.*;
import data.DataList;
import data.ScalingImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import prefilters.PreFilter;
import prefilters.PreFilterAdapter;

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
    private volatile int numberOfDataRecords = -1;

    public DataStore(BdfProvider bdfProvider) {
        bdfProvider.addBdfDataListener(this);
        bdfConfig = bdfProvider.getBdfConfig();
        SignalConfig[] signalConfigs = bdfConfig.getSignalConfigs();
        int numberOfSignals = signalConfigs.length;
        int[] numberOfSamplesInEachDataRecords = new int[numberOfSignals];
        for(int i = 0; i < numberOfSignals; i++) {
            numberOfSamplesInEachDataRecords[i] = signalConfigs[i].getNumberOfSamplesInEachDataRecord();
        }

        bufferSize = (int) (BUFFER_CAPACITY_SECONDS / bdfConfig.getDurationOfDataRecord());
        bufferSize = bufferSize / numberOfSignals;
        dataRecordsBuffer = new LinkedBlockingQueue<byte[]>(bufferSize);

        bdfParser = new BdfParser(bdfConfig.getNumberOfBytesInDataFormat(), numberOfSamplesInEachDataRecords);

        preFiltersList = new PreFilter[numberOfSignals];
        channelsMask = new boolean[numberOfSignals];
        for (int i = 0; i < numberOfSignals; i++) {
            channelsMask[i] = true;
        }

        channelsList = new DataList[numberOfSignals];
        for (int i = 0; i < numberOfSignals; i++) {
            double frequency = numberOfSamplesInEachDataRecords[i] / bdfConfig.getDurationOfDataRecord();
            channelsList[i] = new DataList();
            ScalingImpl scaling = new ScalingImpl();
            scaling.setSamplingInterval(1 / frequency);
            scaling.setTimeSeries(true);
            scaling.setDataGain(signalConfigs[i].getCalibration().getGain());
            scaling.setDataOffset(signalConfigs[i].getCalibration().getOffset());
            scaling.setDataDimension(signalConfigs[i].getCalibration().getPhysicalDimension());
            channelsList[i].setScaling(scaling);
        }

        updateTimer = new Timer(UPDATE_DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                processBufferedData();

                fireDataUpdated();

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
        SignalConfig[] signalConfigs = bdfConfig.getSignalConfigs();
        int length = Math.min(preFiltersList.length, preFilters.length);
        for (int i = 0; i < length; i++) {
            if (preFilters[i] != null) {
                if (signalConfigs[i].getNumberOfSamplesInEachDataRecord() % preFilters[i].getDivider() == 0) {
                    preFiltersList[i] = preFilters[i];
                    ScalingImpl scaling =  (ScalingImpl)channelsList[i].getScaling();
                    scaling.setSamplingInterval(scaling.getSamplingInterval() * preFilters[i].getDivider());
                    preFiltersList[i].addListener(new PreFilterAdapter(channelsList[i]));
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


    public DataList getChannelData(int channelNumber) {
        int signalNumber = channelToSignal(channelNumber);
        return channelsList[signalNumber];
    }

    public DataList getSignalData(int signalNumber) {
        return channelsList[signalNumber];
    }


    public void setStartTime(long startTime) {
        for (int i = 0; i < channelsList.length; i++) {
            ScalingImpl scaling = (ScalingImpl) channelsList[i].getScaling();
            scaling.setStart(startTime);
        }
    }

    public void addListener(DataStoreListener dataStoreListener) {
        updateListeners.add(dataStoreListener);
    }

    private void fireDataUpdated() {
        for (DataStoreListener listener : updateListeners) {
            listener.onDataUpdate();
        }
    }


    private void start() {
        updateTimer.start();
        if (getStartTime() <= 0) {
            long startTime = System.currentTimeMillis() - (long) bdfConfig.getDurationOfDataRecord(); //1 second (1000 msec) duration of a data record
            setStartTime(startTime);
        }

    }

    private long getStartTime() {
        long startTime = 0;
        if(channelsList.length > 0 && channelsList[0] != null) {
            startTime = (long)channelsList[0].getScaling().getStart();
        }
        return startTime;
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
