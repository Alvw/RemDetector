package dreamrec;

import device.BdfDataListener;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 */
public class IncomingDataBuffer implements BdfDataListener {

    private ConcurrentLinkedQueue<int[]> dataframesBuffer = new ConcurrentLinkedQueue<int[]>();

    public void onAdsDataReceived(int[] dataFrame) {
        dataframesBuffer.offer(dataFrame);
    }

    @Override
    public void onStopRecording() {

    }

    public boolean available() {
        return dataframesBuffer.size()>0;
    }

    public int[] poll() {
        return dataframesBuffer.poll();
    }
}
