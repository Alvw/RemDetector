package device.ads2ch_v0;

import device.AdsConfiguration;
import device.FrameDecoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class FrameDecoderCh2V0 extends FrameDecoder {

    public static final byte START_FRAME_MARKER = (byte)(254 & 0xFF);
    private int index;
    private int inputFrameSize;
    int outputFrameSize = 0;
    private byte[] inputFrame;
    private static final Log log = LogFactory.getLog(FrameDecoderCh2V0.class);

    public FrameDecoderCh2V0(AdsConfiguration adsConfiguration) {
        int totalNumberOfDataSamples = adsConfiguration.getTotalNumberOfDataSamplesInEachDataRecord();
        int numberOfBytesInDataFormat = adsConfiguration.getNumberOfBytesInDataFormat();
        // numberOfBytesInDataFormat for each ads or accelerometer channel sample
        // + 1 byte marker + 2 bytes device specific information
        inputFrameSize = totalNumberOfDataSamples * numberOfBytesInDataFormat + 1 + 2;

       // we add one virtual channel (that will occupy numberOfBytesInDataFormat)
       // to store 2 bytes of device specific information
        outputFrameSize = totalNumberOfDataSamples * numberOfBytesInDataFormat + numberOfBytesInDataFormat;

        inputFrame = new byte[inputFrameSize];
        log.info("Com port inputFrame size: " + inputFrameSize + " bytes");
        log.info("Decoded inputFrame size: " + outputFrameSize);
    }

    public void onByteReceived(byte inByte) {
        if (index == 0 && inByte == START_FRAME_MARKER) {
            inputFrame[index] = inByte;
            index++;
        } else if (index > 0 && index < (inputFrameSize - 1)) {
            inputFrame[index] = inByte;
            index++;
        } else if (index == (inputFrameSize - 1)) {
            inputFrame[index] = inByte;
            index = 0;
            byte[] outputFrame = new byte[outputFrameSize];
            // copy inputFrame to outputFrame skipping fist marker byte
            System.arraycopy(inputFrame, 1, outputFrame, 0, inputFrame.length - 1);
            notifyFrameListeners(outputFrame);
        } else {
            log.warn("Lost Frame. Frame index = " + index + " inByte = " + inByte);
            index = 0;
        }
    }
}
