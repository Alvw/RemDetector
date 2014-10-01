package com.crostec.ads;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class BlueGigaManager{

    public static List<Byte> sendHello(){
        int[] cmd = new int[]{0x00,0x00,0x00,0x01};
        return convertToByteList(cmd);
    }

    public static List<Byte> sysInfo(){
        int[] cmd = new int[]{0x00,0x00,0x00,0x08};
        return convertToByteList(cmd);
    }

    public static List<Byte> setScanParameters(){
        int[] cmd = new int[]{0x00,0x05,0x06,0x07,0xC8,0x00,0xC8,0x00,0x01};
        return convertToByteList(cmd);
    }

    public static List<Byte> ble_cmd_gap_discover(){
        int[] cmd = new int[]{0x00,0x01,0x06,0x02,0x01};
        return convertToByteList(cmd);
    }

    public static List<Byte> ble_cmd_attclient_read_by_group_type(int connectionHandle){
        List<Byte> result = convertToByteList(new int[]{0x00, 0x08,0x04, 0x01});
        result.add((byte)connectionHandle);
        result.addAll(convertToByteList(new int[]{0x01,0x00,0xff,0xff,0x02,0x00,0x28}));
        return result;
    }

    public static List<Byte> ble_cmd_attclient_attribute_write(){
        return convertToByteList(new int[]{0x00,0x05,0x04,0x05,0x00,0x11,0x00,0x01,0x01});
    }

    public static List<Byte> gap_connect_direct(List<Byte> address){
        List<Byte> result = convertToByteList(new int[]{0x00, 0x0F,0x06, 0x03});
        result.addAll(address);
        result.addAll(convertToByteList(new int[]{0x00,0x3c,0x00,0x4c,0x00,0x64,0x00,0x00,0x00}));
        return result;
    }

    private static List<Byte> convertToByteList(int[] command){
        List<Byte> byteList = new ArrayList<Byte>();
        for (int i = 0; i < command.length; i++) {
            byteList.add((byte)command[i]);
        }
        return byteList;
    }
}
