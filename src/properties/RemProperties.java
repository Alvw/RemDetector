package properties;

import dreamrec.ApplicationException;

import java.io.File;

/**
 * Created by mac on 12/12/14.
 */
public class RemProperties extends FileProperties  {
    private static final String ACCELEROMETER_REM_FREQUENCY = "rem.accelerometer_frequency";
    private static final String EOG_REM_FREQUENCY = "rem.eog_frequency";
    private static final String EOG_REM_CUTOFF_PERIOD = "rem.eog_cutoff_period";

    public RemProperties(File file) throws ApplicationException {
       super(file);
    }


    public int getAccelerometerRemFrequency() {
        int defaultValue = -1;
        return config.getInt(ACCELEROMETER_REM_FREQUENCY, defaultValue);
    }


    public int getEogRemFrequency() {
        int defaultValue = -1;
        return config.getInt(EOG_REM_FREQUENCY, defaultValue);
    }


    public int getEogRemCutoffPeriod() {
        int defaultValue = 0;
        return config.getInt(EOG_REM_CUTOFF_PERIOD, defaultValue);
    }
}
