package device.implementation.impl2ch;

import javax.naming.ConfigurationException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AdsConfiguration {
    

    private Sps sps = Sps.S500;     // samples per second (sample rate)
    private ArrayList<AdsChannelConfiguration> adsChannels = new ArrayList<AdsChannelConfiguration>();
    private boolean isAccelerometerEnabled = true;
    private Divider accelerometerDivider = Divider.D10;
    private String comPortName = "COM1";
    private boolean isHighResolutionMode = true;
    private DeviceType deviceType;

    public boolean isHighResolutionMode() {
        return isHighResolutionMode;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public String getComPortName() {
        return comPortName;
    }

    public void setComPortName(String comPortName) {
        this.comPortName = comPortName;
    }

    public List<AdsChannelConfiguration> getAdsChannels(){
        return adsChannels;
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

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (AdsChannelConfiguration adsChannel : adsChannels) {
            sb.append(adsChannel.toString());
            sb.append("\r");
        }
        return "AdsConfiguration{" +
                "sps=" + sps +
                ", isAccelerometerEnabled=" + isAccelerometerEnabled +
                ", accelerometerDivider=" + accelerometerDivider +
                ", comPortName='" + comPortName + '\'' +
                ", isHighResolutionMode=" + isHighResolutionMode +
                ", deviceType=" + deviceType +
                '}' + sb.toString();
    }

    public void setSps(Sps sps) {
        this.sps = sps;
    }

}
