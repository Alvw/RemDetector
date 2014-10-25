package device;

import dreamrec.ApplicationException;

public interface BdfDataSource {
    public void startReading() throws ApplicationException;
    public void stopReading() throws ApplicationException;
    public void addBdfDataListener(BdfDataListener bdfDataListener);
    public BdfConfig getBdfConfig();
    void removeBdfDataListener(BdfDataListener bdfDataListener);
}
