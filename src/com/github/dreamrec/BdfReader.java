package com.github.dreamrec;

import device.BdfDataListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BdfReader {

    public List<BdfDataListener> bdfDataListeners = new ArrayList<BdfDataListener>();
    Log log = LogFactory.getLog(BdfReader.class);

    public void addDataListener(BdfDataListener bdfDataListener) {
        bdfDataListeners.add(bdfDataListener);
    }

    public void read(File file) {

        try {
            DataInputStream inputStream = new DataInputStream(new FileInputStream(file));
            int numberOfBytesInHeaderRecord = 256 * (1 + 5);
            for (int i = 0; i < numberOfBytesInHeaderRecord; i++) {
                inputStream.readByte();
            }
            int[] frame = new int[106];

        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    private int read24BitLittleEndian(DataInputStream inputStream) {
        byte[] value = new byte[3];
        for (int i = 0; i < value.length; i++) {
            byte b = value[i];
            try {
                value[i] = inputStream.readByte();
            } catch (IOException e) {
                log.error(e);
                throw new RuntimeException(e);
            }
        }
        int sizeOfInt = 4;
        ByteBuffer byteBuffer = ByteBuffer.allocate(sizeOfInt);
        byteBuffer.put(value[2]);
        byteBuffer.put(value[1]);
        byteBuffer.put(value[0]);
        return byteBuffer.getInt();
    }
}
