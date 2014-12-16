package gui;

public interface GuiConfig {
    public String getDirectoryToSave();
    public String getDirectoryToRead();
 //   public String getUserName();
 //   public void setUserName(String userName);
    public void setDirectoryToSave(String directory);
    public void setDirectoryToRead(String directory);
}
