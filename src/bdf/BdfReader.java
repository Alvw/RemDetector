package bdf;

import dreamrec.ApplicationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class BdfReader implements BdfProvider {
    private static final Log log = LogFactory.getLog(BdfReader.class);
    private BufferedInputStream fileInputStream;
    private BdfConfig bdfConfig;
    private int dataRecordBytesSize;
    private int BUFFER_SIZE = 128 * 1028; // It is best to use buffer sizes that are multiples of 1024 bytes

    private ArrayList<BdfListener> bdfListenersList = new ArrayList<BdfListener>();

    public BdfReader(File file) throws ApplicationException {
        try {
            BdfHeaderReader bdfHeaderReader = new BdfHeaderReader(file);
            bdfConfig = bdfHeaderReader.getBdfConfig();
            dataRecordBytesSize = bdfConfig.getTotalNumberOfSamplesInEachDataRecord() * bdfConfig.getNumberOfBytesInDataFormat();
            fileInputStream = new BufferedInputStream(new FileInputStream(file), BUFFER_SIZE);
            int numberOfBytesInHeader = 256 + 256 * bdfConfig.getNumberOfSignals();
            fileInputStream.skip(numberOfBytesInHeader);
        } catch (IOException e) {
            log.error(e);
            throw new ApplicationException("Error while opening file " + file.getName());
        }
    }


    @Override
    public void startReading() throws ApplicationException {
        byte[] dataRecord = new byte[dataRecordBytesSize];
        try {
            while (fileInputStream.read(dataRecord) != -1) {
                for (BdfListener bdfListener : bdfListenersList) {
                    bdfListener.onDataRecordReceived(dataRecord);
                }
                dataRecord = new byte[dataRecordBytesSize];
            }
            stopReading();
        } catch (IOException e) {
            log.error(e);
            throw new ApplicationException("Error while reading from file" + e);
        }

    }

    @Override
    public void stopReading() throws ApplicationException {
        try {
            fileInputStream.close();
        } catch (IOException e) {
            log.error(e);
            throw new ApplicationException("Error while closing file" + e);
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
