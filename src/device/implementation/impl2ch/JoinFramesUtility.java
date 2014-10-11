package device.implementation.impl2ch;


import device.BdfDataListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

abstract class JoinFramesUtility implements BdfDataListener {

    private AdsConfiguration adsConfiguration;
    private int numberOfFramesToJoin;
    private int[] joinedFrame;
    private int inputFramesCounter;
    private static final Log log = LogFactory.getLog(JoinFramesUtility.class);
    private long lastDataRecordTime = System.currentTimeMillis();

    protected JoinFramesUtility(AdsConfiguration adsConfiguration) {
        this.numberOfFramesToJoin = adsConfiguration.getSps().getValue() / adsConfiguration.getDeviceType().getMaxDiv().getValue(); // 1 second duration of a data record in bdf file
        this.adsConfiguration = adsConfiguration;
        joinedFrame = new int[getJoinedFrameSize(numberOfFramesToJoin, adsConfiguration)];
    }

    @Override
    public void onAdsDataReceived(int[] dataFrame) {
        int channelPosition = 0;
        for (int divider : AdsUtils.getDividersForActiveChannels(adsConfiguration)) {
            int channelSampleNumber = adsConfiguration.getDeviceType().getMaxDiv().getValue() / divider;
            for (int j = 0; j < channelSampleNumber; j++) {
                joinedFrame[channelPosition * numberOfFramesToJoin + inputFramesCounter * channelSampleNumber + j] = dataFrame[channelPosition + j];
            }
            channelPosition += channelSampleNumber;
        }
        inputFramesCounter++;
        if (inputFramesCounter == numberOfFramesToJoin) {  // when edfFrame is ready
            inputFramesCounter = 0;
            notifyListeners(joinedFrame);
            long dataRecordTime = System.currentTimeMillis();
            long delay = lastDataRecordTime - dataRecordTime;
            log.debug("frame delay = " + delay);
            if(delay > 1500){
                log.warn("Frame delay = " + delay + "msec. Should be 1000 msec");
            }
            lastDataRecordTime = dataRecordTime;
        }
    }

    private int getJoinedFrameSize(int numberOfFramesToJoin, AdsConfiguration adsConfiguration) {
        int result = 0;
        for (int divider : AdsUtils.getDividersForActiveChannels(adsConfiguration)) {
            result += adsConfiguration.getDeviceType().getMaxDiv().getValue() / divider;
        }
        return result * numberOfFramesToJoin;
    }

    public abstract void notifyListeners(int[] joinedFrame);
}
