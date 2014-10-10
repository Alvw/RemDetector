package com.crostec.bdfrecorder;


import com.crostec.ads.Ads;
import com.crostec.ads.DeviceConfig;

public class BdfRecorder {
    public static void main(String[] args) {
        Ads ads = new Ads();
        Controller controller = new Controller(ads);
        DeviceConfig deviceConfig = new DeviceConfig();
        deviceConfig.setComPortName("COM14");
        deviceConfig.setComPortSpeed(256000);
        controller.startRecording(deviceConfig);
    }
}
