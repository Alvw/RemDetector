package device.impl2ch;


import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static device.impl2ch.AdsUtils.*;

class BdfHeaderWriter {

    public static byte[] createBdfHeader(BdfHeaderData bdfHeaderData) {
        Charset characterSet = Charset.forName("US-ASCII");
        StringBuilder bdfHeader = new StringBuilder();

        String identificationCode = "BIOSEMI";

        String localPatientIdentification = "Patient: " + bdfHeaderData.getPatientIdentification();
        String localRecordingIdentification = "Record: " + bdfHeaderData.getRecordingIdentification();

        String startDateOfRecording = new SimpleDateFormat("dd.MM.yy").format(new Date(bdfHeaderData.getStartRecordingTime()));
        String startTimeOfRecording = new SimpleDateFormat("HH.mm.ss").format(new Date(bdfHeaderData.getStartRecordingTime()));

        int numberOfSignals = getDividersForActiveChannels(bdfHeaderData.getAdsConfiguration()).size();  // number of signals in data record = number of active channels
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
        bdfHeader.append(adjustLength(Integer.toString(bdfHeaderData.getNumberOfDataRecords()), 8));
        bdfHeader.append(adjustLength(String.format("%.6f", bdfHeaderData.getDurationOfDataRecord()).replace(",", "."), 8));
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
        AdsConfiguration adsConfiguration = bdfHeaderData.getAdsConfiguration();
        List<AdsChannelConfiguration> channelConfigurations = adsConfiguration.getAdsChannels();
        for (int i = 0; i < channelConfigurations.size(); i++) {
            if (channelConfigurations.get(i).isEnabled) {
                labels.append(adjustLength(bdfHeaderData.getAdsChannelNames().get(i), 16));
                transducerTypes.append(adjustLength("Unknown", 80));
                physicalDimensions.append(adjustLength("uV", 8));
                int physicalMaximum = 2400000/channelConfigurations.get(i).getGain().getValue();
                physicalMinimums.append(adjustLength("-" + physicalMaximum, 8));
                physicalMaximums.append(adjustLength("" + physicalMaximum, 8));
                digitalMinimums.append(adjustLength(channelsDigitalMinimum, 8));
                digitalMaximums.append(adjustLength(channelsDigitalMaximum, 8));
                preFilterings.append(adjustLength("None", 80));
                int nrOfSamplesInEachDataRecord = (int) Math.round(bdfHeaderData.getDurationOfDataRecord()) * adsConfiguration.getSps().getValue() /
                        channelConfigurations.get(i).getDivider().getValue();
                samplesNumbers.append(adjustLength(Integer.toString(nrOfSamplesInEachDataRecord), 8));
                reservedForChannels.append(adjustLength("", 32));
            }
        }
        if (adsConfiguration.isAccelerometerEnabled()) {
            for (int i = 0; i < 3; i++) {     //3 accelerometer chanels
                labels.append(adjustLength(bdfHeaderData.getAccelerometerChannelNames().get(i), 16));
                transducerTypes.append(adjustLength("None", 80));
                physicalDimensions.append(adjustLength("g", 8));
                physicalMinimums.append(adjustLength(accelerometerPhysicalMinimum, 8));
                physicalMaximums.append(adjustLength(accelerometerPhysicalMaximum, 8));
                digitalMinimums.append(adjustLength(accelerometerDigitalMinimum, 8));
                digitalMaximums.append(adjustLength(accelerometerDigitalMaximum, 8));
                preFilterings.append(adjustLength("None", 80));
                int nrOfSamplesInEachDataRecord = (int) Math.round(bdfHeaderData.getDurationOfDataRecord()) * adsConfiguration.getSps().getValue() /
                        adsConfiguration.getAccelerometerDivider().getValue();
                samplesNumbers.append(adjustLength(Integer.toString(nrOfSamplesInEachDataRecord), 8));
                reservedForChannels.append(adjustLength("", 32));
            }
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
