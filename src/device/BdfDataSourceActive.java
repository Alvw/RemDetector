package device;

public interface BdfDataSourceActive {
    public void startReading();
    public void stopReading();
    public void addBdfDataListener(BdfDataListener bdfDataListener);
    public BdfConfig getBdfConfig();
    void removeBdfDataListener(BdfDataListener bdfDataListener);
}
