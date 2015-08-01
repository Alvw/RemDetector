package comport;

import dreamrec.ApplicationException;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class TerminalChar {
    int counter = 0;
    ComPort  comPort;


    public TerminalChar() {
        String[] portNames = SerialPortList.getPortNames();
        for (int i = 0; i < portNames.length; i++) {
            System.out.println(portNames[i]);
        }
        try {
            comPort = new ComPort("COM8",256000);
            comPort.setComPortListener(new ComPortListener() {
                @Override
                public void onByteReceived(byte inByte) {
                    char c =  (char) (inByte & 0xFF);
                    System.out.println(counter + "   " + c);
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
