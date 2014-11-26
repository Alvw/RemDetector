package dreamrec;

public interface ApplicationConfig {

    public String getDirectoryToSave();
    public String getDirectoryToRead();
    public void setDirectoryToSave(String directory);
    public void setDirectoryToRead(String directory);

    public String getDeviceClassName();
    public int getEogChannelNumber();
    public int getAccelerometerXChannelNumber();
    public int getAccelerometerYChannelNumber();
    public int getAccelerometerZChannelNumber();

    public double getAccelerometerRemFrequency();
    public double getEogRemFrequency();

    public boolean isFrequencyAutoAdjustment();

}
