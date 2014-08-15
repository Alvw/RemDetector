package com.crostec.bdfrecorder;

import com.crostec.ads.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import javax.swing.*;

public class Controller {

    private boolean isRecording;
    private SettingsWindow settingsWindow;
    private Ads ads;
    private BdfWriter bdfWriter;
    MathlabDataListener mathlabDataListener;

    private static  final Log log = LogFactory.getLog(Controller.class);

    public Controller(Ads ads) {
        this.ads = ads;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void startRecording(BdfHeaderData bdfHeaderData) {
        new AdsConfigUtil().saveAdsConfiguration(bdfHeaderData.getAdsConfiguration());
        log.info(bdfHeaderData.getAdsConfiguration().toString());
        isRecording = true;
        if (bdfWriter != null) {
            ads.removeAdsDataListener(bdfWriter);
        }
        if (mathlabDataListener != null) {
            ads.removeAdsDataListener(mathlabDataListener);
        }
        bdfWriter = new BdfWriter(bdfHeaderData);
        mathlabDataListener = new MathlabDataListener(bdfHeaderData.getAdsConfiguration());
        if(mathlabDataListener.isFrequencyTheSame()){
           ads.addAdsDataListener(mathlabDataListener);
           log.info("Mathlab interface started successfully. ");
        } else {
            log.warn("Mathlab interface disabled. Frequencies for all channels should be the same");
        }
        ads.addAdsDataListener(bdfWriter);
        try {
            ads.startRecording(bdfHeaderData.getAdsConfiguration());
        } catch (AdsException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            System.exit(0);
        }

    }

    public void stopRecording() {
        if (!isRecording) return;
        ads.stopRecording();
        isRecording = false;
    }

    public void closeApplication(BdfHeaderData bdfHeaderData) {
        new AdsConfigUtil().saveAdsConfiguration(bdfHeaderData.getAdsConfiguration());
        stopRecording();
        System.exit(0);
    }

    public void setSettingsWindow(SettingsWindow settingsWindow) {
        this.settingsWindow = settingsWindow;
    }
}
