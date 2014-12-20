package device.impl2ch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

abstract class FrameDecoder {

    public static final int START_FRAME_MARKER = 254;
    private int index;
    private int inputFrameSize;
    int outputFrameSize = 0;
    private byte[] inputFrame;
    private static final Log log = LogFactory.getLog(FrameDecoder.class);

    public FrameDecoder(int numberOfDataSamples, int numberOfBytesInDataFormat) {
        // numberOfBytesInDataFormat for each ads or accelerometer channel sample
        // + 1 byte marker + 2 bytes device specific information
        inputFrameSize = numberOfDataSamples * numberOfBytesInDataFormat + 1 + 2;

       // we add one virtual channel (that will occupy numberOfBytesInDataFormat)
       // to store 2 bytes of device specific information
        outputFrameSize = numberOfDataSamples * numberOfBytesInDataFormat + numberOfBytesInDataFormat;

        inputFrame = new byte[inputFrameSize];
        log.info("Com port inputFrame size: " + inputFrameSize + " bytes");
        log.info("Decoded inputFrame size: " + outputFrameSize);
        System.out.println("Com port inputFrame size: " + inputFrameSize + " bytes");
        System.out.println("Decoded inputFrame size: " + outputFrameSize);
    }

    public void onByteReceived(byte inByte) {
        int inByteInt = inByte & 0xFF;
        if (index == 0 && inByteInt == START_FRAME_MARKER) {
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
            notifyListeners(outputFrame);
        } else {
            log.warn("Lost Frame. Frame index = " + index + " inByte = " + inByte);
            index = 0;
        }
    }

    public abstract void notifyListeners(byte[] decodedFrame);
}
