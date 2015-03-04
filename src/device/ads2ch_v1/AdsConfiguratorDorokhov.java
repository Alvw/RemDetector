package device.ads2ch_v1;

import device.impl2ch.AdsChannelConfiguration;
import device.impl2ch.CommutatorState;

import java.util.ArrayList;
import java.util.List;


public class AdsConfiguratorDorokhov {

    public static final int NUMBER_OF_ADS_CHANNELS = 2;
    public static final int NUMBER_OF_ACCELEROMETER_CHANNELS = 3;


    public List<Byte> writeAdsConfiguration(AdsConfiguration adsConfiguration) {
        List<Byte> result = new ArrayList<Byte>();
        result.add((byte)29);       //длина пакета
        result.add((byte)0xFF);     //stop recording

        result.add((byte)0xF0);     //ads1292 command
        result.add((byte)0x11);     //ads1292 stop continuous

        result.add((byte)0xF1);     //запись регистров ads1292
        result.add((byte)0x01);     //адрес первого регистра
        result.add((byte)0x0A);     //количество регистров

        int config1RegisterValue = adsConfiguration.getSps().getRegisterBits();
        result.add((byte)config1RegisterValue);

        int config2RegisterValue = 0xA0 + loffComparatorEnabledBit(adsConfiguration) + testSignalEnabledBits(adsConfiguration);
        result.add((byte)config2RegisterValue);

         //reg 0x03
        result.add((byte)0x10);
         //reg 0x04
        result.add((byte)getChanelRegisterValue(adsConfiguration.getAdsChannels().get(0)));      //reg 0x04 Set Channel 1 to test
         //reg 0x05
        result.add((byte)getChanelRegisterValue(adsConfiguration.getAdsChannels().get(1)));     //reg 0x05 Set Channel 2 to Input Short and disable
        //reg 0x06 Turn on Drl.
        result.add((byte)0x20);

        //reg 0x07
        int loffSensRegisterValue = 0;
         if(adsConfiguration.getAdsChannels().get(0).isLoffEnable()){
            loffSensRegisterValue += 0x03;
        }
        if(adsConfiguration.getAdsChannels().get(1).isLoffEnable()){
            loffSensRegisterValue += 0x0C;
        }
        result.add((byte)loffSensRegisterValue);     //reg 0x07

        result.add((byte)0x40);     //reg 0x08 clock divider Fclc/16 2048mHz external clock
        result.add((byte)0x02);     //reg 0x09 Set mandatory bit. RLD REF INT doesn't work without it.
        result.add((byte)0x03);     //reg 0x0A Set RLDREF_INT

        result.add((byte)0xF2);     //делители частоты для 2х каналов ads1292  возможные значения 0,1,2,5,10;
        for (int i = 0; i < NUMBER_OF_ADS_CHANNELS; i++) {
            AdsChannelConfiguration adsChannelConfiguration = adsConfiguration.getAdsChannels().get(i);
            int divider = adsChannelConfiguration.isEnabled() ? adsChannelConfiguration.getDivider().getValue() : 0;
            result.add((byte)divider);
        }

        result.add((byte)0xF3);     //accelerometer mode: 0 - disabled, 1 - enabled
        int accelerometerMode = adsConfiguration.isAccelerometerEnabled() ? 1 : 0;
        result.add((byte)accelerometerMode);

        result.add((byte)0xF4);     //передача данных loff статуса: 0 - disabled, 1 - enabled
        int loffEnabled = adsConfiguration.isLoffEnabled() ? 1 : 0;
        result.add((byte)loffEnabled);

        result.add((byte)0xF0);     //ads1292 command
        result.add((byte)0x10);     //ads1292 start continuous

        result.add((byte)0xFE);     //start recording

        result.add((byte)0x55);     //footer1
        result.add((byte)0x55);     //footer1

        return result;
    }

    private int getChanelRegisterValue(AdsChannelConfiguration channelConfiguration) {
        int result = 0x80;   //channel disabled
        if (channelConfiguration.isEnabled()) {
            result = 0x00;
        }
        return result + channelConfiguration.getGain().getRegisterBits() + channelConfiguration.getCommutatorState().getRegisterBits();
    }

    private int loffComparatorEnabledBit(AdsConfiguration configuration) {
        int result = 0x00;
        for (AdsChannelConfiguration adsChannelConfiguration : configuration.getAdsChannels()) {
            if (adsChannelConfiguration.isLoffEnable()) {
                result = 0x40;
            }
        }
        return result;
    }

    private int testSignalEnabledBits(AdsConfiguration configuration) {
        int result = 0x00;
        for (AdsChannelConfiguration adsChannelConfiguration : configuration.getAdsChannels()) {
            if (adsChannelConfiguration.getCommutatorState().equals(CommutatorState.TEST_SIGNAL)) {
                result = 0x03;
            }
        }
        return result;
    }

    public List<Byte> startPinLo() {
        List<Byte> result = new ArrayList<Byte>();
        result.add((byte)0xFF);     //stop recording
        return result;
    }
}
