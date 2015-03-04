package device.ads2ch_v1;

import bdf.BdfParser;
import device.impl2ch.AdsChannelConfiguration;
import device.impl2ch.ComPortListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

abstract class FrameDecoderDorokhov implements ComPortListener {

    private static final Log log = LogFactory.getLog(ComPortListener.class);
    public static final byte START_FRAME_MARKER = (byte) (0xAA & 0xFF);
    public static final byte STOP_FRAME_MARKER = (byte) (0x55 & 0xFF);
    private int frameIndex;
    private int dataRecordSize;
    private int frameSize;//todo refactor names
    private int numberOf3ByteSamples;
    private int decodedFrameSize;
    private byte[] rawFrame;
    private AdsConfiguration adsConfiguration;
    private int previousFrameCounter = -1;
    private byte[] lostFrame;


    public FrameDecoderDorokhov(AdsConfiguration configuration) {
        adsConfiguration = configuration;
        numberOf3ByteSamples = getNumberOf3ByteSamples(configuration);
        dataRecordSize = getRawFrameSize(configuration);
        decodedFrameSize = getDecodedFrameSize(configuration);
        lostFrame = new byte[decodedFrameSize];
        rawFrame = new byte[dataRecordSize];
        log.info("Com port frame size: " + dataRecordSize + " bytes");
    }

    public void onByteReceived(byte inByte) {
        if (frameIndex == 0 && inByte == START_FRAME_MARKER) {
            rawFrame[frameIndex] = inByte;
            frameIndex++;
        } else if (frameIndex == 1 && inByte == START_FRAME_MARKER) {  //receiving data record
            rawFrame[frameIndex] = inByte;
            frameSize = dataRecordSize;
            frameIndex++;
        } else if (frameIndex == 1 && inByte == STOP_FRAME_MARKER) {  //receiving message
            rawFrame[frameIndex] = inByte;
            frameIndex++;
        } else if (frameIndex == 2) {
            rawFrame[frameIndex] = inByte;
            frameIndex++;
            if (rawFrame[1] == STOP_FRAME_MARKER) {   //message length
                frameSize = inByte;
            }
        } else if (frameIndex > 2 && frameIndex < (frameSize - 1)) {
            rawFrame[frameIndex] = inByte;
            frameIndex++;
        } else if (frameIndex == (frameSize - 1)) {
            rawFrame[frameIndex] = inByte;
            if (inByte == STOP_FRAME_MARKER) {
                onFrameReceived();
            }
            frameIndex = 0;
        } else {
            log.warn("Lost Frame. Frame index = " + frameIndex + " inByte = " + inByte);
            frameIndex = 0;
        }
    }

    private void onFrameReceived() {
        if (rawFrame[1] == START_FRAME_MARKER) {
            onDataRecordReceived();
        }
        if (rawFrame[1] == STOP_FRAME_MARKER) {
            onMessageReceived();
        }
    }

    private void onDataRecordReceived() {
        int counter = BdfParser.bytesToUnsignedInt(rawFrame[2], rawFrame[3]);
        byte[] decodedFrame = new byte[decodedFrameSize];
        int rawFrameOffset = 4;
        int decodedFrameOffset = 0;
        for (int i = 0; i < numberOf3ByteSamples * 3; i++) {
            decodedFrame[decodedFrameOffset++] = rawFrame[rawFrameOffset++];
        }

        if (adsConfiguration.isAccelerometerEnabled()) {
            for (int i = 0; i < 3; i++) {
                decodedFrame[decodedFrameOffset++] = rawFrame[rawFrameOffset++];
                decodedFrame[decodedFrameOffset++] = rawFrame[rawFrameOffset++];
                decodedFrame[decodedFrameOffset++] = 0;
            }
        }

        for (int i = 0; i < 3; i++) {
            decodedFrame[decodedFrameOffset++] = 0;
        }
        int numberOfLostFrames = getNumberOfLostFrames(counter);
        if (numberOfLostFrames > 0) {
            log.info(numberOfLostFrames + " lost frames");
        }
        for (int i = 0; i < numberOfLostFrames; i++) {
            // byte[] lostFrame = new byte[decodedFrameSize];
            // System.arraycopy( decodedFrame, 0, lostFrame, 0, decodedFrameSize);
            // decodedFrame[decodedFrameSize - 1] = 1;
            notifyListeners(lostFrame);
        }
        notifyListeners(decodedFrame);
    }

    private void onMessageReceived() {
        if (rawFrame[3] == (byte) (0xA0 & 0xFF)) {
            log.info("Hello message received");
        }
    }

    private int getRawFrameSize(AdsConfiguration adsConfiguration) {
        int result = 2;//маркер начала фрейма
        result += 2; // счечик фреймов
        result += 3 * getNumberOf3ByteSamples(adsConfiguration);
        if (adsConfiguration.isAccelerometerEnabled()) {
            result += 6;
        }
        if (adsConfiguration.isLoffEnabled()) {
            result += 1;
        }
        result += 1;//footer
        return result;
    }

    private int getDecodedFrameSize(AdsConfiguration configuration) {
        int result = 0;
        result += getNumberOf3ByteSamples(configuration);
        if (configuration.isAccelerometerEnabled()) {
            result += 3;
        }
        result += 1;
        return result * 3;
    }

    private int getNumberOf3ByteSamples(AdsConfiguration adsConfiguration) {
        int result = 0;
        for (AdsChannelConfiguration adsChannelConfiguration : adsConfiguration.getAdsChannels()) {
            if (adsChannelConfiguration.isEnabled()) {
                int divider = adsChannelConfiguration.getDivider().getValue();
                int maxDiv = AdsConfiguration.MAX_DIVIDER.getValue();
                result += (maxDiv / divider);
            }
        }
        return result;
    }

    private int getNumberOfLostFrames(int frameCounter) {
        if (previousFrameCounter == -1) {
            previousFrameCounter = frameCounter;
            return 0;
        }
        int result = frameCounter - previousFrameCounter;
        result = result > 0 ? result : (result + 256);
        previousFrameCounter = frameCounter;
        return result - 1;
    }

    public abstract void notifyListeners(byte[] decodedFrame);
}
