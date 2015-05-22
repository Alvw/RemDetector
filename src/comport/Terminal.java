package comport;

import dreamrec.ApplicationException;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class Terminal {
    int bufferSize = 100;
    byte[]  symbols = new byte[bufferSize];
    int counter = 0;
    ComPort  comPort;


    public Terminal() {
        String[] portNames = SerialPortList.getPortNames();
        for (int i = 0; i < portNames.length; i++) {
            System.out.println(portNames[i]);
        }
        try {
            comPort = new ComPort("COM14", 9600);
            comPort.setComPortListener(new ComPortListener() {
                @Override
                public void onByteReceived(byte inByte) {
                   if(counter < bufferSize) {
                       symbols[counter] = inByte;
                   }
                   if(counter == bufferSize){
                       for(int i = 0; i < bufferSize; i++) {
                           //char c =  (char) (symbols[i] & 0xFF);
                           System.out.print(symbols[i]);
                           comPort.disconnect();
                       }
                   }
                    counter++;
                }
            });
        }
        catch(ApplicationException e) {
            System.out.println(e.getMessage());
        }
        catch (SerialPortException e) {
            System.out.println(e.getMessage());
        }
    }
}
