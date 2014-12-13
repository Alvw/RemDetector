package properties;

import dreamrec.ApplicationException;
import org.apache.commons.configuration.CompositeConfiguration;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

/**
 * Created by mac on 13/12/14.
 */
public class FileProperties {
    private static final Log log = LogFactory.getLog(FileProperties.class);
    private FileConfiguration fileConfig;
    protected CompositeConfiguration config;

    public FileProperties(File file) throws ApplicationException {
        try {
            fileConfig = new PropertiesConfiguration(file);
            config = new CompositeConfiguration(fileConfig);
            fileConfig.setAutoSave(true);
        } catch (ConfigurationException e) {
            log.error(e);
            throw new ApplicationException("Error reading from properties file: " + file.getAbsolutePath());
        }
    }

    public void addPropertiesFile(File file) throws  ApplicationException {
        try {
            config.addConfiguration(new PropertiesConfiguration(file));
        } catch (ConfigurationException e) {
            log.error(e);
            throw new ApplicationException("Error reading from properties file: " + file.getAbsolutePath());
        }
    }


/*    public void save() {
        try {
            fileConfig.save();
        }catch (ConfigurationException e) {
            log.error(e);
        }
    } */
}
