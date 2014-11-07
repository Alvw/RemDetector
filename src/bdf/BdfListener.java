package bdf;

/**
 *
 */
public interface BdfListener {
    public void onDataRecordReceived(byte[] bdfDataRecord);
    public void onStopReading();
}
