package device.general;

import comport.ComPortListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 05/03/15.
 */
public abstract class FrameDecoder implements ComPortListener {
    private List<FrameListener> listeners = new ArrayList<FrameListener>();

    public void addFrameListener(FrameListener frameListener) {
        listeners.add(frameListener);
    }
    public void removeFrameListener(FrameListener frameListener) {
        listeners.remove(frameListener);
    }
    protected void notifyFrameListeners(byte[] frame) {
        for (FrameListener listener : listeners) {
            listener.onFrameReceived(frame);
        }
    }
}
