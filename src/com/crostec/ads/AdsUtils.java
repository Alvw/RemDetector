package com.crostec.ads;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class AdsUtils {


    /**
     * convert int data format to 24 bit (3 bytes) data format valid for Bdf and
     * change Big_endian (java)  to Little_endian (for bdf)
     */
    public static byte[] to24BitLittleEndian(int value) {
        int sizeOfInt = 4;
        ByteBuffer byteBuffer = ByteBuffer.allocate(sizeOfInt).putInt(value);
        byte[] result = new byte[3];
        result[0] = byteBuffer.get(sizeOfInt - 1);
        result[1] = byteBuffer.get(sizeOfInt - 2);
        result[2] = byteBuffer.get(sizeOfInt - 3);
        return result;
    }

     /**
     * if the String.length() is more then the given length we cut the String
     * if the String.length() is less then the given length we append spaces to the end of the String
     */
    public static String adjustLength(String text, int length) {
        StringBuilder sB = new StringBuilder(text);
        if (text.length() > length) {
            sB.delete((length + 1), text.length());
        } else {
            for (int i = text.length(); i < length; i++) {
                sB.append(" ");
            }
        }
        return sB.toString();
    }
}
