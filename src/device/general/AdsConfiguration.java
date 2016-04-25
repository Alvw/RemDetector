package device.general;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 */
public class AdsConfiguration {

    private static final Log log = LogFactory.getLog(AdsConfiguration.class);

    private static final String COM_PORT_NAME = "comPort";
    private static final String SPS = "sps";
    private static final String CHANNEL_DIVIDER = "dividerChannel";
    private static final String CHANNEL_GAIN = "gainChannel";
    private static final String CHANNEL_COMMUTATOR_STATE = "commutatorStateChannel";
    private static final String CHANNEL_IS_ENABLED = "isEnabledChannel";
    private static final String CHANNEL_IS_LOFF_ENABLED = "isLoffEnabledChannel";
    private static final String CHANNEL_IS_RLD_SENSE_ENABLED = "isRldSenseEnabledChannel";
    private static final String ACCELEROMETER_IS_ENABLED = "isEnabledAccelerometer";
    private static final String ACCELEROMETER_DIVIDER = "dividerAccelerometer";
    private static final String IS_BATTERY_VOLTAGE_ENABLED = "isBatteryVoltageMeasureEnabled";

    private String fileName;
    private int numberOfAdsChannels;
    private boolean isHighResolutionMode = true;
    private  Divider accelerometerDivider;
    private int comPortSpeed;
    private Divider maxDivider;
    private static final int NUMBER_OF_BYTES_IN_DATA_FORMAT = 3;

    private PropertiesConfiguration config;

    public AdsConfiguration(String propertiesFileName, int numberOfAdsChannels, int comPortSpeed, Divider maxDivider, Divider accDivider) {
        this.numberOfAdsChannels = numberOfAdsChannels;
        this.comPortSpeed = comPortSpeed;
        this.maxDivider = maxDivider;
        this.accelerometerDivider = accDivider;
        fileName = propertiesFileName;
        try {
            config = new PropertiesConfiguration(propertiesFileName);
        } catch (ConfigurationException e) {
            log.error(e);
        }
    }

    public AdsConfiguration(String propertiesFileName, int numberOfAdsChannels, int comPortSpeed, Divider maxDivider) {
        this(propertiesFileName, numberOfAdsChannels, comPortSpeed, maxDivider, null);
    }

    public boolean isBatteryVoltageMeasureEnabled() {
        return config.getBoolean(IS_BATTERY_VOLTAGE_ENABLED);
    }

    public int getNumberOfBytesInDataFormat() {
        return NUMBER_OF_BYTES_IN_DATA_FORMAT;
    }

    public  Divider getMaxDivider() {
        return maxDivider;
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
        if(accelerometerDivider != null) {
            return accelerometerDivider;
        }
        try {
            return Divider.valueOf(config.getInt(ACCELEROMETER_DIVIDER));
        } catch (IllegalArgumentException e) {
            String msg = "ads_config.properties file: " +  "accelerometer divider " + e.getMessage();
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

    public int getTotalNumberOfDataSamplesInEachDataRecord() {
        int totalNumberOfDataSamples = 0;
        for (int i = 0; i < getNumberOfAdsChannels(); i++) {
            if(isChannelEnabled(i)) {
                int numberOfSamplesInEachDataRecord = getMaxDivider().getValue() / getChannelDivider(i).getValue();
                totalNumberOfDataSamples += numberOfSamplesInEachDataRecord;
            }

        }
        if(isAccelerometerEnabled()) {
            int numberOfSamplesInEachDataRecord = getMaxDivider().getValue()  / getAccelerometerDivider().getValue();
            totalNumberOfDataSamples += 3 * numberOfSamplesInEachDataRecord;
        }
        return totalNumberOfDataSamples;
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
