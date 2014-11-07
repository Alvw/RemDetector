package bdf;

import com.crostec.ads.AdsUtils;
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
public class BdfWriter implements BdfListener {

    private static final Log LOG = LogFactory.getLog(BdfWriter.class);
    private final BdfConfig bdfConfig;
    private RandomAccessFile fileToSave;
    private long startRecordingTime;
    private long stopRecordingTime;
    private int numberOfDataRecords;
    private boolean stopRecordingRequest;

    public BdfWriter(BdfConfig bdfConfig) {
        this(bdfConfig, new SimpleDateFormat("dd-MM-yyyy_HH-mm").format(new Date(System.currentTimeMillis())) + ".bdf");
    }

    public BdfWriter(BdfConfig bdfConfig, String fileToSave) {
       this.bdfConfig = bdfConfig;
        try {
            this.fileToSave = new RandomAccessFile(fileToSave, "rw");
        } catch (FileNotFoundException e) {
            LOG.error(e);
        }
    }

    @Override
    public synchronized void onDataRecordReceived(byte[] bdfDataRecord) {
        if (!stopRecordingRequest) {
            if (numberOfDataRecords == 0) {
                startRecordingTime = System.currentTimeMillis() - (long)bdfConfig.getDurationOfDataRecord(); //1 second (1000 msec) duration of a data record
                try {
                    fileToSave.write(BdfHeaderWriter.createBdfHeader(bdfConfig, startRecordingTime, -1));
                } catch (IOException e) {
                    LOG.error(e);
                    throw new RuntimeException(e);
                }
            }
            numberOfDataRecords++;
            stopRecordingTime = System.currentTimeMillis();
/*            for (int i = 0; i < bdfDataRecord.length; i++) {
                for(int j = 0; j < bdfDataRecord[i].length; j++)  {
                    try {
                        fileToSave.write(AdsUtils.to24BitLittleEndian(bdfDataRecord[i][j]));
                    } catch (IOException e) {
                        LOG.error(e);
                        throw new RuntimeException(e);
                    }
                }

            }*/
        }
    }

    @Override
    public synchronized void onStopReading() {
        if (stopRecordingRequest) return;
        stopRecordingRequest = true;
        double durationOfDataRecord = (stopRecordingTime - startRecordingTime) * 0.001 / numberOfDataRecords;
        bdfConfig.setDurationOfDataRecord(durationOfDataRecord);
        try {
            fileToSave.seek(0);
            fileToSave.write(BdfHeaderWriter.createBdfHeader(bdfConfig,startRecordingTime,numberOfDataRecords));
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
}
