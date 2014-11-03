package device.implementation.impl2ch;


import device.DataListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class BdfWriter implements DataListener {

    private static final Log LOG = LogFactory.getLog(BdfWriter.class);
    private BdfHeaderData bdfHeaderData;
    private RandomAccessFile fileToSave;
    private JoinFramesUtility joinFramesUtility;
    private long startRecordingTime;
    private long stopRecordingTime;
    private int numberOfDataRecords;
    private boolean stopRecordingRequest;

    public BdfWriter(BdfHeaderData bdfHeaderData) {
        this.bdfHeaderData = bdfHeaderData;
        try {
            this.fileToSave = new RandomAccessFile(bdfHeaderData.getFileNameToSave(), "rw");
        } catch (FileNotFoundException e) {
            LOG.error(e);
        }
        joinFramesUtility = new JoinFramesUtility(bdfHeaderData.getAdsConfiguration()) {
            @Override
            public void notifyListeners(int[] joinedFrame) {
              // onBdfDataRecordReady(joinedFrame);
            }

            @Override
            public void onStopReading() {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

    @Override
    public synchronized void onDataRecordReceived(int[][] bdfDataRecord) {
        if (!stopRecordingRequest) {
            joinFramesUtility.onDataRecordReceived(bdfDataRecord);
        }
    }

    @Override
    public synchronized void onStopReading() {
        if(stopRecordingRequest) return;
        stopRecordingRequest = true;
        double durationOfDataRecord = (stopRecordingTime - startRecordingTime) * 0.001 / numberOfDataRecords;
        bdfHeaderData.setDurationOfDataRecord(durationOfDataRecord);
        bdfHeaderData.setNumberOfDataRecords(numberOfDataRecords);
        try {
            fileToSave.seek(0);
            fileToSave.write(BdfHeaderWriter.createBdfHeader(bdfHeaderData));
            fileToSave.close();
        } catch (IOException e) {
            LOG.error(e);
            throw new RuntimeException(e);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SS");
        LOG.info("Start recording time = " + startRecordingTime + " (" + dateFormat.format(new Date(startRecordingTime)));
        LOG.info("Stop recording time = " + stopRecordingTime + " (" + dateFormat.format(new Date(stopRecordingTime)));
        LOG.info("Number of data records = " + numberOfDataRecords);
        LOG.info("Duration of a data record = " + durationOfDataRecord);
    }

    private void onBdfDataRecordReady(int[] dataFrame) {
        if (numberOfDataRecords == 0) {
            startRecordingTime = System.currentTimeMillis() - 1000; //1 second (1000 msec) duration of a data record
            bdfHeaderData.setStartRecordingTime(startRecordingTime);
            try {
                fileToSave.write(BdfHeaderWriter.createBdfHeader(bdfHeaderData));
            } catch (IOException e) {
                LOG.error(e);
                throw new RuntimeException(e);
            }
        }
        numberOfDataRecords++;
        stopRecordingTime = System.currentTimeMillis();
        for (int i = 0; i < dataFrame.length; i++) {
            try {
                fileToSave.write(AdsUtils.to24BitLittleEndian(dataFrame[i]));
            } catch (IOException e) {
                LOG.error(e);
                throw new RuntimeException(e);
            }
        }
    }


}
