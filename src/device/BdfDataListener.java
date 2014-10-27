package device;

/**
 *
 */
public interface BdfDataListener {
    public void onDataRecordReceived(int[][] bdfDataRecord);
    public void onStopReading();
}
