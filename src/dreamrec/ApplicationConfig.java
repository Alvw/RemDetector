package dreamrec;

public interface ApplicationConfig {
    public String getDeviceClassName();
    public int getEogChannelNumber();
    public int getAccelerometerXChannelNumber();
    public int getAccelerometerYChannelNumber();
    public int getAccelerometerZChannelNumber();

    public int getAccelerometerRemFrequency();
    public int getEogRemFrequency();

    public int getEogRemCutoffPeriod();

    public boolean isFrequencyAutoAdjustment();

}
