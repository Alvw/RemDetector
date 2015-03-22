package device.ads8ch_v0;

import device.general.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 22/03/15.
 */
public class AdsConfiguratorCh8V0 implements AdsConfigurator {

    public static final int NUMBER_OF_ADS_CHANNELS = 8;
    public static final int NUMBER_OF_ACCELEROMETER_CHANNELS = 3;
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

    public static final Divider MAX_DIVIDER = Divider.D10;
    private static final String PROPERTIES_FILE_NAME = "ads8ch_v0_config.properties";
    private static final int COM_PORT_SPEED = 460800;
    private AdsConfiguration adsConfiguration = new AdsConfiguration(PROPERTIES_FILE_NAME, NUMBER_OF_ADS_CHANNELS, COM_PORT_SPEED, MAX_DIVIDER);


    @Override
    public FrameDecoder getFrameDecoder() {
        return new FrameDecoderCh8V0(adsConfiguration);
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
            result.addAll(writeDividerForChannel(i, divider));
        }
        result.addAll(writeAccelerometerEnabled(adsConfiguration.isAccelerometerEnabled()));

        result.addAll(writeRegister(0x41, getRegister_1Value()));  //set SPS
        result.addAll(writeRegister(0x42, testSignalEnabledBits()));  //test signal
        if(isLoffEnabled()){
            result.addAll(writeRegister(0x44, 0x13)); //turn on DC lead off detection
            result.addAll(writeRegister(0x57, 0x02)); //turn on loff comparators
        } else {
            result.addAll(writeRegister(0x44, 0x00)); //default LOFF register value
            result.addAll(writeRegister(0x57, 0x00)); //default CONF4 register value. turn off loff comparators
        }
        for (int i = 0; i < 8; i++) {
            result.addAll(writeRegister(0x45 + i, getChanelRegisterValue(i)));
        }
        int rlsSensBits = getRLDSensBits();
        result.addAll(writeRegister(0x4D, rlsSensBits));  //RLD sens positive
        result.addAll(writeRegister(0x4E, rlsSensBits));  //RLD sens negative

        int loffSensBits = getLoffSensRegisterValue();
        result.addAll(writeRegister(0x4F, loffSensBits));  //loff sens positive
        result.addAll(writeRegister(0x50, loffSensBits));  //loff sens negative

        result.addAll(writeConfigDataReceivedCode());
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

    private int getRegister_1Value() {
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

    private int getChanelRegisterValue(int channelNumber) {
        int result = 0x80;   //channel disabled
        if (adsConfiguration.isChannelEnabled(channelNumber)) {
            result = 0x00;
        }
        return result + adsConfiguration.getChannelGain(channelNumber).getRegisterBits() + adsConfiguration.getChannelCommutatorState(channelNumber).getRegisterBits();
    }

    private int testSignalEnabledBits() {
        int result = 0x00;
        for (int i = 0; i < NUMBER_OF_ADS_CHANNELS; i++) {
            if (adsConfiguration.getChannelCommutatorState(i).equals(CommutatorState.TEST_SIGNAL)) {
                result = 0x10;
            }
        }
        return result;
    }

    private boolean isLoffEnabled() {
        for (int i = 0; i < NUMBER_OF_ADS_CHANNELS; i++) {
            if (adsConfiguration.isChannelEnabled(i) && adsConfiguration.isChannelLoffEnable(i)) {
                return true;
            }
        }
        return false;
    }

    private int getLoffSensRegisterValue(){
        int result = 0;
        for (int i = 0; i < NUMBER_OF_ADS_CHANNELS; i++) {
            result += (adsConfiguration.isChannelEnabled(i) && adsConfiguration.isChannelLoffEnable(i)) ? Math.pow(2, i) : 0;
        }
        return result;
    }

    private int getRLDSensBits() {
        int result = 0;
        for (int i = 0; i < NUMBER_OF_ADS_CHANNELS; i++) {
            result += adsConfiguration.isChannelRldSenseEnable(i) ? Math.pow(2, i) : 0;
        }
        return result;
    }
}
