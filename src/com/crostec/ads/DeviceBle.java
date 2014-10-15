package com.crostec.ads;

import device.BdfDataListener;
import device.implementation.ComPort;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class DeviceBle {

    private static final Log log = LogFactory.getLog(DeviceBle.class);

    private List<BdfDataListener> bdfDataListeners = new ArrayList<BdfDataListener>();
    private ComPort comPort;
    private boolean isRecording;

    public void writeToPort(List<Byte> bytes){
        comPort.writeToPort(bytes);
    }

    public void startRecording(DeviceConfig deviceConfig) {
        String failConnectMessage = "Connection failed. Check com port settings.\nReset power on the target amplifier. Restart the application.";
        try {
            FrameDecoder frameDecoder = new FrameDecoder(this) {
                @Override
                public void notifyListeners(int[] decodedFrame) {
                    notifyAdsDataListeners(decodedFrame);
                }
            };
            comPort = new ComPort();
            comPort.connect(deviceConfig);
            comPort.setFrameDecoder(frameDecoder);
            //comPort.writeToPort(adsConfiguration.getDeviceType().getAdsConfigurator().writeAdsConfiguration(adsConfiguration));
            isRecording = true;
        } catch (NoSuchPortException e) {
            String msg = "No port with the name " + deviceConfig.getComPortName() + "\n" + failConnectMessage;
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

    public void stopRecording() {
        for (BdfDataListener bdfDataListener : bdfDataListeners) {
            bdfDataListener.onStopReading();
        }
        if (!isRecording) return;
        //comPort.writeToPort(new AdsConfigurator().startPinLo());
       try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.warn(e);
        }
        comPort.disconnect();
    }

    public void addAdsDataListener(BdfDataListener bdfDataListener) {
        bdfDataListeners.add(bdfDataListener);
    }

    private void notifyAdsDataListeners(int[] dataRecord) {
        for (BdfDataListener bdfDataListener : bdfDataListeners) {
            bdfDataListener.onDataRecordReceived(dataRecord);
        }
    }

    public void removeAdsDataListener(BdfDataListener bdfDataListener) {
        bdfDataListeners.remove(bdfDataListener);
    }
}
