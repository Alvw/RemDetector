package dreamrec;

import bdf.BdfProvider;
import gui.SettingsWindow;

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
        //SettingsWindow settingsWindow = new SettingsWindow();
    }
}
