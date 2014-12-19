package device.impl2ch;

import bdf.*;
import dreamrec.ApplicationException;
import dreamrec.BdfDevice;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Ads implements BdfDevice {

    private static final Log log = LogFactory.getLog(Ads.class);

    private List<BdfListener> bdfListeners = new ArrayList<BdfListener>();
    private ComPort comPort;
    private boolean isRecording;
    AdsConfiguration adsConfiguration;
    private DeviceBdfConfig deviceBdfConfig;

    public Ads() {
        adsConfiguration = new AdsConfigUtil().readConfiguration();
    }

    @Override
    public void startReading() throws ApplicationException{
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
    public void removeBdfDataListener(BdfListener bdfListener) {
        bdfListeners.remove(bdfListener);
    }

    @Override
    public DeviceBdfConfig getBdfConfig() {
        if(deviceBdfConfig == null) {
            deviceBdfConfig = createBdfConfig();
        }
        return deviceBdfConfig;
    }


    private RecordingBdfConfig createBdfConfig() {
        List<SignalConfig> signalConfigList = new ArrayList<SignalConfig>();
        int n = 0;
        for (AdsChannelConfiguration channelConfiguration : adsConfiguration.getAdsChannels()) {
            if (channelConfiguration.isEnabled()) {
                int physicalMax = 2400000 / channelConfiguration.getGain().getValue();
                int numberOfSamplesInEachDataRecord = adsConfiguration.getDeviceType().getMaxDiv().getValue() / channelConfiguration.getDivider().getValue();
                SignalConfig signalConfig = new SignalConfig.Builder()
                        .setLabel("Channel " + n++)
                        .setTransducerType("Unknown")
                        .setDigitalMax(8388607)
                        .setDigitalMin(-8388608)
                        .setPhysicalMax(physicalMax)
                        .setPhysicalMin(-physicalMax)
                        .setPrefiltering("None")
                        .setNumberOfSamplesInEachDataRecord(numberOfSamplesInEachDataRecord)
                        .setPhysicalDimension("uV").build();

                signalConfigList.add(signalConfig);
            }
        }
        for (int i = 0; i < 3; i++) {
            int numberOfSamplesInEachDataRecord = adsConfiguration.getDeviceType().getMaxDiv().getValue() / adsConfiguration.getAccelerometerDivider().getValue();
            if (adsConfiguration.isAccelerometerEnabled()) {
                SignalConfig signalConfig = new SignalConfig.Builder()
                        .setLabel("Accelerometer " + i + 1)
                        .setTransducerType("Unknown")
                        .setDigitalMax(30800)
                        .setDigitalMin(-30800)
                        .setPhysicalMax(2)
                        .setPhysicalMin(-2)
                        .setPrefiltering("None")
                        .setNumberOfSamplesInEachDataRecord(numberOfSamplesInEachDataRecord)
                        .setPhysicalDimension("g")
                        .build();

                signalConfigList.add(signalConfig);
            }
        }

            SignalConfig signalConfig = new SignalConfig.Builder()
                    .setLabel("System events ")
                    .setTransducerType("Unknown")
                    .setDigitalMax(8388607)
                    .setDigitalMin(-8388608)
                    .setPhysicalMax(8388607)
                    .setPhysicalMin(-8388608)
                    .setPrefiltering("None")
                    .setNumberOfSamplesInEachDataRecord(1)
                    .setPhysicalDimension("n/a")
                    .build();
            signalConfigList.add(signalConfig);


        double DurationOfDataRecord = (double)(adsConfiguration.getDeviceType().getMaxDiv().getValue()) / adsConfiguration.getSps().getValue();
        int numberOfBytesInDataFormat = 3;
        SignalConfig[] signalConfigArray = signalConfigList.toArray(new SignalConfig[signalConfigList.size()]);
        RecordingBdfConfig recordingConfig = new RecordingBdfConfig(DurationOfDataRecord, numberOfBytesInDataFormat, signalConfigArray);
        return recordingConfig;
    }
}
