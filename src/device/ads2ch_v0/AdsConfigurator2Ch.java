package device.ads2ch_v0;

import device.AdsConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AdsConfigurator2Ch extends AdsConfigurator {

    public static final int NUMBER_OF_ADS_CHANNELS = 2;
    public static final int NUMBER_OF_ACCELEROMETER_CHANNELS = 3;

    @Override
    public List<Byte> writeAdsConfiguration(AdsConfiguration adsConfiguration) {
        List<Byte> result = new ArrayList<Byte>();
        result.addAll(startPinLo());
        result.addAll(writeCommand(0x11));  //stop continious
  /*      for (int i = 0; i < NUMBER_OF_ADS_CHANNELS; i++) {
            AdsChannelConfiguration adsChannelConfiguration = adsConfiguration.getAdsChannels().get(i);
            int divider = adsChannelConfiguration.isEnabled() ? adsChannelConfiguration.getDivider().getValue() : 0;
            result.addAll(writeDividerForChannel(i, divider));
        }
        for (int i = NUMBER_OF_ADS_CHANNELS; i < NUMBER_OF_ACCELEROMETER_CHANNELS + NUMBER_OF_ADS_CHANNELS; i++) {
            int divider = adsConfiguration.isAccelerometerEnabled() ? adsConfiguration.getAccelerometerDivider().getValue() : 0;
            result.addAll(writeDividerForChannel(i,divider ));
        }
        result.addAll(writeAccelerometerEnabled(adsConfiguration.isAccelerometerEnabled()));
        int config1RegisterValue = adsConfiguration.getSps().getRegisterBits();
        result.addAll(writeRegister(0x41, config1RegisterValue));  //set SPS

        int config2RegisterValue = 0xA0 + loffComparatorEnabledBit(adsConfiguration) + testSignalEnabledBits(adsConfiguration);
        result.addAll(writeRegister(0x42, config2RegisterValue));

        result.addAll(writeRegister(0x43, 0x10));//Loff comparator threshold

        result.addAll(writeRegister(0x44, getChanelRegisterValue(adsConfiguration.getAdsChannels().get(0))));
        result.addAll(writeRegister(0x45, getChanelRegisterValue(adsConfiguration.getAdsChannels().get(1))));

        int rldSensRegisterValue = 0x20;
        if(adsConfiguration.getAdsChannels().get(0).isRldSenseEnabled()){
            rldSensRegisterValue += 0x03;
        }
        if(adsConfiguration.getAdsChannels().get(1).isRldSenseEnabled()){
            rldSensRegisterValue += 0x0C;
        }
        result.addAll(writeRegister(0x46, rldSensRegisterValue));

        int loffSensRegisterValue = 0;
         if(adsConfiguration.getAdsChannels().get(0).isLoffEnable()){
            loffSensRegisterValue += 0x03;
        }
        if(adsConfiguration.getAdsChannels().get(1).isLoffEnable()){
            loffSensRegisterValue += 0x0C;
        }
        result.addAll(writeRegister(0x47, loffSensRegisterValue));

        result.addAll(writeConfigDataReceivedCode());*/
        return result;
    }

/*    private int getChanelRegisterValue(AdsChannelConfiguration channelConfiguration) {
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
    } */
}
