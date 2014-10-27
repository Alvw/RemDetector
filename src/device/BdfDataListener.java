package device;

/**
 *
 */
public interface BdfDataListener {
    public void onDataRecordReceived(int[][] dataRecord);
    public void onStopReading();
}
