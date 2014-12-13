package properties;

import dreamrec.ApplicationException;

import java.io.File;

public class ApplicationProperties extends FileProperties {
    private static final String DEVICE_TYPE = "device.type";
    private static final String DEVICE_CLASSNAME = "device.classname";
    private static final String DEVICE_CHANNEL = "device.channel";
    private static final String IS_FREQUENCY_AUTO_ADJUSTMENT = "is_frequency_auto_adjustment";

    public ApplicationProperties(File file) throws ApplicationException {
        super(file);
        String deviceType = config.getString(DEVICE_TYPE);
        if(deviceType != null) {
            String devicePropertiesFilename = deviceType.concat(".").concat("properties");
            File devicePropertiesFile = new File(file.getParent(), devicePropertiesFilename);
            addPropertiesFile(devicePropertiesFile);
        }
    }

    public String getDeviceClassName()  {
        return config.getString(DEVICE_CLASSNAME);

    }

    public boolean isFrequencyAutoAdjustment() {
        boolean defaultValue = true;
        return config.getBoolean(IS_FREQUENCY_AUTO_ADJUSTMENT, defaultValue);
    }

    public String[] getDeviceChannelsLabels(int numberOfChannels) {
        //Iterator<String> keys = config.getKeys(DEVICE_CHANNEL);
        String[] labels = new String[numberOfChannels];
        for(int i = 0; i < numberOfChannels; i++) {
            String key = DEVICE_CHANNEL + "." + i;
            labels[i] = config.getString(key);
        }

        return labels;
    }

}
