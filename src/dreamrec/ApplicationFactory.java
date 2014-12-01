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
                throw new ApplicationException("Device type or Implementing Class is not specified");
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
}
