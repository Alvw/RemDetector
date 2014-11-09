package dreamrec;

import bdf.BdfProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by IntelliJ IDEA.
 * User: galafit
 * Date: 03/10/14
 * Time: 10:43
 * To change this template use File | Settings | File Templates.
 */
public class DreamRec {
    public static void main(String[] args) {
        final LinkedBlockingQueue<Integer> buffer = new LinkedBlockingQueue<Integer>(4);
        ApplicationProperties appProperties = new ApplicationProperties();
        BdfProvider device = appProperties.getDeviceImplementation();
        Controller controller = new Controller();
       // SettingsWindow settingsWindow = new SettingsWindow();
      /*  Timer timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                System.out.println("gui thread: ");
                buffer.poll();
            }
        });
        timer.start();
        int i = 0;
        while(true) {
            try{
                Thread.sleep(500);
                buffer.put(1);
                i++;
                System.out.println("main thread "+i);
            } catch(InterruptedException e) {
                System.out.println(e);
            }
        }*/
    }
}
