package com.crostec.ads;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class FrameDecoder {

    private DeviceBle deviceBle;
    private int rx_cntr;
    private int frameSize;
    private int[] frame = new int[42];
    private int connectionHandle;
    private static final Log log = LogFactory.getLog(FrameDecoder.class);

    protected FrameDecoder(DeviceBle deviceBle) {
        this.deviceBle = deviceBle;
    }

    public void onByteReceived(int inByte) {
        // System.out.print(inByte + " ");
        // System.out.print(inByte + "(" + rx_cntr + ") ");
        frame[rx_cntr++] = inByte;
        switch (rx_cntr) {
            case 0x01:
                if ((frame[0] != 0x80) && (frame[0] != 0x00)) {
                    rx_cntr = 0;
                    System.out.println("sync err");
                }
                break;
            case 0x02:
                frameSize = frame[1] + 4;
                if (frameSize > 43) {
                    rx_cntr = 0;
                    System.out.println("big length");
                }
                break;
            case 0x03:
                if (frame[2] > 7) {
                    rx_cntr = 0;
                    System.out.println("Wrong command class ID");
                }
                break;
            default:
                if (rx_cntr > (frameSize - 1)) {
                    rx_cntr = 0;
                    System.out.println("frame end");
                    parseBG_Data(frame);
                }
                break;
        }
    }

    private void parseBG_Data(int[] frame) {
        switch (frame[0]) {
            case 0x80:
                on_RF_Event_Received();
                break;
            case 0x00:
                on_RF_Responce_Received();
                break;
            default:
                break;
        }
    }

    private void on_RF_Responce_Received() {
        switch (frame[2]) {
            case 0x00:
                //on_RF_System_Responce_Received();
                break;
            case 0x02:
                //on_RF_Attribute_Database_Responce_Received();
                break;
            case 0x03:
                //on_RF_Connection_Responce_Received();
                break;
            case 0x04:
                // on_RF_Attribute_Client_Responce_Received();
                break;
            case 0x06:
                on_RF_GAP_Responce_Received();
                break;
            case 0x07:
                //on_RF_Hardware_Responce_Received();
                break;
            default:
                //on_RF_Unexpected_Responce_Received();
                break;
        }
    }

    private void on_RF_GAP_Responce_Received() {
        switch (frame[3]) {
            case 0x07:
                on_RF_GAP_Set_Scan_Parameters_Responce_Received();
                break;
        }
    }

    private void on_RF_GAP_Set_Scan_Parameters_Responce_Received() {
        deviceBle.writeToPort(BlueGigaManager.ble_cmd_gap_discover());
    }


    private void on_RF_Event_Received() {
        switch (frame[2]) {
            case 0x06:
                on_RF_GAP_Event_Received();
                break;
            case 0x03:
                onRF_ConnectionEventReceived();
                break;
            case 0x04:
                onRF_AttributeClientEventReceived();
                break;
        }
    }

    private void onRF_AttributeClientEventReceived() {
        switch (frame[3]) {
            case 0x02:
                ble_evt_attclient_group_found();
                break;
            case 0x05:
                ble_evt_attclient_attribute_value();
                break;
            default:
                // on_RF_Unexpected_Event_Received();
                break;
        }
    }

    private void ble_evt_attclient_attribute_value() {
        int decodedFrame[] = new int[10];
        //9 byte offset
        //2 байта счетчик todo
        int counter = ((((frame[10]) << 24) + (frame[9] << 16)) / 65536);
        //System.out.println("counter = " + counter);
        //3 байта - первое значение ADS1292
        decodedFrame[0] = (((frame[13] << 24) + ((frame[12]) << 16) + (frame[11] << 8)) / 256);
        //4 * 2 байта последующие 4 значения для ADS1292
        for (int i = 0; i < 4; i++) {
            decodedFrame[i + 1] = (((frame[i * 2 + 15]) << 24) + (frame[i * 2 + 14] << 16))/65536 ;
        }
        // Восстанавливаем полное значение по приращению
        for (int i = 0; i < 4; i++) {
            decodedFrame[i + 1] =  decodedFrame[i + 1] + decodedFrame[i];
        }
        //акселерометр
        decodedFrame[5] = ((frame[24] & 0x0F) << 8) + frame[23];
        decodedFrame[6] = ((frame[27] & 0xF0) << 4) + frame[25];
        decodedFrame[7] = ((frame[27] & 0x0F) << 8) + frame[26];

        decodedFrame[8] = ((frame[24] & 0xF0) << 4) + frame[22];
        decodedFrame[9] = 0;


        notifyListeners(decodedFrame);
        System.out.println("Data Received: ");
        for (int i = 0; i < decodedFrame.length; i++) {
                System.out.print(decodedFrame[i] + " ");
        }
    }

    private void ble_evt_attclient_group_found() {
        if (frame[9] == 0x09 && frame[10] == 0x18) {

        }
    }

    private void onRF_ConnectionEventReceived() {
        switch (frame[3]) {
            case 0x00:
                ble_msg_connection_status_evt_t();
                break;
            case 0x04:
                ble_msg_connection_disconnected_evt();
                break;
            default:
                // on_RF_Unexpected_Event_Received();
                break;
        }
    }

    private void ble_msg_connection_disconnected_evt() {
        try {
            Thread.sleep(1000);
            deviceBle.writeToPort(BlueGigaManager.setScanParameters());
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    private void ble_msg_connection_status_evt_t() {
        connectionHandle = frame[4];
        deviceBle.writeToPort(BlueGigaManager.ble_cmd_attclient_attribute_write());
    }


    private void on_RF_GAP_Event_Received() {
        switch (frame[3]) {
            case 0x00:
                on_RF_GAP_Scan_Responce_Event_Received();
                break;
            default:
                // on_RF_Unexpected_Event_Received();
                break;
        }
    }

    private void on_RF_GAP_Scan_Responce_Event_Received() {
        byte[] messageBytes = new byte[28];
        for (int i = 0; i < 28; i++) {
            messageBytes[i] = (byte) frame[i + 14];
        }
        String message = new String(messageBytes);
        System.out.println(message);
        List<Byte> btAddress = new ArrayList<Byte>();
        if (message.contains("DKBLE112 thermometer")) {
            for (int i = 0; i < 6; i++) {
                btAddress.add((byte) frame[i + 6]);
            }
            deviceBle.writeToPort(BlueGigaManager.gap_connect_direct(btAddress));
        }
    }


    public abstract void notifyListeners(int[] decodedFrame);
}
