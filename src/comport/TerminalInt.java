package comport;

import dreamrec.ApplicationException;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class TerminalInt {
   byte hb;
    byte lb;
    int counter = 0;
    ComPort  comPort;

    public TerminalInt() {
        String[] portNames = SerialPortList.getPortNames();
        for (int i = 0; i < portNames.length; i++) {
            System.out.println(portNames[i]);
        }
        try {
            comPort = new ComPort("COM8", 256000);
            comPort.setComPortListener(new ComPortListener() {
                @Override
                public void onByteReceived(byte inByte) {

                     if (counter%2 == 0) {
                         hb = inByte;
                     }
                    else {
                         lb = inByte;
                         int result = (hb & 0xFF) << 8 | (lb & 0xFF);
                         System.out.println(counter/2+ "   "+result);
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

    public void disconnect() {
        comPort.disconnect();
    }
}
