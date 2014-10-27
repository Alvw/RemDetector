package bdf;

import device.*;
import dreamrec.ApplicationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class BdfReader implements BdfDataSource {
    private static final Log log = LogFactory.getLog(BdfDataSource.class);
    private BufferedInputStream fileInputStream;
    private BdfConfig bdfConfig;
    private int numberOfBytesInDataFormat = 3;
    private int[] signalsConfig;
    private int bdfDataRecordLength;

    private ArrayList<BdfDataListener> bdfDataListenersList = new ArrayList<BdfDataListener>();

    public BdfReader(File file) throws ApplicationException {
        try {
            BdfHeaderReader bdfHeaderReader = new BdfHeaderReader(file);
            bdfConfig = bdfHeaderReader.getBdfConfig();
            fileInputStream = new BufferedInputStream(new FileInputStream(file));
            int numberOfBytesInHeader = 256 + 256 * bdfConfig.getNumberOfSignals();
            fileInputStream.skip(numberOfBytesInHeader);

            List<BdfSignalConfig> bdfSignalConfigList = bdfConfig.getSignalsConfigList();
            int numberOfSignals = bdfSignalConfigList.size();
            signalsConfig = new int[numberOfSignals];
            for (int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++) {
                BdfSignalConfig bdfSignalConfig = bdfSignalConfigList.get(signalNumber);
                bdfDataRecordLength += bdfSignalConfig.getNrOfSamplesInEachDataRecord();
                signalsConfig[signalNumber] = bdfSignalConfig.getNrOfSamplesInEachDataRecord();
            }

        } catch (IOException e) {
            log.error(e);
            throw new ApplicationException("Error while opening file " + file.getName());
        }
    }

    public boolean isBdfDataRecordAvailable() throws ApplicationException {
        try {
            if (fileInputStream.available() > bdfDataRecordLength * numberOfBytesInDataFormat) {
                return true;
            }
            return false;
        } catch (IOException e) {
            log.error(e);
            throw new ApplicationException("Error while reading from file" + e);
        }
    }

    private int[][] readBdfDataRecord() throws ApplicationException {
         int numberOfSignals =  signalsConfig.length;
         int[][] dataRecord = new int[numberOfSignals][];
        for (int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++) {
            int numberOfSamplesInSignal = signalsConfig[signalNumber];
            dataRecord[signalNumber] = new int[numberOfSamplesInSignal];
            for(int sampleNumber = 0; sampleNumber < numberOfSamplesInSignal; sampleNumber++) {
                dataRecord[signalNumber][sampleNumber] = readInt();
            }
        }
        return dataRecord;
    }

    /**
     * convert 24 bit (3 bytes) data format valid for Bdf to int data format and
     * Little_endian (for bdf) change to Big_endian (java)
     */
    private int readInt() throws ApplicationException {
        try {
            byte[] dataUnitBytes = new byte[numberOfBytesInDataFormat];
            fileInputStream.read(dataUnitBytes);
            int sizeOfInt = 4;
            ByteBuffer byteBufferForInt = ByteBuffer.allocate(sizeOfInt);
            // change the bytes order on the opposite
            for (int i = 0; i < numberOfBytesInDataFormat; i++) {
                int byteBufferForIntIndex = sizeOfInt - 1 - i;
                byteBufferForInt.put(byteBufferForIntIndex, dataUnitBytes[i]);
            }
            return byteBufferForInt.getInt();

        } catch (IOException e) {
            log.error(e);
            throw new ApplicationException("Error while reading from file" + e);
        }
    }

    @Override
    public void startReading() throws ApplicationException {
        while (isBdfDataRecordAvailable()) {
            int[][] dataRecord = readBdfDataRecord();
            for (BdfDataListener bdfDataListener : bdfDataListenersList) {
                bdfDataListener.onDataRecordReceived(dataRecord);
            }
        }

        stopReading();

    }

    @Override
    public void stopReading() throws ApplicationException {
        try {
            fileInputStream.close();
        } catch (IOException e) {
            log.error(e);
            throw new ApplicationException("Error while closing file" + e);
        }

        for (BdfDataListener bdfDataListener : bdfDataListenersList) {
            bdfDataListener.onStopReading();
        }
    }

    @Override
    public void addBdfDataListener(BdfDataListener bdfDataListener) {
        bdfDataListenersList.add(bdfDataListener);
    }

    @Override
    public void removeBdfDataListener(BdfDataListener bdfDataListener) {

    }

    @Override
    public BdfConfig getBdfConfig() {
        return bdfConfig;
    }

}
