package gui;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GuiProperties {
    private static final Log log = LogFactory.getLog(GuiProperties.class);
    private static final String APPLICATION_PROPERTIES = "application.properties";
    private static final String CURRENT_DIR = "current_dir";
    private static PropertiesConfiguration config;

    private static void init() {
        try {
            config = new PropertiesConfiguration(APPLICATION_PROPERTIES);
        } catch (ConfigurationException e) {
            log.error(e);
        }
    }
    public static String getCurrentDir(){
           if(config == null) {
               init();
           }
           if(config != null) {
               return config.getString(CURRENT_DIR);
           }
        return null;
    }

    public static  void setCurrentDir(String dir){
        if(config == null) {
            init();
        }
        if(config != null) {
            config.setProperty(CURRENT_DIR, dir);
        }
    }
}
