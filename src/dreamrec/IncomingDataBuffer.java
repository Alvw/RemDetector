package dreamrec;

import device.BdfDataListener;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 */
public class IncomingDataBuffer implements BdfDataListener {

    private ConcurrentLinkedQueue<int[]> dataframesBuffer = new ConcurrentLinkedQueue<int[]>();

    public void onDataRecordReceived(int[] bdfDataRecord) {
        dataframesBuffer.offer(bdfDataRecord);
    }

    @Override
    public void onStopReading() {

    }

    public boolean available() {
        return dataframesBuffer.size()>0;
    }

    public int[] poll() {
        return dataframesBuffer.poll();
    }
}
