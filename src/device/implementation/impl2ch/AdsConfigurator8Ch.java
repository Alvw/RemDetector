package device.implementation.impl2ch;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AdsConfigurator8Ch extends AdsConfigurator {
    public static final int NUMBER_OF_ADS_CHANNELS = 8;
    public static final int NUMBER_OF_ACCELEROMETER_CHANNELS = 3;

    @Override
    public List<Byte> writeAdsConfiguration(AdsConfiguration adsConfiguration) {
        List<Byte> result = new ArrayList<Byte>();
        result.addAll(startPinLo());
        result.addAll(writeCommand(0x11));  //stop continious
        for (int i = 0; i < NUMBER_OF_ADS_CHANNELS; i++) {
            AdsChannelConfiguration adsChannelConfiguration = adsConfiguration.getAdsChannels().get(i);
            int divider = adsChannelConfiguration.isEnabled ? adsChannelConfiguration.getDivider().getValue() : 0;
            result.addAll(writeDividerForChannel(i, divider));
        }
        for (int i = NUMBER_OF_ADS_CHANNELS; i < NUMBER_OF_ACCELEROMETER_CHANNELS + NUMBER_OF_ADS_CHANNELS; i++) {
            int divider = adsConfiguration.isAccelerometerEnabled() ? adsConfiguration.getAccelerometerDivider().getValue() : 0;
            result.addAll(writeDividerForChannel(i, divider));
        }
        result.addAll(writeAccelerometerEnabled(adsConfiguration.isAccelerometerEnabled()));

        result.addAll(writeRegister(0x41, getRegister_1Value(adsConfiguration)));  //set SPS
        result.addAll(writeRegister(0x42, testSignalEnabledBits(adsConfiguration)));  //test signal
        if(isLoffEnabled(adsConfiguration)){
            result.addAll(writeRegister(0x44, 0x13)); //turn on DC lead off detection
            result.addAll(writeRegister(0x57, 0x02)); //turn on loff comparators
        } else {
            result.addAll(writeRegister(0x44, 0x00)); //default LOFF register value
            result.addAll(writeRegister(0x57, 0x00)); //default CONF4 register value. turn off loff comparators
        }
        for (int i = 0; i < 8; i++) {
            result.addAll(writeRegister(0x45 + i, getChanelRegisterValue(adsConfiguration.getAdsChannels().get(i))));
        }
        int rlsSensBits = getRLDSensBits(adsConfiguration.getAdsChannels());
        result.addAll(writeRegister(0x4D, rlsSensBits));  //RLD sens positive
        result.addAll(writeRegister(0x4E, rlsSensBits));  //RLD sens negative

        int loffSensBits = getLoffSensRegisterValue(adsConfiguration.getAdsChannels());
        result.addAll(writeRegister(0x4F, loffSensBits));  //loff sens positive
        result.addAll(writeRegister(0x50, loffSensBits));  //loff sens negative

        result.addAll(writeConfigDataReceivedCode());
        return result;
    }

    private int getRegister_1Value(AdsConfiguration adsConfiguration) {
        int registerValue = 0;
        if (adsConfiguration.isHighResolutionMode()) {
            switch (adsConfiguration.getSps()) {
                case S250:
                    registerValue = 0x06;//switch to low power mode
                    break;
                case S500:
                    registerValue = 0x86;
                    break;
                case S1000:
                    registerValue = 0x85;
                    break;
                case S2000:
                    registerValue = 0x84;
                    break;
            }
        } else {
            switch (adsConfiguration.getSps()) {
                case S250:
                    registerValue = 0x06;
                    break;
                case S500:
                    registerValue = 0x05;
                    break;
                case S1000:
                    registerValue = 0x04;
                    break;
                case S2000:
                    registerValue = 0x03;
                    break;
            }
        }
        return registerValue;
    }
    //--------------------------------

    private int getChanelRegisterValue(AdsChannelConfiguration channelConfiguration) {
        int result = 0x80;   //channel disabled
        if (channelConfiguration.isEnabled()) {
            result = 0x00;
        }
        return result + channelConfiguration.getGain().getRegisterBits() + channelConfiguration.getCommutatorState().getRegisterBits();
    }

    private int testSignalEnabledBits(AdsConfiguration configuration) {
        int result = 0x00;
        for (AdsChannelConfiguration adsChannelConfiguration : configuration.getAdsChannels()) {
            if (adsChannelConfiguration.getCommutatorState().equals(CommutatorState.TEST_SIGNAL)) {
                result = 0x10;
            }
        }
        return result;
    }

    private boolean isLoffEnabled(AdsConfiguration configuration) {
        for (AdsChannelConfiguration adsChannelConfiguration : configuration.getAdsChannels()) {
            if (adsChannelConfiguration.isEnabled && adsChannelConfiguration.isLoffEnable) {
                return true;
            }
        }
        return false;
    }

    private int getLoffSensRegisterValue(List<AdsChannelConfiguration> channelConfigurationList){
        int result = 0;
        for (int i = 0; i < channelConfigurationList.size(); i++) {
            AdsChannelConfiguration adsChannelConfiguration = channelConfigurationList.get(i);
            result += (adsChannelConfiguration.isEnabled && adsChannelConfiguration.isLoffEnable()) ? Math.pow(2, i) : 0;
        }
        return result;
    }

    private int getRLDSensBits(List<AdsChannelConfiguration> channelConfigurationList) {
        int result = 0;
        for (int i = 0; i < channelConfigurationList.size(); i++) {
            AdsChannelConfiguration adsChannelConfiguration = channelConfigurationList.get(i);
            result += adsChannelConfiguration.isRldSenseEnabled() ? Math.pow(2, i) : 0;
        }
        return result;
    }
}
