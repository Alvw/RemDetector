package com.crostec.ads;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

class ComPort {

    private static Log log = LogFactory.getLog(ComPort.class);
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean isConnected;
    CommPort commPort;
    SerialReader serialReader;
    Thread serialReaderThread;
    SerialWriter serialWriter;
    Thread serialWriterThread;

    public void connect(DeviceConfig deviceConfig) throws Exception {
        if (isConnected) {
            return;
        }
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(deviceConfig.getComPortName());
        if (portIdentifier.isCurrentlyOwned()) {
            log.error("Error: Port is currently in use");
        } else {
            commPort = portIdentifier.open(this.getClass().getName(), 2000);
            if (commPort instanceof SerialPort) {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(deviceConfig.getComPortSpeed(), SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                inputStream = serialPort.getInputStream();
                outputStream = serialPort.getOutputStream();
                isConnected = true;
                serialReader = new SerialReader(inputStream);
                serialReaderThread = new Thread(serialReader);
                serialReaderThread.start();
                serialWriter = new SerialWriter(outputStream);
                serialWriterThread = new Thread(serialWriter);
                serialWriterThread.start();
            } else {
                log.error("Error: Not a serial ports.");
            }
        }
    }


    public void disconnect() {
        if (!isConnected)
            return;
        try {
            isConnected = false;
            serialReader.disconnect();
            serialWriter.disconnect();
            inputStream.close();
            outputStream.close();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    commPort.close();
                }
            }).start();
        } catch (IOException e) {
            log.error(e);
        }
    }

    public void writeToPort(List<Byte> bytes) {
        if(isConnected){
            serialWriter.write(bytes);
        } else {
            log.warn("Com port disconnected. Can't write to port.");
        }
    }

    public void setFrameDecoder(FrameDecoder frameDecoder) {
        serialReader.setFrameDecoder(frameDecoder);
    }

    public static class SerialReader implements Runnable {
        private boolean isConnected = true;
        private InputStream in;
        private FrameDecoder frameDecoder;

        public SerialReader(InputStream in) {
            this.in = in;
        }

        public void disconnect() {
            isConnected = false;
        }


        public void setFrameDecoder(FrameDecoder frameDecoder) {
            this.frameDecoder = frameDecoder;
        }

        public void run() {
            int len = -1;
            byte[] buf = new byte[8];
            try {
                while (isConnected) {
                    len = this.in.read(buf);
                    while (isConnected && (len = this.in.read(buf)) > -1) {
                        for (int i = 0; i < len; i++) {
                            if (frameDecoder != null) {
                                frameDecoder.onByteReceived((buf[i] & 0xFF));
                            }
                        }
                    }
                    Thread.sleep(100);
                }
            } catch (IOException e) {
                log.error(e);
            } catch (InterruptedException e){
                log.error(e);
            }
        }
    }


    public static class SerialWriter implements Runnable {
        private OutputStream out;
        List<Byte> data = new ArrayList<Byte>();
        boolean isConnected = true;
        boolean isDataReady = false;

        public SerialWriter(OutputStream out) {
            this.out = out;
        }

        public void write(List<Byte> dataList) {
            synchronized (data) {
                data.clear();
                data.addAll(dataList);
                isDataReady = true;
                data.notifyAll();
            }
        }

        public void run() {
            synchronized (data) {
                while (isConnected) {
                    while (!isDataReady) {
                        try {
                            data.wait(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    byte[] outData = new byte[data.size()];
                    for (int i = 0; i < outData.length; i++) {
                        outData[i] = data.get(i);
                    }
                    try {
                        if (isConnected) {
                            out.write(outData);
                            isDataReady = false;
                        }
                    } catch (IOException e) {
                        log.error(e);
                    }
                }
            }
        }

        public void disconnect() {
            isConnected = false;
            isDataReady = true;
           /* synchronized (data) {
                data.notifyAll();    //todo lock
            }*/
        }
    }
}
