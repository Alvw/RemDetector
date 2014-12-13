package device.impl2ch;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 *
 */
class AdsConfigProperties {
    private static final Log log = LogFactory.getLog(AdsConfigProperties.class);
    private static final String ADS_CONFIG_PROPERTIES = "config/ads_config.properties";

    public static final String DEVICE_TYPE = "deviceType";
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
    private PropertiesConfiguration config;

    public AdsConfigProperties() {
        try {
            config = new PropertiesConfiguration(ADS_CONFIG_PROPERTIES);
        } catch (ConfigurationException e) {
            log.error(e);
        }
    }

    public DeviceType getDeviceType(){
            return DeviceType.valueOf(config.getString(DEVICE_TYPE));
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

    public Divider getAccelerometerDivider() {
        try {
            return Divider.valueOf(config.getInt(ACCELEROMETER_DIVIDER));
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


    public void setSps(Sps sps) {
        config.setProperty(SPS, sps);
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

    public void setAccelerometerDivider(Divider divider) {
        config.setProperty(ACCELEROMETER_DIVIDER, divider);
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
            config.save(ADS_CONFIG_PROPERTIES);
        } catch (ConfigurationException e) {
            log.error(e);
        }
    }
}
