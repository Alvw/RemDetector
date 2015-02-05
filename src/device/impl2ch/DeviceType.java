package device.impl2ch;

import gnu.io.SerialPort;
import static device.impl2ch.Divider.*;

public enum DeviceType {
    ADS1298(8, D10, new Divider[]{D1, D2, D5, D10}, new Divider[]{D1, D2, D5, D10},
            new ComPortParams(460800, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE)),

    ADS1292(2, D50, new Divider[]{D1, D2, D5, D10, D25, D50}, new Divider[]{D10, D25, D50},
            new ComPortParams(230400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE)),

    ADS1292Dorokhov(2, D10, new Divider[]{D1, D2, D5, D10}, new Divider[]{D10},
            new ComPortParams(460800, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE));


    private int numberOfAdsChannels;
    private Divider[] channelsAvailableDividers;
    private Divider[] getAccelerometerAvailableDividers;
    private ComPortParams comPortParams;
    private Divider maxDiv;

    DeviceType(int numberOfAdsChannels, Divider maxDiv, Divider[] channelsAvailableDividers, Divider[] getAccelerometerAvailableDividers, ComPortParams comPortParams) {
        this.maxDiv = maxDiv;
        this.numberOfAdsChannels = numberOfAdsChannels;
        this.channelsAvailableDividers = channelsAvailableDividers;
        this.getAccelerometerAvailableDividers = getAccelerometerAvailableDividers;
        this.comPortParams = comPortParams;
    }

    public Divider getMaxDiv(){
        return maxDiv;
    }

    public int getNumberOfAdsChannels() {
        return numberOfAdsChannels;
    }

    public Divider[] getChannelsAvailableDividers() {
        return channelsAvailableDividers;
    }

    public Divider[] getGetAccelerometerAvailableDividers() {
        return getAccelerometerAvailableDividers;
    }

    public ComPortParams getComPortParams() {
        return comPortParams;
    }

    public AdsConfigurator getAdsConfigurator(){
        if(this.name().equals("ADS1292")){
            return new AdsConfigurator2Ch();
        }
        if(this.name().equals("ADS1298")) {
            return new AdsConfigurator8Ch();
        }
        if(this.name().equals("ADS1292Dorokhov")) {
            return new AdsConfiguratorDorokhov();
        }
        throw new IllegalStateException("Number of DeviceBle channel shoul be 2 or 8");
    }
}
