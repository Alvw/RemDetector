package device.ads2ch_v1;

import device.impl2ch.*;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AdsConfiguration {

    private static final Log log = LogFactory.getLog(AdsConfiguration.class);

    private Sps sps = Sps.S500;     // samples per second (sample rate)

    private boolean isAccelerometerEnabled = true;
    private Divider accelerometerDivider = Divider.D10;
    private String comPortName = "COM1";
    private boolean isHighResolutionMode = true;
    final static Divider MAX_DIVIDER = Divider.D10;


    protected Divider adsChannelDivider = Divider.D1;
    protected boolean isEnabled = true;
    private Gain gain = Gain.G2;
    private CommutatorState commutatorState = CommutatorState.INPUT;
    protected boolean isLoffEnable = true;
    private boolean isRldSenseEnabled = false;

    private static final String PROPERTIES_FILE_NAME = "ads2ch_v1_config.properties";

    public static final String COM_PORT_NAME = "comPort";
    public static final String SPS = "sps";
    public static final String CHANNEL_DIVIDER = "dividerChannel";
    public static final String CHANNEL_GAIN = "gainChannel";
    public static final String CHANNEL_COMMUTATOR_STATE = "commutatorStateChannel";
    public static final String CHANNEL_IS_ENABLED = "isEnabledChannel";
    public static final String CHANNEL_LOFF_ENABLED = "loffEnabledChannel";
    public static final String CHANNEL_RLD_SENSE_ENABLED = "rldSenseEnabledChannel";
    public static final String ACCELEROMETER_DIVIDER = "dividerAccelerometer";
    public static final String ACCELEROMETER_IS_ENABLED = "isEnabledAccelerometer";

    public static final int NUMBER_OF_CHANNELS = 2;

    private PropertiesConfiguration config;

    public AdsConfiguration() {
        try {
            config = new PropertiesConfiguration(PROPERTIES_FILE_NAME);
        } catch (ConfigurationException e) {
            log.error(e);
        }
    }

    public String getComPortName() {
        return config.getString(COM_PORT_NAME);
    }

    public void setComPortName(String comPortName) {
        config.setProperty(COM_PORT_NAME, comPortName);
    }

    public Sps getSps() {
        try {
            return Sps.valueOf(config.getInt(SPS));
        } catch (IllegalArgumentException e) {
            String msg = "ads_config.properties file " + e.getMessage();
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    public void setSps(Sps sps) {
        config.setProperty(SPS, sps);
    }

    public Divider getAccelerometerDivider() {
        try {
            return Divider.D10;
        } catch (IllegalArgumentException e) {
            String msg = "ads_config.properties file: " + e.getMessage();
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    public boolean isChannelEnabled(int channelNumber) {
        return config.getBoolean(CHANNEL_IS_ENABLED + channelNumber);
    }

    public void setChannelEnabled(int channelNumber, boolean isEnabled) {
        config.setProperty(CHANNEL_IS_ENABLED + channelNumber, isEnabled);
    }

    public boolean isAccelerometerEnabled() {
        return config.getBoolean(ACCELEROMETER_IS_ENABLED);
    }

    public void setAccelerometerEnabled(boolean isEnabled) {
        config.setProperty(ACCELEROMETER_IS_ENABLED, isEnabled);
    }


    public Divider getChannelDivider(int channelNumber) {
        try {
            return Divider.valueOf(config.getInt(CHANNEL_DIVIDER + channelNumber));
        } catch (IllegalArgumentException e) {
            String msg = "ads_config.properties file: " + channelNumber + "channel " + e.getMessage();
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    public Gain getChannelGain(int channelNumber) {
        try {
            return Gain.valueOf(config.getInt(CHANNEL_GAIN + channelNumber));
        } catch (IllegalArgumentException e) {
            String msg = "ads_config.properties file: " + channelNumber + "channel " + e.getMessage();
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    public CommutatorState getChannelCommutatorState(int channelNumber) {
        return CommutatorState.valueOf(config.getString(CHANNEL_COMMUTATOR_STATE + channelNumber));
    }


    public void setChannelDivider(int channelNumber, Divider divider) {
        config.setProperty(CHANNEL_DIVIDER + channelNumber, divider);
    }

    public void setChannelGain(int channelNumber, Gain gain) {
        config.setProperty(CHANNEL_GAIN + channelNumber, gain);
    }

    public void setChannelCommutatorState(int channelNumber, CommutatorState commutatorState) {
        config.setProperty(CHANNEL_COMMUTATOR_STATE + channelNumber, commutatorState);
    }


    public boolean isChannelLoffEnable(int channelNumber) {
        return config.getBoolean(CHANNEL_LOFF_ENABLED + channelNumber);
    }

    public boolean isChannelRldSenseEnable(int channelNumber) {
        return config.getBoolean(CHANNEL_RLD_SENSE_ENABLED + channelNumber);
    }

    public void setChannelRldSenseEnabled(int channelNumber, boolean isRldEnabled) {
        config.setProperty(CHANNEL_RLD_SENSE_ENABLED + channelNumber, isRldEnabled);
    }

    public void setChannelLoffEnabled(int channelNumber, boolean isLoffEnabled) {
        config.setProperty(CHANNEL_LOFF_ENABLED + channelNumber, isLoffEnabled);
    }

    public void save() {
        try {
            config.save(PROPERTIES_FILE_NAME);
        } catch (ConfigurationException e) {
            log.error(e);
        }
    }


    public boolean isHighResolutionMode() {
        return isHighResolutionMode;
    }
    public boolean isLoffEnabled() {
        for (int i = 0; i < NUMBER_OF_CHANNELS; i++) {
           if(isChannelLoffEnable(i)){
               return true;
           }
        }
        return false;
    }



    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < NUMBER_OF_CHANNELS; i++) {
            sb.append( "AdsChannelConfiguration_"+i+
                    " {divider=" +getChannelDivider(i) +
                    ", isEnabled=" + isChannelEnabled(i) +
                    ", gain=" + getChannelGain(i) +
                    ", commutatorState=" + getChannelCommutatorState(i) +
                    ", isLoffEnable=" + isChannelLoffEnable(i) +
                    ", isRldSenseEnabled=" + isChannelRldSenseEnable(i) +
                    '}' + "\n");

            sb.append("\r");
        }
        return "AdsConfiguration{" +
                "sps=" + sps +
                ", isAccelerometerEnabled=" + isAccelerometerEnabled +
                ", accelerometerDivider=" + accelerometerDivider +
                ", comPortName='" + comPortName + '\'' +
                ", isHighResolutionMode=" + isHighResolutionMode +
                '}' + sb.toString();
    }


    public AdsConfiguratorDorokhov getAdsConfigurator() {
        return new AdsConfiguratorDorokhov();
    }
}
