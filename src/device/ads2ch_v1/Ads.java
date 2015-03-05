package device.ads2ch_v1;

import bdf.*;
import comport.ComPort;
import data.DataDimension;
import dreamrec.ApplicationException;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class Ads implements BdfProvider, FrameListener {

    private List<BdfListener> bdfListeners = new ArrayList<BdfListener>();

    private static final Log log = LogFactory.getLog(device.ads2ch_v0.Ads.class);
    private final int NUMBER_OF_BYTES_IN_DATA_FORMAT = 3;
    private static final int COM_PORT_SPEED = 460800;
    private ComPort comPort;
    private boolean isRecording;
    private AdsConfiguration adsConfiguration;


    public Ads() {
        adsConfiguration = new AdsConfiguration();
    }

    @Override
    public void startReading() throws ApplicationException {
        String failConnectMessage = "Connection failed. Check com port settings.\nReset power on the target amplifier. Restart the application.";
        try {
            FrameDecoder comPortListener = new FrameDecoder(adsConfiguration);
            comPortListener.addFrameListener(this);
            AdsConfigurator adsConfigurator = new AdsConfigurator();
            comPort = new ComPort(adsConfiguration.getComPortName(), COM_PORT_SPEED);
            comPort.setComPortListener(comPortListener);
            comPort.writeToPort(adsConfigurator.writeAdsConfiguration(adsConfiguration));
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
    public void onFrameReceived(byte[] frame) {
        notifyAdsDataListeners(frame);
    }

    @Override
    public void addBdfDataListener(BdfListener bdfBdfListener) {
        bdfListeners.add(bdfBdfListener);
    }

    @Override
    public void removeBdfDataListener(BdfListener bdfListener) {
        bdfListeners.remove(bdfListener);
    }

    private void notifyAdsDataListeners(byte[] bdfDataRecord) {
        for (BdfListener bdfListener : bdfListeners) {
            bdfListener.onDataRecordReceived(bdfDataRecord);
        }
    }

    @Override
    public DeviceBdfConfig getBdfConfig() {
        return createBdfConfig();
    }


    private DeviceBdfConfig createBdfConfig() {
        List<SignalConfig> signalConfigList = new ArrayList<SignalConfig>();
        int n = 0;
        for (int i = 0; i < adsConfiguration.NUMBER_OF_CHANNELS; i++) {
            if (adsConfiguration.isChannelEnabled(i)) {
                int physicalMax = 2400000 / adsConfiguration.getChannelGain(i).getValue();
                int numberOfSamplesInEachDataRecord = AdsConfiguration.MAX_DIVIDER.getValue() / adsConfiguration.getChannelDivider(i).getValue();
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
            int numberOfSamplesInEachDataRecord = AdsConfiguration.MAX_DIVIDER.getValue()  / adsConfiguration.getAccelerometerDivider().getValue();
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

        double DurationOfDataRecord = (double) (AdsConfiguration.MAX_DIVIDER.getValue() ) / adsConfiguration.getSps().getValue();
        SignalConfig[] signalConfigArray = signalConfigList.toArray(new SignalConfig[signalConfigList.size()]);
        DeviceBdfConfig bdfConfig = new DeviceBdfConfig(DurationOfDataRecord, NUMBER_OF_BYTES_IN_DATA_FORMAT, signalConfigArray);
        return bdfConfig;
    }
}
