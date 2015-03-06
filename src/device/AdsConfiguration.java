package device;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 */
public class AdsConfiguration {

    private static final Log log = LogFactory.getLog(AdsConfiguration.class);

    public static final String COM_PORT_NAME = "comPort";
    public static final String SPS = "sps";
    public static final String CHANNEL_DIVIDER = "dividerChannel";
    public static final String CHANNEL_GAIN = "gainChannel";
    public static final String CHANNEL_COMMUTATOR_STATE = "commutatorStateChannel";
    public static final String CHANNEL_IS_ENABLED = "isEnabledChannel";
    public static final String CHANNEL_IS_LOFF_ENABLED = "isLoffEnabledChannel";
    public static final String CHANNEL_IS_RLD_SENSE_ENABLED = "isRldSenseEnabledChannel";
    public static final String ACCELEROMETER_IS_ENABLED = "isEnabledAccelerometer";

    private String fileName;
    private int numberOfAdsChannels;
    private boolean isHighResolutionMode = true;
    private  Divider accelerometerDivider = Divider.D10;
    private int comPortSpeed;
    private final static Divider MAX_DIVIDER = Divider.D10;

    private PropertiesConfiguration config;

    public AdsConfiguration(String propertiesFileName, int numberOfAdsChannels, int comPortSpeed) {
        this.numberOfAdsChannels = numberOfAdsChannels;
        this.comPortSpeed = comPortSpeed;
        fileName = propertiesFileName;
        try {
            config = new PropertiesConfiguration(propertiesFileName);
        } catch (ConfigurationException e) {
            log.error(e);
        }
    }

    public static Divider getMaxDivider() {
        return MAX_DIVIDER;
    }

    public int getComPortSpeed() {
        return comPortSpeed;
    }

    public int getNumberOfAdsChannels() {
        return numberOfAdsChannels;
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
            return accelerometerDivider;
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
        return config.getBoolean(CHANNEL_IS_LOFF_ENABLED + channelNumber);
    }

    public boolean isChannelRldSenseEnable(int channelNumber) {
        return config.getBoolean(CHANNEL_IS_RLD_SENSE_ENABLED + channelNumber);
    }

    public void setChannelRldSenseEnabled(int channelNumber, boolean isRldEnabled) {
        config.setProperty(CHANNEL_IS_RLD_SENSE_ENABLED + channelNumber, isRldEnabled);
    }

    public void setChannelLoffEnabled(int channelNumber, boolean isLoffEnabled) {
        config.setProperty(CHANNEL_IS_LOFF_ENABLED + channelNumber, isLoffEnabled);
    }

    public void save() {
        try {
            config.save(fileName);
        } catch (ConfigurationException e) {
            log.error(e);
        }
    }


    public boolean isHighResolutionMode() {
        return isHighResolutionMode;
    }
    public boolean isLoffEnabled() {
        for (int i = 0; i < numberOfAdsChannels; i++) {
           if(isChannelLoffEnable(i)){
               return true;
           }
        }
        return false;
    }



    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < numberOfAdsChannels; i++) {
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
                "sps=" + getSps() +
                ", isAccelerometerEnabled=" + isAccelerometerEnabled() +
                ", accelerometerDivider=" + getAccelerometerDivider() +
                ", comPortName='" + getComPortName() + '\'' +
                ", isHighResolutionMode=" + isHighResolutionMode +
                '}' + sb.toString();

    }
}
