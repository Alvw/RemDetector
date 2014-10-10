package com.crostec.bdfrecorder;

import bdf.BdfWriter;
import com.crostec.ads.*;
import device.BdfConfig;
import device.BdfSignalConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    private boolean isRecording;
    private Ads ads;
    private BdfWriter bdfWriter;

    private static final Log log = LogFactory.getLog(Controller.class);

    public Controller(Ads ads) {
        this.ads = ads;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void startRecording(DeviceConfig deviceConfig) {

        isRecording = true;
        if (bdfWriter != null) {
            ads.removeAdsDataListener(bdfWriter);
        }
        bdfWriter = new BdfWriter(getBdfConfig());
        ads.addAdsDataListener(bdfWriter);
        try {
            ads.startRecording(deviceConfig);
            Thread.sleep(1000);
            ads.writeToPort(BlueGigaManager.setScanParameters());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            System.exit(0);
        }

    }

    public void stopRecording() {
        if (!isRecording) return;
        ads.stopRecording();
        isRecording = false;
    }

    BdfConfig getBdfConfig() {
        BdfConfig bdfConfig = new BdfConfig();
        bdfConfig.setDurationOfADataRecord(0.1);
        bdfConfig.setFileNameToSave("tralivali1.bdf");
        bdfConfig.setLocalPatientIdentification("xxx");
        bdfConfig.setLocalRecordingIdentification("yyy");
        bdfConfig.setNumberOfSignals(6);
        List<BdfSignalConfig> bdfSignalConfigList = new ArrayList<BdfSignalConfig>();

        BdfSignalConfig ch1Config = new BdfSignalConfig();
        ch1Config.setDigitalMax(8388607);
        ch1Config.setDigitalMin(-8388608);
        int gain = 6;
        ch1Config.setPhysicalMin(-(2400000 / gain));
        ch1Config.setPhysicalMax(2400000 / gain);
        ch1Config.setLabel("EOG");
        ch1Config.setNrOfSamplesInEachDataRecord(5);
        ch1Config.setPhysicalDimension("uV");
        bdfSignalConfigList.add(ch1Config);

        for (int i = 0; i < 3; i++) {
            BdfSignalConfig adc10config = new BdfSignalConfig();
            adc10config.setDigitalMax(4095);
            adc10config.setDigitalMin(-4096);
            adc10config.setPhysicalMin(-1000);
            adc10config.setPhysicalMax(1000);
            adc10config.setLabel("Accelerometer" + i);
            adc10config.setNrOfSamplesInEachDataRecord(1);
            adc10config.setPhysicalDimension("mV");
            bdfSignalConfigList.add(adc10config);
        }
         BdfSignalConfig batteryConfig = new BdfSignalConfig();
            batteryConfig.setDigitalMax(4095);
            batteryConfig.setDigitalMin(-4096);
            batteryConfig.setPhysicalMin(-1000);
            batteryConfig.setPhysicalMax(1000);
            batteryConfig.setLabel("Battery");
            batteryConfig.setNrOfSamplesInEachDataRecord(1);
            batteryConfig.setPhysicalDimension("mV");
            bdfSignalConfigList.add(batteryConfig);
        //-----------------
         BdfSignalConfig eventConfig = new BdfSignalConfig();
            eventConfig.setDigitalMax(100);
            eventConfig.setDigitalMin(-100);
            eventConfig.setPhysicalMin(-100);
            eventConfig.setPhysicalMax(100);
            eventConfig.setLabel("Event");
            eventConfig.setNrOfSamplesInEachDataRecord(1);
            eventConfig.setPhysicalDimension("N");
            bdfSignalConfigList.add(eventConfig);

        bdfConfig.setSignalConfigList(bdfSignalConfigList);
        return bdfConfig;
    }
}
