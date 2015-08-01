package comport;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Terminal {
    public static void main(String[] args) {
        final TerminalInt t = new TerminalInt();
        JFrame frame = new JFrame("ComPort");
        JButton disconnectButton = new JButton("Disconnect com port");
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                t.disconnect();
            }
        });
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                t.disconnect();
            }
        });
        frame.add(disconnectButton);
        frame.pack();
        frame.setVisible(true);
    }
}
