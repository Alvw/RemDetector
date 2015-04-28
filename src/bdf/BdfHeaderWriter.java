package bdf;


import dreamrec.ApplicationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BdfHeaderWriter {
    private static final Log LOG = LogFactory.getLog(BdfHeaderWriter.class);

    public static void writeBdfHeader(RecordingBdfConfig recordingBdfConfig, File fileToSave) throws ApplicationException {
        RandomAccessFile fileAccess;
        try {
            fileAccess = new RandomAccessFile(fileToSave, "rw");

        } catch (FileNotFoundException e) {
            LOG.error(e);
            throw new ApplicationException("File: " + fileToSave.getAbsolutePath() + "could not be written");
        }

        try {
            fileAccess.write(createBdfHeader(recordingBdfConfig));
            fileAccess.close();
        } catch (IOException e) {
            LOG.error(e);
            throw new RuntimeException(e);
        }
    }

    public static byte[] createBdfHeader(RecordingBdfConfig recordingBdfConfig) {
        return createBdfHeader(recordingBdfConfig, 0);
    }

    public static byte[] createBdfHeader(RecordingBdfConfig recordingBdfConfig, double actualDurationOfDataRecord) {
        double durationOfDataRecord = recordingBdfConfig.getDurationOfDataRecord();
        if (actualDurationOfDataRecord > 0) {
            durationOfDataRecord = actualDurationOfDataRecord;
        }
        boolean isBdf = true;
        if (recordingBdfConfig.getNumberOfBytesInDataFormat() == 2) {
            isBdf = false;   // edf file
        }

        Charset characterSet = Charset.forName("US-ASCII");
        StringBuilder bdfHeader = new StringBuilder();


        String identificationCode = "BIOSEMI";  // bdf
        if (!isBdf) {   // edf
            identificationCode = "";
        }

        String localPatientIdentification = recordingBdfConfig.getPatientIdentification();
        String localRecordingIdentification = recordingBdfConfig.getRecordingIdentification();
        long startTime = recordingBdfConfig.getStartTime();
        int numberOfDataRecords = recordingBdfConfig.getNumberOfDataRecords();

        String startDateOfRecording = new SimpleDateFormat("dd.MM.yy").format(new Date(startTime));
        String startTimeOfRecording = new SimpleDateFormat("HH.mm.ss").format(new Date(startTime));

        int numberOfSignals = recordingBdfConfig.getNumberOfSignals();  // number of signals in data record = number of active channels
        int numberOfBytesInHeaderRecord = 256 * (1 + numberOfSignals);

        String versionOfDataFormat = "24BIT"; //bdf
        if (!isBdf) {   // edf
            versionOfDataFormat = "BIOSEMI";
        }


        bdfHeader.append(adjustLength(identificationCode, 7));  //7 not 8 because first non ascii byte (or "0" for edf) we will add later
        bdfHeader.append(adjustLength(localPatientIdentification, 80));
        bdfHeader.append(adjustLength(localRecordingIdentification, 80));
        bdfHeader.append(startDateOfRecording);
        bdfHeader.append(startTimeOfRecording);
        bdfHeader.append(adjustLength(Integer.toString(numberOfBytesInHeaderRecord), 8));
        bdfHeader.append(adjustLength(versionOfDataFormat, 44));
        bdfHeader.append(adjustLength(Integer.toString(numberOfDataRecords), 8));
        bdfHeader.append(adjustLength(double2String(durationOfDataRecord), 8));
        bdfHeader.append(adjustLength(Integer.toString(numberOfSignals), 4));


        StringBuilder labels = new StringBuilder();
        StringBuilder transducerTypes = new StringBuilder();
        StringBuilder physicalDimensions = new StringBuilder();
        StringBuilder physicalMinimums = new StringBuilder();
        StringBuilder physicalMaximums = new StringBuilder();
        StringBuilder digitalMinimums = new StringBuilder();
        StringBuilder digitalMaximums = new StringBuilder();
        StringBuilder preFilterings = new StringBuilder();
        StringBuilder samplesNumbers = new StringBuilder();
        StringBuilder reservedForChannels = new StringBuilder();
        SignalConfig[] signalConfigList = recordingBdfConfig.getSignalConfigs();
        for (int i = 0; i < signalConfigList.length; i++) {
            SignalConfig signalConfig = signalConfigList[i];
            labels.append(adjustLength(signalConfig.getLabel(), 16));
            transducerTypes.append(adjustLength(signalConfig.getTransducerType(), 80));
            Calibration calibration = signalConfig.getCalibration();
            physicalDimensions.append(adjustLength(calibration.getPhysicalDimension(), 8));
            double physicalMaximum = calibration.getPhysicalMax();
            double physicalMinimum = calibration.getPhysicalMin();
            int digitalMax = calibration.getDigitalMax();
            int digitalMin = calibration.getDigitalMin();

            physicalMinimums.append(adjustLength(double2String(physicalMinimum), 8));
            physicalMaximums.append(adjustLength(double2String(physicalMaximum), 8));
            digitalMinimums.append(adjustLength(String.valueOf(digitalMin), 8));
            digitalMaximums.append(adjustLength(String.valueOf(digitalMax), 8));
            preFilterings.append(adjustLength(signalConfig.getPrefiltering(), 80));
            int nrOfSamplesInEachDataRecord = signalConfig.getNumberOfSamplesInEachDataRecord();
            samplesNumbers.append(adjustLength(Integer.toString(nrOfSamplesInEachDataRecord), 8));
            reservedForChannels.append(adjustLength("", 32));
        }

        bdfHeader.append(labels);
        bdfHeader.append(transducerTypes);
        bdfHeader.append(physicalDimensions);
        bdfHeader.append(physicalMinimums);
        bdfHeader.append(physicalMaximums);
        bdfHeader.append(digitalMinimums);
        bdfHeader.append(digitalMaximums);
        bdfHeader.append(preFilterings);
        bdfHeader.append(samplesNumbers);
        bdfHeader.append(reservedForChannels);
        // reserve space for first byte
        ByteBuffer byteBuffer = ByteBuffer.allocate(bdfHeader.length() + 1);
        if (isBdf) {
            // add first non ascii byte "255"
            byteBuffer.put((byte) 255);
        } else {
            // add first byte "0"
            bdfHeader.insert(0, "0");
        }

        byteBuffer.put(bdfHeader.toString().getBytes(characterSet));
        return byteBuffer.array();
    }

    /**
     * if the String.length() is more then the given length we cut the String
     * if the String.length() is less then the given length we append spaces to the end of the String
     */
    private static String adjustLength(String text, int length) {
        StringBuilder sB = new StringBuilder(text);
        if (text.length() > length) {
            sB.delete(length, text.length());
        } else {
            for (int i = text.length(); i < length; i++) {
                sB.append(" ");
            }
        }
        return sB.toString();
    }

    private static String double2String(double value ) {
        return String.format("%f", value).replace(",", ".");
    }
}
