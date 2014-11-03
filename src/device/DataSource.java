package device;


import dreamrec.ApplicationException;

public interface     DataSource {
    public void startReading() throws ApplicationException;
    public void stopReading() throws ApplicationException;
    public void addDataListener(DataListener dataListener);
    public BdfConfig getBdfConfig();
}
