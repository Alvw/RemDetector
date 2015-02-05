package device.impl2ch;

import bdf.BdfParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

abstract class FrameDecoderDorokhov implements FrameDecoder{

    public static final byte START_FRAME_MARKER = (byte)(0xAA & 0xFF);
    public static final byte STOP_FRAME_MARKER = (byte)(0x55 & 0xFF);
    private int frameIndex;
    private int rawFrameSize;
    private int numberOf3ByteSamples;
    private int decodedFrameSize;
    private byte[] rawFrame;
    AdsConfiguration adsConfiguration;
    private static final Log log = LogFactory.getLog(FrameDecoder.class);



    public FrameDecoderDorokhov(AdsConfiguration configuration) {
        //  decodedFrameSize = 25;
//        rawFrameSize = 71;
        adsConfiguration = configuration;
        numberOf3ByteSamples = getNumberOf3ByteSamples(configuration);
        rawFrameSize = getRawFrameSize(configuration);
        decodedFrameSize = getDecodedFrameSize(configuration);
        rawFrame = new byte[rawFrameSize];
        log.info("Com port frame size: " + rawFrameSize + " bytes");
    }

    public void onByteReceived(byte inByte) {
        if (frameIndex == 0 && inByte == START_FRAME_MARKER) {
            rawFrame[frameIndex] = inByte;
            frameIndex++;
        } else if (frameIndex == 1 && inByte == START_FRAME_MARKER) {
            rawFrame[frameIndex] = inByte;
            frameIndex++;
        } else if (frameIndex > 1 && frameIndex < (rawFrameSize - 1)) {
            rawFrame[frameIndex] = inByte;
            frameIndex++;
        } else if (frameIndex == (rawFrameSize - 1)) {
            rawFrame[frameIndex] = inByte;
            if (inByte != STOP_FRAME_MARKER) {
               log.warn("Lost Stop Frame Marker");
            }
            frameIndex = 0;
            onFrameReceived();
        } else {
           // log.warn("Lost Frame. Frame index = " + frameIndex + " inByte = " + inByte);
            frameIndex = 0;
        }
    }

    private void onFrameReceived() {

        int counter = BdfParser.bytesToUnsignedInt(rawFrame[2],rawFrame[3]);

        byte[] decodedFrame = new byte[decodedFrameSize];
        int rawFrameOffset = 4;
        int decodedFrameOffset = 0;
        for (int i = 0; i < numberOf3ByteSamples*3; i++) {
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

        notifyListeners(decodedFrame);
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
            if (adsChannelConfiguration.isEnabled) {
                int divider = adsChannelConfiguration.getDivider().getValue();
                int maxDiv = adsConfiguration.getDeviceType().getMaxDiv().getValue();
                result += (maxDiv / divider);
            }
        }
        return result;
    }

    public abstract void notifyListeners(byte[] decodedFrame);
}
