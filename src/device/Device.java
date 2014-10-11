package device;

import bdf.BdfWriter;

public interface Device {
    public void startRecord();
    public void stopRecording();
    public void addBdfDataListener(BdfDataListener bdfDataListener);
    public BdfConfig getBdfConfig();
    void removeBdfDataListener(BdfWriter bdfWriter);
}
