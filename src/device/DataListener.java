package device;

/**
 *
 */
public interface DataListener {
    public void onDataRecordReceived(int[][] dataRecord);
    public void onStopReading();
}
