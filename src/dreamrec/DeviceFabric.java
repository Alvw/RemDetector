package dreamrec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class DeviceFabric {
    private static final Log log = LogFactory.getLog(DeviceFabric.class);
    private  BdfDevice device;
    private  String deviceClassName;

    public DeviceFabric(String deviceClassName) {
        this.deviceClassName = deviceClassName;
    }

   synchronized public BdfDevice getDeviceImplementation() throws ApplicationException {
        if(device == null) {

            if(deviceClassName == null) {
                throw new ApplicationException("Device type or Implementing Class is not specified");
            }
            try {
                Class deviceClass = Class.forName(deviceClassName);
                device = (BdfDevice)deviceClass.newInstance();
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
