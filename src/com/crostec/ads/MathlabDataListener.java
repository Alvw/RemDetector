package com.crostec.ads;

import com.crostec.ads.*;
import edu.ucsd.sccn.LSL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MathlabDataListener implements AdsDataListener {

    private static final Log log = LogFactory.getLog(MathlabDataListener.class);
    private AdsConfiguration adsConfiguration;
    LSL.StreamInfo info;
    LSL.StreamOutlet outlet;
    int numberOfEnabledChannels;
    int nrOfSamplesInOneChannel;
     List<Integer> microvoltValueDividers;
    private boolean stopRecordingRequest;

    public MathlabDataListener(AdsConfiguration adsConfiguration) {
        this.adsConfiguration = adsConfiguration;
        List<Integer> dividers = AdsUtils.getDividersForActiveChannels(adsConfiguration);
        numberOfEnabledChannels = dividers.size();
        int divider = dividers.get(0);
        int maxDiv = adsConfiguration.getDeviceType().getMaxDiv().getValue();
        nrOfSamplesInOneChannel = maxDiv / divider;
        int frequency = adsConfiguration.getSps().getValue()/divider;
        info = new LSL.StreamInfo("BioSemi", "EEG", numberOfEnabledChannels, frequency, LSL.ChannelFormat.int32, "myuid324457");
        log.debug("MatlabDataListener initialization. Number of enabled channels = " + numberOfEnabledChannels +
        ". Frequency = " +  frequency + ". Number of samples in BDF data record = " +  nrOfSamplesInOneChannel);
        outlet = new LSL.StreamOutlet(info);
        microvoltValueDividers = getValueDividersForActiveChannels();
    }


    @Override
    public synchronized void onAdsDataReceived(int[] dataFrame) {
        if (stopRecordingRequest) {
            return;
        }
        for (int j = 0; j < nrOfSamplesInOneChannel; j++) {
            int [] mathlabDataFrame = new int[numberOfEnabledChannels];
            for (int i = 0; i < numberOfEnabledChannels; i++) {
                mathlabDataFrame[i] = dataFrame[i * nrOfSamplesInOneChannel + j] / microvoltValueDividers.get(i);
            }
            outlet.push_sample(mathlabDataFrame);
        }
    }

    @Override
    public synchronized void onStopRecording() {
        if(stopRecordingRequest) return;
        stopRecordingRequest = true;
        outlet.close();
        info.destroy();
    }

    /**
     * Checks if frequencies for all channels are the same
     *
     * @return
     */
    public boolean isFrequencyTheSame() {
        List<AdsChannelConfiguration> channelConfigurations = adsConfiguration.getAdsChannels();
        int divider = 0;
        for (AdsChannelConfiguration channelConfiguration : channelConfigurations) {
            if (!channelConfiguration.isEnabled()) {
                continue;
            }
            int nextDivider = channelConfiguration.getDivider().getValue();
            if (nextDivider == 0) {
                continue;
            } else if (divider == 0) {
                divider = nextDivider;
            } else if (divider != nextDivider) {
                return false;
            }
        }
        //check accelerometer frequency the same
        if(adsConfiguration.isAccelerometerEnabled() && adsConfiguration.getAccelerometerDivider().getValue() != divider) {
            return false;
        }
        return true;
    }

    public List<Integer> getValueDividersForActiveChannels(){
        List<Integer> gainsForActiveChannels = new ArrayList<Integer>();
        for (AdsChannelConfiguration channelConfiguration : adsConfiguration.getAdsChannels()) {
            if (channelConfiguration.isEnabled()) {
                gainsForActiveChannels.add(8388607 * channelConfiguration.getGain().getValue()/2400000);
            }
        }
        for (int i = 0; i < 3; i++) {
             gainsForActiveChannels.add(15);

        }
        return gainsForActiveChannels;
    }
}
