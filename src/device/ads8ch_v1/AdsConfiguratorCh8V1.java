package device.ads8ch_v1;

import device.general.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 22/03/15.
 */
public class AdsConfiguratorCh8V1 implements AdsConfigurator {

    public static final int NUMBER_OF_ADS_CHANNELS = 8;
    public static final Divider MAX_DIVIDER = Divider.D10;
    private static final String PROPERTIES_FILE_NAME = "ads8ch_v1_config.properties";
    private static final int COM_PORT_SPEED = 460800;
    private AdsConfiguration adsConfiguration = new AdsConfiguration(PROPERTIES_FILE_NAME, NUMBER_OF_ADS_CHANNELS, COM_PORT_SPEED, MAX_DIVIDER);


    @Override
    public FrameDecoder getFrameDecoder() {
        return new FrameDecoderCh8V1(adsConfiguration);
    }



    @Override
    public AdsConfiguration getAdsConfiguration() {
        return adsConfiguration;
    }

    @Override
    public List<Byte> startPinLo() {
        List<Byte> stopCmd = new ArrayList<Byte>();
        stopCmd.add((byte)0xFF);
        return stopCmd;
    }

     @Override
    public List<Byte> writeAdsConfiguration() {
        //-----------------------------------------
        List<Byte> result = new ArrayList<Byte>();
        result.add((byte)51);       //длина пакета

        result.add((byte)0xF0);     //ads1292 command
        result.add((byte)0x11);     //ads1292 stop continuous

        result.add((byte)0xF1);     //запись регистров ads1298
        result.add((byte)0x01);     //адрес первого регистра
        result.add((byte)0x17);     //количество регистров

        result.add((byte) getRegister_1Value(adsConfiguration));         //register 0x01   set SPS
        result.add((byte)testSignalEnabledBits(adsConfiguration));       //register 0x02   test signal
        result.add((byte)0xCC);                                          //register 0x03
        boolean isLoffEnabled = adsConfiguration.isLoffEnabled();
        result.add((byte)(isLoffEnabled? 0x13 : 0x00));                  //register 0x04
        for (int i = 0; i < 8; i++) {
            result.add((byte) getChanelRegisterValue(i));//registers 0x05 - 0x0C
        }
         int rlsSensBits = getRLDSensBits();
        result.add((byte)rlsSensBits);  //RLD sens positive              register 0x0D
        result.add((byte)rlsSensBits);  //RLD sens negative              register 0x0E

        int loffSensBits = getLoffSensRegisterValue();
        result.add((byte)loffSensBits); //loff sens positive             //register 0x0F
        result.add((byte)loffSensBits); //loff sens negative             //register 0x10
        result.add((byte)0x00);                                          //register 0x11
        result.add((byte)0x00);                                          //register 0x12
        result.add((byte)0x00);                                          //register 0x13
        result.add((byte)0x0F);                                          //register 0x14
        result.add((byte)0x00);                                          //register 0x15
        result.add((byte)0x20);                                          //register 0x16
        result.add((byte)(isLoffEnabled? 0x02 : 0x00));                  //register 0x17


        result.add((byte)0xF2);     //делители частоты для 8 каналов ads1298  возможные значения 0,1,2,5,10;
        for (int i = 0; i < NUMBER_OF_ADS_CHANNELS; i++) {
            int divider = adsConfiguration.isChannelEnabled(i) ? adsConfiguration.getChannelDivider(i).getValue() : 0;
            result.add((byte)divider);
        }

        result.add((byte)0xF3);     //accelerometer mode: 0 - disabled, 1 - enabled
        int accelerometerMode = adsConfiguration.isAccelerometerEnabled() ? 1 : 0;
        result.add((byte)accelerometerMode);

        result.add((byte)0xF4);     //send battery voltage data: 0 - disabled, 1 - enabled
        int batteryMeasure = adsConfiguration.isBatteryVoltageMeasureEnabled() ? 1 : 0;
        result.add((byte)batteryMeasure);

        result.add((byte)0xF5);     //передача данных loff статуса: 0 - disabled, 1 - enabled
        result.add((byte)(isLoffEnabled ? 1 : 0));

        result.add((byte)0xF6);     //reset timeout. In seconds
        result.add((byte)20);

        result.add((byte)0xF0);     //ads1292 command
        result.add((byte)0x10);     //ads1292 start continuous

        result.add((byte)0xFE);     //start recording

        result.add((byte)0x55);     //footer1
        result.add((byte)0x55);     //footer1
        for (int i = 0; i < result.size(); i++) {
             System.out.printf("i=%d; val=%x \n",i, result.get(i));
        }
        return result;
    }

    private int getRegister_1Value(AdsConfiguration adsConfiguration) {
        int registerValue = 0;
        //if (adsConfiguration.isHighResolutionMode()) {
            switch (adsConfiguration.getSps()) {
                /*case S250:
                    registerValue = 0x06;//switch to low power mode
                    break;*/
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
        return registerValue;
    }
    //--------------------------------

    private int getChanelRegisterValue(int channelNumber) {
        if (adsConfiguration.isChannelEnabled(channelNumber)) {
            return adsConfiguration.getChannelGain(channelNumber).getRegisterBits() + adsConfiguration.getChannelCommutatorState(channelNumber).getRegisterBits();
        }
        return 0x81;   //channel disabled
    }

    private int testSignalEnabledBits(AdsConfiguration configuration) {
        int result = 0x00;
        for (int i = 0; i < NUMBER_OF_ADS_CHANNELS; i++) {
           if (adsConfiguration.isChannelEnabled(i) && adsConfiguration.getChannelCommutatorState(i).equals(CommutatorState.TEST_SIGNAL)) {
                result = 0x10;
            }
        }
        return result;
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
