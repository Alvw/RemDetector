package device.impl2ch;

import bdf.BdfConfig;
import bdf.BdfListener;
import bdf.BdfProvider;
import bdf.BdfSignalConfig;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Ads implements BdfProvider {

    private static final Log log = LogFactory.getLog(Ads.class);

    private List<BdfListener> bdfListeners = new ArrayList<BdfListener>();
    private ComPort comPort;
    private boolean isRecording;
    AdsConfiguration adsConfiguration;
    private BdfConfig bdfConfig;

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

    private void notifyAdsDataListeners(byte[] bdfDataRecord) {
        for (BdfListener bdfListener : bdfListeners) {
            bdfListener.onDataRecordReceived(bdfDataRecord);
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
        if(bdfConfig == null) {
            bdfConfig = createBdfConfig();
        }
        return bdfConfig;
    }


    private BdfConfig createBdfConfig() {
        List<BdfSignalConfig> signalConfigList = new ArrayList<BdfSignalConfig>();
        int n = 0;
        for (AdsChannelConfiguration channelConfiguration : adsConfiguration.getAdsChannels()) {
            if (channelConfiguration.isEnabled()) {
                int physicalMax = 2400000 / channelConfiguration.getGain().getValue();
                int numberOfSamplesInEachDataRecord = adsConfiguration.getDeviceType().getMaxDiv().getValue() / channelConfiguration.getDivider().getValue();
                BdfSignalConfig bdfSignalConfig = new BdfSignalConfig.Builder()
                        .setLabel("Channel " + n++)
                        .setDigitalMax(8388607)
                        .setDigitalMin(-8388608)
                        .setPhysicalMax(physicalMax)
                        .setPhysicalMin(-physicalMax)
                        .setNumberOfSamplesInEachDataRecord(numberOfSamplesInEachDataRecord)
                        .setPhysicalDimension("uV").build();

                signalConfigList.add(bdfSignalConfig);
            }
        }
        for (int i = 0; i < 3; i++) {
            int numberOfSamplesInEachDataRecord = adsConfiguration.getDeviceType().getMaxDiv().getValue() / adsConfiguration.getAccelerometerDivider().getValue();
            if (adsConfiguration.isAccelerometerEnabled()) {
                BdfSignalConfig bdfSignalConfig = new BdfSignalConfig.Builder()
                        .setLabel("Accelerometer " + i + 1)
                        .setDigitalMax(30800)
                        .setDigitalMin(-30800)
                        .setPhysicalMax(2)
                        .setPhysicalMin(-2)
                        .setNumberOfSamplesInEachDataRecord(numberOfSamplesInEachDataRecord)
                        .setPhysicalDimension("g")
                        .build();

                signalConfigList.add(bdfSignalConfig);
            }
        }
        for (int i = 0; i < 2; i++) {
            BdfSignalConfig bdfSignalConfig = new BdfSignalConfig.Builder()
                    .setLabel("Loff stat " + i + 1)
                    .setDigitalMax(8388607)
                    .setDigitalMin(-8388608)
                    .setPhysicalMax(8388607)
                    .setPhysicalMin(-8388608)
                    .setNumberOfSamplesInEachDataRecord(1)
                    .setPhysicalDimension("n/a")
                    .build();

            signalConfigList.add(bdfSignalConfig);
        }

        int DurationOfDataRecord = adsConfiguration.getDeviceType().getMaxDiv().getValue() / adsConfiguration.getSps().getValue();
        int numberOfBytesInDataFormat = 3;
        BdfSignalConfig[] signalConfigArray = signalConfigList.toArray(new BdfSignalConfig[signalConfigList.size()]);
        BdfConfig bdfConfig = new BdfConfig(DurationOfDataRecord, numberOfBytesInDataFormat, signalConfigArray);
        return bdfConfig;
    }
}
