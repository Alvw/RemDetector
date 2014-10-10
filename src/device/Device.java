package device;

public interface Device {
    public void startRecord();
    public void stopRecording();
    public void addBdfDataListener(BdfDataListener bdfDataListener);
    public BdfConfig getBdfConfig();
}
