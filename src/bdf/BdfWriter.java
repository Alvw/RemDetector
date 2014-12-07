package bdf;

import dreamrec.ApplicationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class BdfWriter implements BdfListener {

    private static final Log LOG = LogFactory.getLog(BdfWriter.class);
    private final RecordingBdfConfig recordingBdfConfig;
    private RandomAccessFile fileToSave;
    private long startRecordingTime;
    private long stopRecordingTime;
    private int numberOfDataRecords;
    private boolean stopRecordingRequest;


    public BdfWriter(RecordingBdfConfig recordingBdfConfig, File fileToSave)  throws ApplicationException {
        this.recordingBdfConfig = recordingBdfConfig;
        try {
            this.fileToSave = new RandomAccessFile(fileToSave, "rw");
        } catch (FileNotFoundException e) {
            LOG.error(e);
            throw new ApplicationException("File: " + fileToSave.getAbsolutePath() + "could not be written");
        }
    }


    @Override
    public synchronized void onDataRecordReceived(byte[] bdfDataRecord) {
        if (!stopRecordingRequest) {
            if (numberOfDataRecords == 0) {
                startRecordingTime = System.currentTimeMillis() - (long) recordingBdfConfig.getDurationOfDataRecord(); //1 second (1000 msec) duration of a data record
                recordingBdfConfig.setStartTime(startRecordingTime);
                recordingBdfConfig.setNumberOfDataRecords(-1);
                try {
                    fileToSave.write(BdfHeaderWriter.createBdfHeader(recordingBdfConfig));
                } catch (IOException e) {
                    LOG.error(e);
                    throw new RuntimeException(e);
                }
            }
            numberOfDataRecords++;
            stopRecordingTime = System.currentTimeMillis();
            try {
                fileToSave.write(bdfDataRecord);
            } catch (IOException e) {
                LOG.error(e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public synchronized void onStopReading() {
        if (stopRecordingRequest) return;
        stopRecordingRequest = true;
        // if BdfProvide(device) don't have quartz we should calculate actualDurationOfDataRecord
        double actualDurationOfDataRecord = (stopRecordingTime - startRecordingTime) * 0.001 / numberOfDataRecords;
        recordingBdfConfig.setStartTime(startRecordingTime);
        recordingBdfConfig.setNumberOfDataRecords(numberOfDataRecords);

        try {
            fileToSave.seek(0);
           // fileToSave.write(BdfHeaderWriter.createBdfHeader(recordingBdfConfig, actualDurationOfDataRecord));
            fileToSave.write(BdfHeaderWriter.createBdfHeader(recordingBdfConfig));
            fileToSave.close();
        } catch (IOException e) {
            LOG.error(e);
            throw new RuntimeException(e);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SS");
        LOG.info("Start recording time = " + startRecordingTime + " (" + dateFormat.format(new Date(startRecordingTime)));
        LOG.info("Stop recording time = " + stopRecordingTime + " (" + dateFormat.format(new Date(stopRecordingTime)));
        LOG.info("Number of data records = " + numberOfDataRecords);
        LOG.info("Duration of a data record = " + actualDurationOfDataRecord);
    }
}
