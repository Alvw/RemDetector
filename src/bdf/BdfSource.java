package bdf;


import dreamrec.ApplicationException;

public interface BdfSource {
    public void startReading() throws ApplicationException;
    public void stopReading() throws ApplicationException;
    public void addBdfDataListener(BdfListener bdfListener);
    public BdfConfig getBdfConfig();
}
