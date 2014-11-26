package gui;

import dreamrec.ApplicationConfig;

public class GuiConfig {
    private ApplicationConfig applicationConfig;

    public GuiConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public String getDirectoryToSave() {
        return applicationConfig.getDirectoryToSave();
    }
    public String getDirectoryToRead() {
        return applicationConfig.getDirectoryToRead();
    }
    public void setDirectoryToSave(String directory) {
        applicationConfig.setDirectoryToSave(directory);
    }
    public void setDirectoryToRead(String directory) {
        applicationConfig.setDirectoryToRead(directory);
    }
}
