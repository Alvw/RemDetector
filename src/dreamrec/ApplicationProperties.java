package dreamrec;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ApplicationProperties implements ApplicationConfig {
    private static final Log log = LogFactory.getLog(ApplicationProperties.class);
    private static final String APPLICATION_PROPERTIES = "application.properties";
    private static final String DEVICE_TYPE = "device_type";
    private static final String DIRECTORY_TO_READ = "directory_to_read";
    private static final String DIRECTORY_TO_SAVE = "directory_to_save";
    private static final String DEVICE_CLASS_NAME = "class_name";
    private static final String DEVICE_EOG = "EOG_channel_number";
    private static final String DEVICE_ACCELEROMETER_X = "accelerometerX_channel_number";
    private static final String DEVICE_ACCELEROMETER_Y = "accelerometerY_channel_number";
    private static final String DEVICE_ACCELEROMETER_Z = "accelerometerZ_channel_number";
    private static final String ACCELEROMETER_REM_FREQUENCY = "accelerometer_rem_frequency";
    private static final String EOG_REM_FREQUENCY = "eog_rem_frequency";
    private static final String EOG_REM_CUTOFF_PERIOD = "eog_rem_cutoff_period";
    private static final String IS_FREQUENCY_AUTO_ADJUSTMENT = "is_frequency_auto_adjustment";

    private static PropertiesConfiguration config;

    public ApplicationProperties() throws ApplicationException {
        try {
            config = new PropertiesConfiguration(APPLICATION_PROPERTIES);
        } catch (ConfigurationException e) {
            log.error(e);
            throw new ApplicationException("Error reading from properties file: " + APPLICATION_PROPERTIES);
        }
    }

    @Override
    public String getDirectoryToSave() {
        return config.getString(DIRECTORY_TO_SAVE);
    }

    @Override
    public String getDirectoryToRead() {
        return config.getString(DIRECTORY_TO_READ);
    }

    @Override
    public void setDirectoryToSave(String directory) {
        if(directory != null) {
            config.setProperty(DIRECTORY_TO_SAVE, directory);
        }
    }

    @Override
    public void setDirectoryToRead(String directory) {
        if(directory != null) {
            config.setProperty(DIRECTORY_TO_READ, directory);
        }
    }

    @Override
    public String getDeviceClassName() {
        String deviceImplKey = getDeviceType().concat("_").concat(DEVICE_CLASS_NAME);
        return config.getString(deviceImplKey);
    }

    @Override
    public int getEogChannelNumber() {
        String key = getDeviceType().concat("_").concat(DEVICE_EOG);
        int defaultValue = -1;
        return config.getInt(key, defaultValue);
    }

    @Override
    public int getAccelerometerXChannelNumber() {
        String key = getDeviceType().concat("_").concat(DEVICE_ACCELEROMETER_X);
        int defaultValue = -1;
        return config.getInt(key, defaultValue);
    }

    @Override
    public int getAccelerometerYChannelNumber() {
        String key = getDeviceType().concat("_").concat(DEVICE_ACCELEROMETER_Y);
        int defaultValue = -1;
        return config.getInt(key, defaultValue);
    }

    @Override
    public int getAccelerometerZChannelNumber() {
        String key = getDeviceType().concat("_").concat(DEVICE_ACCELEROMETER_Z);
        int defaultValue = -1;
        return config.getInt(key, defaultValue);
    }

    @Override
    public int getAccelerometerRemFrequency() {
        int defaultValue = -1;
        return config.getInt(ACCELEROMETER_REM_FREQUENCY, defaultValue);
    }

    @Override
    public int getEogRemFrequency() {
        int defaultValue = -1;
        return config.getInt(EOG_REM_FREQUENCY, defaultValue);
    }

    @Override
    public boolean isFrequencyAutoAdjustment() {
        return config.getBoolean(IS_FREQUENCY_AUTO_ADJUSTMENT);
    }

    private String getDeviceType() {
        return config.getString(DEVICE_TYPE);
    }

    @Override
    public int getEogRemCutoffPeriod() {
        return config.getInt(EOG_REM_CUTOFF_PERIOD);
    }
}
