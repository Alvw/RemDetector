package device;

import bdf.BdfWriter;

public interface Device {
    public void startRecord();
    public void stopRecord();
    public void addBdfDataListener(BdfDataListener bdfDataListener);
    public BdfConfig getBdfConfig();
    void removeBdfDataListener(BdfDataListener bdfDataListener);
}
