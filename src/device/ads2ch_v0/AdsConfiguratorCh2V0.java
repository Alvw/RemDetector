package device.ads2ch_v0;

import device.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AdsConfiguratorCh2V0 implements AdsConfigurator{
    private static final int BYTE_0_MARKER = 0x00;
    private static final int BYTE_1_MARKER = 0x10;
    private static final int BYTE_2_MARKER = 0x20;
    private static final int BYTE_3_MARKER = 0x30;
    private static final int START_PIN_HI_CODE = 0xF0;
    private static final int START_PIN_LO_CODE = 0xF1;
    private static final int WRITE_COMMAND_CODE = 0xF2;
    private static final int WRITE_REGISTER_CODE = 0xF3;
    private static final int SET_CHANEL_DIVIDER_CODE = 0xF4;
    private static final int SET_ACCELEROMETER_ENABLED_CODE = 0xF5;
    private static final int CONFIG_DATA_RECEIVED_CODE = 0xF6;

    public static final int NUMBER_OF_ADS_CHANNELS = 2;
    public static final int NUMBER_OF_ACCELEROMETER_CHANNELS = 3;
    public static final Divider MAX_DIVIDER = Divider.D50;
    private static final String PROPERTIES_FILE_NAME = "ads2ch_v0_config.properties";
    private static final int COM_PORT_SPEED = 230400;
    private AdsConfiguration adsConfiguration = new AdsConfiguration(PROPERTIES_FILE_NAME, NUMBER_OF_ADS_CHANNELS, COM_PORT_SPEED, MAX_DIVIDER);

    @Override
    public FrameDecoder getFrameDecoder() {
        return new FrameDecoderCh2V0(adsConfiguration);
    }

    @Override
    public AdsConfiguration getAdsConfiguration() {
        return adsConfiguration;
    }

    @Override
    public List<Byte> startPinLo() {
        return write(START_PIN_LO_CODE);
    }


    @Override
    public List<Byte> writeAdsConfiguration() {
        List<Byte> result = new ArrayList<Byte>();
        result.addAll(startPinLo());
        result.addAll(writeCommand(0x11));  //stop continious
        for (int i = 0; i < NUMBER_OF_ADS_CHANNELS; i++) {
            int divider = adsConfiguration.isChannelEnabled(i) ? adsConfiguration.getChannelDivider(i).getValue() : 0;
            result.addAll(writeDividerForChannel(i, divider));
        }
        for (int i = NUMBER_OF_ADS_CHANNELS; i < NUMBER_OF_ACCELEROMETER_CHANNELS + NUMBER_OF_ADS_CHANNELS; i++) {
            int divider = adsConfiguration.isAccelerometerEnabled() ? adsConfiguration.getAccelerometerDivider().getValue() : 0;
            result.addAll(writeDividerForChannel(i,divider ));
        }
        result.addAll(writeAccelerometerEnabled(adsConfiguration.isAccelerometerEnabled()));
        int config1RegisterValue = adsConfiguration.getSps().getRegisterBits();
        result.addAll(writeRegister(0x41, config1RegisterValue));  //set SPS

        int config2RegisterValue = 0xA0 + loffComparatorEnabledBit() + testSignalEnabledBits();
        result.addAll(writeRegister(0x42, config2RegisterValue));

        result.addAll(writeRegister(0x43, 0x10));//Loff comparator threshold

        result.addAll(writeRegister(0x44, getChanelRegisterValue(0)));
        result.addAll(writeRegister(0x45, getChanelRegisterValue(1)));

        int rldSensRegisterValue = 0x20;
        if(adsConfiguration.isChannelRldSenseEnable(0)){
            rldSensRegisterValue += 0x03;
        }
        if(adsConfiguration.isChannelRldSenseEnable(1)){
            rldSensRegisterValue += 0x0C;
        }
        result.addAll(writeRegister(0x46, rldSensRegisterValue));

        int loffSensRegisterValue = 0;
         if(adsConfiguration.isChannelLoffEnable(0)){
            loffSensRegisterValue += 0x03;
        }
        if(adsConfiguration.isChannelLoffEnable(1)){
            loffSensRegisterValue += 0x0C;
        }
        result.addAll(writeRegister(0x47, loffSensRegisterValue));

        result.addAll(writeConfigDataReceivedCode());
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

    private List<Byte> write(int code) {
        List<Byte> result = new ArrayList<Byte>();
        result.add((byte) code);
        return result;
    }

    private List<Byte> write(int param, int code) {
        List<Byte> result = new ArrayList<Byte>();
        result.add((byte) (BYTE_0_MARKER | (param >> 4)));
        result.add((byte) (BYTE_1_MARKER | (param & 0x0F)));
        result.add((byte) code);
        return result;
    }

    private List<Byte> write(int param1, int param2, int code) {
        List<Byte> result = new ArrayList<Byte>();
        result.add((byte) (BYTE_0_MARKER | (param1 >> 4)));
        result.add((byte) (BYTE_1_MARKER | (param1 & 0x0F)));
        result.add((byte) (BYTE_2_MARKER | (param2 >> 4)));
        result.add((byte) (BYTE_3_MARKER | (param2 & 0x0F)));
        result.add((byte) code);
        return result;
    }

    public List<Byte> startPinHi() {
        return write(START_PIN_HI_CODE);
    }

    public List<Byte> writeCommand(int command) {
        return write(command, WRITE_COMMAND_CODE);
    }

    public List<Byte> writeRegister(int address, int value) {
        return write(address, value, WRITE_REGISTER_CODE);
    }

    public List<Byte> writeDividerForChannel(int chanelNumber, int divider) {
        return write(chanelNumber, divider, SET_CHANEL_DIVIDER_CODE);
    }

    public List<Byte> writeAccelerometerEnabled(boolean isAccelerometerEnabled) {
        int isEnabled = isAccelerometerEnabled ? 1 : 0;
        return write(isEnabled, SET_ACCELEROMETER_ENABLED_CODE);
    }

    public List<Byte> writeConfigDataReceivedCode() {
        return write(CONFIG_DATA_RECEIVED_CODE);
    }


}
