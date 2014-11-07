package device.impl2ch;

import bdf.BdfConfig;
import bdf.BdfListener;
import bdf.BdfSignalConfig;
import bdf.BdfSource;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Ads implements BdfSource {

    private static final Log log = LogFactory.getLog(Ads.class);

    private List<BdfListener> bdfListeners = new ArrayList<BdfListener>();
    private ComPort comPort;
    private boolean isRecording;
    AdsConfiguration adsConfiguration;
    public Ads() {
        adsConfiguration = new AdsConfigUtil().readConfiguration();
    }

    @Override
    public void startReading() {
        String failConnectMessage = "Connection failed. Check com port settings.\nReset power on the target amplifier. Restart the application.";
        try {
            FrameDecoder frameDecoder = new FrameDecoder(adsConfiguration) {
                @Override
                public void notifyListeners(int[] decodedFrame) {
                   // notifyAdsDataListeners(decodedFrame);
                }
            };
            comPort = new ComPort();
            comPort.connect(adsConfiguration);
            comPort.setFrameDecoder(frameDecoder);
            comPort.writeToPort(adsConfiguration.getDeviceType().getAdsConfigurator().writeAdsConfiguration(adsConfiguration));
            isRecording = true;
        } catch (NoSuchPortException e) {
            String msg = "No port with the name " + adsConfiguration.getComPortName() + "\n" + failConnectMessage;
            log.error(msg, e);
            throw new AdsException(msg, e);
        } catch (PortInUseException e) {
            log.error(failConnectMessage, e);
            throw new AdsException(failConnectMessage, e);
        } catch (Throwable e) {
            log.error(failConnectMessage, e);
            throw new AdsException(failConnectMessage, e);
        }
    }

    public void stopReading() {
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
    }

    @Override
    public void addBdfDataListener(BdfListener bdfBdfListener) {
        bdfListeners.add(bdfBdfListener);
    }

    @Override
    public BdfConfig getBdfConfig() {
        BdfConfig bdfConfig = new BdfConfig();
        bdfConfig.setDurationOfDataRecord(adsConfiguration.getDeviceType().getMaxDiv().getValue() / adsConfiguration.getSps().getValue());
       // bdfConfig.setNumberOfSignals(AdsUtils.getDividersForActiveChannels(adsConfiguration).size()+2);
        List<BdfSignalConfig> signalConfigList = new ArrayList<BdfSignalConfig>();
        int n = 0;
        for (AdsChannelConfiguration channelConfiguration : adsConfiguration.getAdsChannels()) {
            if (channelConfiguration.isEnabled()) {
                BdfSignalConfig bdfSignalConfig = new BdfSignalConfig();
                bdfSignalConfig.setLabel("Channel " + n++);
                bdfSignalConfig.setDigitalMax(8388607);
                bdfSignalConfig.setDigitalMin(-8388608);
                int physicalMax = 2400000/channelConfiguration.getGain().getValue();
                bdfSignalConfig.setPhysicalMax(physicalMax);
                bdfSignalConfig.setPhysicalMin(-physicalMax);
                bdfSignalConfig.setNumberOfSamplesInEachDataRecord(adsConfiguration.getDeviceType().getMaxDiv().getValue() / channelConfiguration.getDivider().getValue());
                bdfSignalConfig.setPhysicalDimension("uV");
                signalConfigList.add(bdfSignalConfig);
            }
        }
        for (int i = 0; i < 3; i++) {
            if (adsConfiguration.isAccelerometerEnabled()) {
                BdfSignalConfig bdfSignalConfig = new BdfSignalConfig();
                bdfSignalConfig.setLabel("Accelerometer " + i + 1);
                bdfSignalConfig.setDigitalMax(30800);
                bdfSignalConfig.setDigitalMin(-30800);
                bdfSignalConfig.setPhysicalMax(2);
                bdfSignalConfig.setPhysicalMin(-2);
                bdfSignalConfig.setNumberOfSamplesInEachDataRecord(adsConfiguration.getDeviceType().getMaxDiv().getValue() / adsConfiguration.getAccelerometerDivider().getValue());
                bdfSignalConfig.setPhysicalDimension("g");
                signalConfigList.add(bdfSignalConfig);
            }
        }
        for (int i = 0; i < 2; i++) {
                BdfSignalConfig bdfSignalConfig = new BdfSignalConfig();
                bdfSignalConfig.setLabel("Loff stat " + i + 1);
                bdfSignalConfig.setDigitalMax(8388607);
                bdfSignalConfig.setDigitalMin(-8388608);
                bdfSignalConfig.setPhysicalMax(8388607);
                bdfSignalConfig.setPhysicalMin(-8388608);
                bdfSignalConfig.setNumberOfSamplesInEachDataRecord(1);
                bdfSignalConfig.setPhysicalDimension("n/a");
                signalConfigList.add(bdfSignalConfig);
        }

        bdfConfig.setSignalsConfigList(signalConfigList);
        return bdfConfig;
    }

    private void notifyAdsDataListeners(byte[] bdfDataRecord) {
        for (BdfListener bdfListener : bdfListeners) {
            bdfListener.onDataRecordReceived(bdfDataRecord);
        }
    }
}
