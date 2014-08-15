package com.crostec.bdfrecorder;


import com.crostec.ads.Ads;
import com.crostec.ads.AdsConfigUtil;
import com.crostec.ads.AdsConfiguration;
import com.crostec.ads.BdfHeaderData;
import com.crostec.bdfrecorder.Controller;
import com.crostec.bdfrecorder.SettingsWindow;

public class BdfRecorder {
    public static void main(String[] args) {
        Ads ads = new Ads();
        AdsConfigUtil adsConfigUtil = new AdsConfigUtil();
        AdsConfiguration adsConfiguration = adsConfigUtil.readConfiguration();
        BdfHeaderData bdfHeaderData = new BdfHeaderData(adsConfiguration);
        Controller controller = new Controller(ads);
        SettingsWindow settingsWindow = new SettingsWindow(controller, bdfHeaderData);
        controller.setSettingsWindow(settingsWindow);
        ads.addAdsDataListener(settingsWindow);
    }
}
