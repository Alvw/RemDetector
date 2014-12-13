package properties;

import dreamrec.ApplicationException;
import gui.GuiConfig;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GuiProperties implements GuiConfig{
    private static final Log log = LogFactory.getLog(GuiProperties.class);
    private static final String APPLICATION_PROPERTIES = "config/application.properties";
    private static final String DIRECTORY_TO_READ = "directory_to_read";
    private static final String DIRECTORY_TO_SAVE = "directory_to_save";

    private static PropertiesConfiguration config;

    public GuiProperties() throws ApplicationException {
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

}
