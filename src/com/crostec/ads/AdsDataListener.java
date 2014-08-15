package com.crostec.ads;

/**
 *
 */
public interface AdsDataListener {

    public void onAdsDataReceived(int[] dataFrame);
    public void onStopRecording();

}
