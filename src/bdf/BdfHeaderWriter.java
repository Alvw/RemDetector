package bdf;


import device.BdfConfig;
import device.BdfSignalConfig;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.crostec.ads.AdsUtils.*;

class BdfHeaderWriter {

    public static byte[] createBdfHeader(BdfConfig bdfConfig, long startTime, int numberOfDataRecords) {
        Charset characterSet = Charset.forName("US-ASCII");
        StringBuilder bdfHeader = new StringBuilder();

        String identificationCode = "BIOSEMI";

        String localPatientIdentification =  bdfConfig.getLocalPatientIdentification();
        String localRecordingIdentification =   bdfConfig.getLocalRecordingIdentification();

        String startDateOfRecording = new SimpleDateFormat("dd.MM.yy").format(new Date(startTime));
        String startTimeOfRecording = new SimpleDateFormat("HH.mm.ss").format(new Date(startTime));

        int numberOfSignals = bdfConfig.getNumberOfSignals();  // number of signals in data record = number of active channels
        int numberOfBytesInHeaderRecord = 256 * (1 + numberOfSignals);
        String versionOfDataFormat = "24BIT";

        String channelsDigitalMaximum = "8388607";
        String channelsDigitalMinimum = "-8388608";

        String accelerometerDigitalMaximum = "30800";
        String accelerometerDigitalMinimum = "-30800";
        String accelerometerPhysicalMaximum = "2";
        String accelerometerPhysicalMinimum = "-2";

        bdfHeader.append(adjustLength(identificationCode, 7));  //7 not 8 because first non ascii byte we will add later
        bdfHeader.append(adjustLength(localPatientIdentification, 80));
        bdfHeader.append(adjustLength(localRecordingIdentification, 80));
        bdfHeader.append(startDateOfRecording);
        bdfHeader.append(startTimeOfRecording);
        bdfHeader.append(adjustLength(Integer.toString(numberOfBytesInHeaderRecord), 8));
        bdfHeader.append(adjustLength(versionOfDataFormat, 44));
        bdfHeader.append(adjustLength(Integer.toString(numberOfDataRecords), 8));
        bdfHeader.append(adjustLength(String.format("%.6f", bdfConfig.getDurationOfADataRecord()).replace(",", "."), 8));
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
        List<BdfSignalConfig> signalConfigList = bdfConfig.getSignalConfigList();
        for (int i = 0; i < signalConfigList.size(); i++) {
            BdfSignalConfig bdfSignalConfig = signalConfigList.get(i);
                labels.append(adjustLength(bdfSignalConfig.getLabel(), 16));
                transducerTypes.append(adjustLength("Unknown", 80));
                physicalDimensions.append(adjustLength(bdfSignalConfig.getPhysicalDimension(), 8));
               int physicalMaximum = bdfSignalConfig.getPhysicalMax();
               int physicalMinimum = bdfSignalConfig.getPhysicalMin();
               int digitalMax = bdfSignalConfig.getDigitalMax();
               int digitalMin = bdfSignalConfig.getDigitalMin();

                physicalMinimums.append(adjustLength(String.valueOf(physicalMinimum), 8));
                physicalMaximums.append(adjustLength(String.valueOf(physicalMaximum), 8));
                digitalMinimums.append(adjustLength(String.valueOf(digitalMin), 8));
                digitalMaximums.append(adjustLength(String.valueOf(digitalMax), 8));
                preFilterings.append(adjustLength("None", 80));
                int nrOfSamplesInEachDataRecord = bdfSignalConfig.getNrOfSamplesInEachDataRecord();
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
        // add first non ascii byte  "255"
        ByteBuffer byteBuffer = ByteBuffer.allocate(bdfHeader.length() + 1);
        byteBuffer.put((byte) 255);
        byteBuffer.put(bdfHeader.toString().getBytes(characterSet));
        return byteBuffer.array();
    }
}
