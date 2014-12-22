package properties;

import dreamrec.ApplicationException;
import gui.GuiConfig;

public class GuiProperties extends FileProperties implements GuiConfig{
    private static final String DIRECTORY_TO_READ = "directory_to_read";
    private static final String DIRECTORY_TO_SAVE = "directory_to_save";

    public GuiProperties(String file) throws ApplicationException {
        super(file);

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
