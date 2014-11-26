package dreamrec;

import bdf.BdfProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class ApplicationFactory {
    private static final Log log = LogFactory.getLog(ApplicationFactory.class);
    private  BdfProvider device;
    private  ApplicationConfig config;

    public ApplicationFactory(ApplicationConfig config) {
        this.config = config;
    }

    public BdfProvider getDeviceImplementation() throws ApplicationException {
        if(device == null) {
            String deviceClassName = config.getDeviceClassName();
            if(deviceClassName == null) {
                throw new ApplicationException("Device type or Implementing Class \n is not specified");
            }
            try {
                Class deviceClass = Class.forName(deviceClassName);
                device = (BdfProvider)deviceClass.newInstance();
            } catch (ClassNotFoundException e) {
                log.error(e);
                throw new ApplicationException("Device Implementing Class is not found ");
            }
            catch (InstantiationException e) {
                log.error(e);
                throw new ApplicationException("Device can not be Instantiated");
            } catch (IllegalAccessException e) {
                log.error(e);
                throw new ApplicationException("Device can not be Instantiated");
            }
        }
        return device;
    }

    public RemConfig getDeviceRemConfig() throws ApplicationException {
        int accelerometerX = config.getAccelerometerXChannelNumber();
        int accelerometerY = config.getAccelerometerYChannelNumber();
        int accelerometerZ = config.getAccelerometerZChannelNumber();
        int eog = config.getEogChannelNumber();
        double  eogRemFrequency = config.getEogRemFrequency();
        double  accelerometerRemFrequency = config.getAccelerometerRemFrequency();
        if(accelerometerX < 0) {
            throw new ApplicationException("Device channel number that correspond accelerometerX \n" +
                    " is not specified");
        }
        if(accelerometerY < 0) {
            throw new ApplicationException("Device channel number that correspond accelerometerY \n" +
                    " is not specified");
        }
        if(accelerometerZ < 0) {
            throw new ApplicationException("Device channel number that correspond accelerometerz \n" +
                    " is not specified");
        }
        if(eog < 0) {
            throw new ApplicationException("Device channel number that correspond EOG \n" +
                    " is not specified");
        }
        if(eog < 0) {
            throw new ApplicationException("Device channel number that correspond EOG \n" +
                    " is not specified");
        }
        if(eogRemFrequency < 0) {
            throw new ApplicationException("EOG REM Frequency is not specified");
        }
        if(eogRemFrequency < 0) {
            throw new ApplicationException("Accelerometer REM Frequency is not specified");
        }

        RemConfig remConfig = new RemConfig(eogRemFrequency, accelerometerRemFrequency, eog,
                accelerometerX, accelerometerY, accelerometerZ);

        return remConfig;
    }

}
