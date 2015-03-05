package device.ads2ch_v0;

import bdf.*;
import comport.ComPort;
import data.DataDimension;
import device.ads2ch_v1.AdsConfiguration;
import device.impl2ch.*;
import dreamrec.ApplicationException;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Ads implements BdfProvider{

    private static final Log log = LogFactory.getLog(Ads.class);
    private final int NUMBER_OF_BYTES_IN_DATA_FORMAT = 3;
    private List<BdfListener> bdfListeners = new ArrayList<BdfListener>();
    private ComPort comPort;
    private boolean isRecording;
    private AdsConfiguration adsConfiguration;


    public Ads() {
        throw new UnsupportedOperationException();
//        adsConfiguration = new AdsConfigUtil().readConfiguration();
    }

    @Override
    public void startReading() throws ApplicationException {
        String failConnectMessage = "Connection failed. Check com port settings.\nReset power on the target amplifier. Restart the application.";
        try {
            FrameDecoder2ch frameDecoder2ch = new FrameDecoder2ch(getNumberOfDataSamples(), NUMBER_OF_BYTES_IN_DATA_FORMAT) {
                @Override
                public void notifyListeners(byte[] decodedFrame) {
                    notifyAdsDataListeners(decodedFrame);
                }
            };
            comPort = new ComPort(null, 0);
          //  comPort.connect(adsConfiguration);
            comPort.setComPortListener(frameDecoder2ch);
          //  comPort.writeToPort(adsConfiguration.getAdsConfigurator().writeAdsConfiguration(adsConfiguration));
            isRecording = true;
        } catch (NoSuchPortException e) {
            String msg = "No port with the name " + adsConfiguration.getComPortName() + "\n" + failConnectMessage;
            log.error(msg, e);
            throw new ApplicationException(msg, e);
        } catch (PortInUseException e) {
            log.error(failConnectMessage, e);
            throw new ApplicationException(failConnectMessage, e);
        } catch (Throwable e) {
            log.error(failConnectMessage, e);
            throw new ApplicationException(failConnectMessage, e);
        }
    }

    private void notifyAdsDataListeners(byte[] bdfDataRecord) {
        for (BdfListener bdfListener : bdfListeners) {
            bdfListener.onDataRecordReceived(bdfDataRecord);
        }
    }

    public void stopReading() {
        log.debug("Stop Reading begins");
        for (BdfListener adsBdfListener : bdfListeners) {
            adsBdfListener.onStopReading();
        }
        if (!isRecording) return;
        comPort.writeToPort(new AdsConfigurator().startPinLo());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.warn(e);
        }
        comPort.disconnect();
        log.debug("Stop Reading finished");
    }

    @Override
    public void addBdfDataListener(BdfListener bdfBdfListener) {
        bdfListeners.add(bdfBdfListener);
    }

    @Override
    public void removeBdfDataListener(BdfListener bdfListener) {
        bdfListeners.remove(bdfListener);
    }

    @Override
    public DeviceBdfConfig getBdfConfig() {
        return null; //createBdfConfig();
    }


 /*   private DeviceBdfConfig createBdfConfig() {
        List<SignalConfig> signalConfigList = new ArrayList<SignalConfig>();
        int n = 0;
        for (AdsChannelConfiguration channelConfiguration : adsConfiguration.getAdsChannels()) {
            if (channelConfiguration.isEnabled()) {
                int physicalMax = 2400000 / channelConfiguration.getGain().getValue();
                int numberOfSamplesInEachDataRecord = AdsConfiguration_v0.MAX_DIVIDER.getValue() / channelConfiguration.getDivider().getValue();
                DataDimension dataDimension = new DataDimension();
                dataDimension.setDigitalMax(8388607);
                dataDimension.setDigitalMin(-8388608);
                dataDimension.setPhysicalMax(physicalMax);
                dataDimension.setPhysicalMin(-physicalMax);
                dataDimension.setPhysicalDimension("uV");
                SignalConfig signalConfig = new SignalConfig(numberOfSamplesInEachDataRecord, dataDimension);
                signalConfig.setLabel("Channel " + n++);
                signalConfig.setTransducerType("Unknown");
                signalConfig.setPrefiltering("None");
                signalConfigList.add(signalConfig);
            }
        }
        for (int i = 0; i < 3; i++) {
            int numberOfSamplesInEachDataRecord = AdsConfiguration_v0.MAX_DIVIDER.getValue() / adsConfiguration.getAccelerometerDivider().getValue();
            if (adsConfiguration.isAccelerometerEnabled()) {
                DataDimension dataDimension = new DataDimension();
                dataDimension.setDigitalMax(30800);
                dataDimension.setDigitalMin(-30800);
                dataDimension.setPhysicalMax(2);
                dataDimension.setPhysicalMin(-2);
                dataDimension.setPhysicalDimension("g");
                SignalConfig signalConfig = new SignalConfig(numberOfSamplesInEachDataRecord, dataDimension);
                signalConfig.setLabel("Accelerometer " + i + 1);
                signalConfig.setTransducerType("Unknown");
                signalConfig.setPrefiltering("None");
                signalConfigList.add(signalConfig);
            }
        }

        // channel for device specific information (loff status and so on);
        int numberOfSamplesInEachDataRecord = 1;
        DataDimension dataDimension = new DataDimension();
        dataDimension.setDigitalMax(8388607);
        dataDimension.setDigitalMin(-8388608);
        dataDimension.setPhysicalMax(8388607);
        dataDimension.setPhysicalMin(-8388607);
        dataDimension.setPhysicalDimension("");
        SignalConfig signalConfig = new SignalConfig(numberOfSamplesInEachDataRecord, dataDimension);
        signalConfig.setLabel("System events");
        signalConfig.setTransducerType("Unknown");
        signalConfig.setPrefiltering("None");
        signalConfigList.add(signalConfig);

        double DurationOfDataRecord = (double) (AdsConfiguration_v0.MAX_DIVIDER.getValue()) / adsConfiguration.getSps().getValue();
        SignalConfig[] signalConfigArray = signalConfigList.toArray(new SignalConfig[signalConfigList.size()]);
        DeviceBdfConfig bdfConfig = new DeviceBdfConfig(DurationOfDataRecord, NUMBER_OF_BYTES_IN_DATA_FORMAT, signalConfigArray);
        return bdfConfig;
    }*/

    private int getNumberOfDataSamples() {
        int numberOfDataSamples = 0;
        BdfConfig bdfConfig = getBdfConfig();
        // the last channel is virtual channel for device specific information (loff status and so on)
        // so we do not take it into consideration
        SignalConfig[] signalConfigs = bdfConfig.getSignalConfigs();
        int numberOfRealChannels = signalConfigs.length - 1;
        for (int i = 0; i < numberOfRealChannels; i++) {
            numberOfDataSamples += signalConfigs[i].getNumberOfSamplesInEachDataRecord();
        }
        return numberOfDataSamples;
    }
}
