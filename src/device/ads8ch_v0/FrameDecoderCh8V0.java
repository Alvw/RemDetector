package device.ads8ch_v0;

import device.general.AdsConfiguration;
import device.general.FrameDecoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by mac on 22/03/15.
 */
public class FrameDecoderCh8V0 extends FrameDecoder {
    public static final int START_FRAME_MARKER = (byte)(254 & 0xFF);
    private int index;
    private int inputFrameSize;
    private int outputFrameSize;
    private byte[] inputFrame;

    private static final Log log = LogFactory.getLog(FrameDecoderCh8V0.class);

    public FrameDecoderCh8V0(AdsConfiguration adsConfiguration) {
        int totalNumberOfDataSamples = adsConfiguration.getTotalNumberOfDataSamplesInEachDataRecord();
        int numberOfBytesInDataFormat = adsConfiguration.getNumberOfBytesInDataFormat();
        // numberOfBytesInDataFormat for each ads or accelerometer channel sample
        // + 1 byte marker + 2 bytes device specific information
        inputFrameSize = totalNumberOfDataSamples * numberOfBytesInDataFormat + 1 + 2;

        // we add one virtual channel (that will occupy numberOfBytesInDataFormat)
        // to store 2 bytes of device specific information
        outputFrameSize = totalNumberOfDataSamples * numberOfBytesInDataFormat + numberOfBytesInDataFormat;
        inputFrame = new byte[inputFrameSize];

        log.info("Com port frame size: " + inputFrameSize + " bytes");
        log.info("Decoded frame size: " + outputFrameSize + " bytes");
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
