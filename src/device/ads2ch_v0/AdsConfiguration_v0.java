package device.ads2ch_v0;

import device.impl2ch.Divider;
import device.impl2ch.Sps;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AdsConfiguration_v0 {

    private Sps sps = Sps.S500;     // samples per second (sample rate)
 //   private ArrayList<AdsChannelConfiguration> adsChannels = new ArrayList<AdsChannelConfiguration>();
    private boolean isAccelerometerEnabled = true;
    private Divider accelerometerDivider = Divider.D10;
    private String comPortName = "COM1";
    private boolean isHighResolutionMode = true;
    final static Divider MAX_DIVIDER = Divider.D50;

    public boolean isHighResolutionMode() {
        return isHighResolutionMode;
    }
    public boolean isLoffEnabled() {
     /*   for (AdsChannelConfiguration adsChannel : adsChannels) {
           if(adsChannel.isLoffEnable()){
               return true;
           }
        }*/
        return false;
    }

    public String getComPortName() {
        return comPortName;
    }

    public void setComPortName(String comPortName) {
        this.comPortName = comPortName;
    }



    public void setAccelerometerEnabled(boolean accelerometerEnabled) {
        isAccelerometerEnabled = accelerometerEnabled;
    }

    public void setAccelerometerDivider(Divider accelerometerDivider) {
        this.accelerometerDivider = accelerometerDivider;
    }

    public Divider getAccelerometerDivider() {
        return accelerometerDivider;
    }

    public boolean isAccelerometerEnabled() {
        return isAccelerometerEnabled;
    }

    public Sps getSps() {
        return sps;
    }



    public AdsConfigurator getAdsConfigurator() {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }
}
