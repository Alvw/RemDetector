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
    private BdfConfig bdfConfig;
    private int numberOfBytesInDataRecord;
    private int BUFFER_SIZE = 64 * 1028; // It is best to use buffer sizes that are multiples of 1024 bytes
    private boolean isFileOpen = false;


    private ArrayList<BdfListener> bdfListenersList = new ArrayList<BdfListener>();

    public BdfReader(File file) throws ApplicationException {
        try {
            BdfHeaderReader bdfHeaderReader = new BdfHeaderReader(file);
            bdfConfig = bdfHeaderReader.getBdfConfig();
            numberOfBytesInDataRecord = bdfConfig.getTotalNumberOfSamplesInEachDataRecord() * bdfConfig.getNumberOfBytesInDataFormat();
            fileInputStream = new BufferedInputStream(new FileInputStream(file), BUFFER_SIZE);
            int numberOfBytesInHeader = 256 + 256 * bdfConfig.getNumberOfSignals();
            fileInputStream.skip(numberOfBytesInHeader);
            isFileOpen = true;
        } catch (IOException e) {
            log.error(e);
            throw new ApplicationException("Error while opening file " + file.getName());
        }
    }


    public void readData() {
        byte[] dataRecord = new byte[numberOfBytesInDataRecord];
        try {
            while (isFileOpen && fileInputStream.read(dataRecord) != -1) {
                for (BdfListener bdfListener : bdfListenersList) {
                    bdfListener.onDataRecordReceived(dataRecord);
                }
                dataRecord = new byte[numberOfBytesInDataRecord];
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
    public BdfConfig getBdfConfig() {
        return bdfConfig;
    }

}
