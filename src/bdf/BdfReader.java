package bdf;

import dreamrec.ApplicationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class BdfReader implements BdfProvider {
    private static final Log log = LogFactory.getLog(BdfReader.class);
    private BufferedInputStream fileInputStream;
    private RecordingBdfConfig recordingBdfConfig;
    private int BUFFER_SIZE = 64 * 1028; // It is best to use buffer sizes that are multiples of 1024 bytes
    private boolean isFileOpen = false;
    private int totalNumberOfSamplesInEachDataRecord;


    private ArrayList<BdfListener> bdfListenersList = new ArrayList<BdfListener>();

    public BdfReader(File file) throws ApplicationException {
        try {
            BdfHeaderReader bdfHeaderReader = new BdfHeaderReader(file);
            recordingBdfConfig = bdfHeaderReader.getRecordingBdfConfig();
            totalNumberOfSamplesInEachDataRecord = getTotalNumberOfBytesInDataRecord();
             fileInputStream = new BufferedInputStream(new FileInputStream(file), BUFFER_SIZE);
            int numberOfBytesInHeader = 256 + 256 * recordingBdfConfig.getNumberOfSignals();
            if(fileInputStream.skip(numberOfBytesInHeader) == numberOfBytesInHeader) {
                isFileOpen = true;
            } else {
                throw new ApplicationException("Error while opening file " + file.getName());
            }

        } catch (IOException e) {
            log.error(e);
            throw new ApplicationException("Error while opening file " + file.getName());
        }
    }

    private int getTotalNumberOfBytesInDataRecord() {
        int totalNumberOfSamplesInEachDataRecord = 0;
        int[] numbersOfSamplesInEachDataRecord = recordingBdfConfig.getNumbersOfSamplesInEachDataRecord();
        for (int signalNumber = 0; signalNumber < recordingBdfConfig.getNumberOfSignals(); signalNumber++) {
            totalNumberOfSamplesInEachDataRecord += numbersOfSamplesInEachDataRecord[signalNumber];
        }
        return totalNumberOfSamplesInEachDataRecord * recordingBdfConfig.getNumberOfBytesInDataFormat();

    }


    public void readData() {
        byte[] dataRecord = new byte[totalNumberOfSamplesInEachDataRecord];
        try {
            while (isFileOpen &&  fileInputStream.read(dataRecord) == totalNumberOfSamplesInEachDataRecord) {
                for (BdfListener bdfListener : bdfListenersList) {
                    bdfListener.onDataRecordReceived(dataRecord);
                }
                dataRecord = new byte[totalNumberOfSamplesInEachDataRecord];
            }
            stopReading();
        } catch (IOException e) {
            log.error(e);
        }
    }

    @Override
    public void startReading() {
        if (SwingUtilities.isEventDispatchThread()){ // if file reading starts from gui we read it in new Thread
            new Thread(new Runnable() {
                @Override
                public void run() {
                    readData();
                }
            }).start();
        }else{
            readData(); // // if file reading starts from non-gui Thread we read it in the same thread
        }

    }

    @Override
    public void stopReading() {
        try {
            fileInputStream.close();
            isFileOpen = false;
        } catch (IOException e) {
            log.error(e);
        }

        for (BdfListener bdfBdfListener : bdfListenersList) {
            bdfBdfListener.onStopReading();
        }
    }

    @Override
    public void addBdfDataListener(BdfListener bdfBdfListener) {
        bdfListenersList.add(bdfBdfListener);
    }


    @Override
    public RecordingBdfConfig getBdfConfig() {
        return recordingBdfConfig;
    }

}
