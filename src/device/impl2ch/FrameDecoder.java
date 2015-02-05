package device.impl2ch;

/**
 * Created by IntelliJ IDEA.
 * User: SlowFlyer
 * Date: 05.02.15
 * Time: 19:45
 * To change this template use File | Settings | File Templates.
 */
public interface FrameDecoder {

    public void onByteReceived(byte inByte);
    public void notifyListeners(byte[] decodedFrame);
}
