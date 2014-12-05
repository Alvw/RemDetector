package bdf;


import dreamrec.ApplicationException;

public interface BdfProvider {
    public void startReading() throws ApplicationException;
    public void stopReading() throws ApplicationException;
    public void addBdfDataListener(BdfListener bdfListener);
    public void removeBdfDataListener(BdfListener bdfListener);
    public BdfConfig getBdfConfig();
}
