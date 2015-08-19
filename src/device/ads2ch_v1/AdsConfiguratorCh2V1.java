package device.ads2ch_v1;

import device.general.*;

import java.util.ArrayList;
import java.util.List;


public class AdsConfiguratorCh2V1 implements AdsConfigurator {
    private static final String PROPERTIES_FILE_NAME = "ads2ch_v1_config.properties";
    private static final int NUMBER_OF_ADS_CHANNELS = 2;
    public static final Divider MAX_DIVIDER = Divider.D10;
    public static final Divider ACCELEROMETER_DIVIDER = Divider.D10;
    private static final int COM_PORT_SPEED = 460800;
    private AdsConfiguration adsConfiguration = new AdsConfiguration(PROPERTIES_FILE_NAME, NUMBER_OF_ADS_CHANNELS, COM_PORT_SPEED, MAX_DIVIDER, ACCELEROMETER_DIVIDER);
    @Override
    public FrameDecoder getFrameDecoder() {
        return new FrameDecoderCh2V1(adsConfiguration);
    }

    @Override
    public AdsConfiguration getAdsConfiguration() {
        return adsConfiguration;
    }

    @Override
    public List<Byte> startPinLo() {
        List<Byte> result = new ArrayList<Byte>();
        result.add((byte)0xFF);     //stop recording
        return result;
    }

    @Override
    public List<Byte> writeAdsConfiguration() {
        List<Byte> result = new ArrayList<Byte>();
        result.add((byte)32);       //длина пакета

        result.add((byte)0xF0);     //ads1292 command
        result.add((byte)0x11);     //ads1292 stop continuous

        result.add((byte)0xF1);     //запись регистров ads1292
        result.add((byte)0x01);     //адрес первого регистра
        result.add((byte)0x0A);     //количество регистров

        int config1RegisterValue = adsConfiguration.getSps().getRegisterBits();
        result.add((byte)config1RegisterValue);

        int config2RegisterValue = 0xA0 + loffComparatorEnabledBit() + testSignalEnabledBits();
        result.add((byte)config2RegisterValue);

         //reg 0x03
        result.add((byte)0x10);
         //reg 0x04
        result.add((byte)getChanelRegisterValue(0));      //reg 0x04 Set Channel 1 to test
         //reg 0x05
        result.add((byte)getChanelRegisterValue(1));     //reg 0x05 Set Channel 2 to Input Short and disable
        //reg 0x06 Turn on Drl.
        result.add((byte)0x20);

        //reg 0x07
        int loffSensRegisterValue = 0;
        if(adsConfiguration.isChannelLoffEnable(0)){
            loffSensRegisterValue += 0x03;
        }
        if(adsConfiguration.isChannelLoffEnable(1)){
            loffSensRegisterValue += 0x0C;
        }
        result.add((byte)loffSensRegisterValue);     //reg 0x07

        result.add((byte)0x40);     //reg 0x08 clock divider Fclc/16 2048mHz external clock
        result.add((byte)0x02);     //reg 0x09 Set mandatory bit. RLD REF INT doesn't work without it.
        result.add((byte)0x03);     //reg 0x0A Set RLDREF_INT

        result.add((byte)0xF2);     //делители частоты для 2х каналов ads1292  возможные значения 0,1,2,5,10;
        for (int i = 0; i < adsConfiguration.getNumberOfAdsChannels(); i++) {
            int divider = adsConfiguration.isChannelEnabled(i) ? adsConfiguration.getChannelDivider(i).getValue() : 0;
            result.add((byte)divider);
        }

        result.add((byte)0xF3);     //accelerometer mode: 0 - disabled, 1 - enabled
        int accelerometerMode = adsConfiguration.isAccelerometerEnabled() ? 1 : 0;
        result.add((byte)accelerometerMode);

        result.add((byte)0xF4);     //send battery voltage data: 0 - disabled, 1 - enabled
        result.add((byte)0);

        result.add((byte)0xF5);     //передача данных loff статуса: 0 - disabled, 1 - enabled
        int loffEnabled = adsConfiguration.isLoffEnabled() ? 1 : 0;
        result.add((byte)loffEnabled);

        result.add((byte)0xF6);     //reset timeout. In seconds
        result.add((byte)20);

        result.add((byte)0xF0);     //ads1292 command
        result.add((byte)0x10);     //ads1292 start continuous

        result.add((byte)0xFE);     //start recording

        result.add((byte)0x55);     //footer1
        result.add((byte)0x55);     //footer1

        return result;
    }

    private int getChanelRegisterValue(int channelNumber) {
        int result = 0x80;   //channel disabled
        if (adsConfiguration.isChannelEnabled(channelNumber)) {
            result = 0x00;
        }
        return result + adsConfiguration.getChannelGain(channelNumber).getRegisterBits() + adsConfiguration.getChannelCommutatorState(channelNumber).getRegisterBits();
    }

    private int loffComparatorEnabledBit() {
        int result = 0x00;
        for (int i = 0; i < adsConfiguration.getNumberOfAdsChannels(); i++) {
            if (adsConfiguration.isChannelLoffEnable(i)) {
                result = 0x40;
            }
        }
        return result;
    }

    private int testSignalEnabledBits() {
        int result = 0x00;
        for (int i = 0; i < adsConfiguration.getNumberOfAdsChannels(); i++) {
            if (adsConfiguration.getChannelCommutatorState(i).equals(CommutatorState.TEST_SIGNAL)) {
                result = 0x03;
            }
        }
        return result;
    }
}
