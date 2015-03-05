package device;

import comport.ComPortListener;

/**
 * Created by mac on 05/03/15.
 */
public interface FrameDecoder extends ComPortListener {
    public void addFrameListener(FrameListener frameListener);
    public void removeFrameListener(FrameListener frameListener);
}
