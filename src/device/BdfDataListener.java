package device;

/**
 *
 */
public interface BdfDataListener {

    public void onAdsDataReceived(int[] dataFrame);
    public void onStopRecording();

}
