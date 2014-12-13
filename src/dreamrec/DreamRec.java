package dreamrec;

import bdf.DeviceBdfConfig;
import gui.GuiConfig;
import gui.MainWindow;
import properties.ApplicationProperties;
import properties.GuiProperties;
import properties.RemProperties;

import javax.swing.*;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: galafit
 * Date: 03/10/14
 * Time: 10:43
 * To change this template use File | Settings | File Templates.
 */
public class DreamRec {
    public static void main(String[] args) {
        try {
            File configDirectory = new File(System.getProperty("user.dir"), "config");
            ApplicationProperties applicationProperties = new ApplicationProperties(new File(configDirectory, "application.properties"));
            RemConfig remConfig = new RemProperties(new File(configDirectory, "rem.properties"));

            String deviceClass = applicationProperties.getDeviceClassName();
            DeviceFabric deviceFabric = new DeviceFabric(deviceClass);
            BdfDevice bdfDevice = deviceFabric.getDeviceImplementation();
            DeviceBdfConfig bdfConfig = bdfDevice.getBdfConfig();
            bdfConfig.setSignalsLabels(applicationProperties.getDeviceChannelsLabels(bdfConfig.getNumberOfSignals()));
            boolean isFrequencyAutoAdjustment = applicationProperties.isFrequencyAutoAdjustment();
            RemConfigurator remConfigurator = new RemConfigurator(remConfig);
            Controller controller = new Controller(bdfDevice, remConfigurator, isFrequencyAutoAdjustment);
            GuiConfig guiConfig = new GuiProperties();
            MainWindow mainWindow = new MainWindow(controller, guiConfig);

        } catch (ApplicationException e) {
            showMessage(e.getMessage());
        }

    }

    public static void showMessage(String s) {
        JOptionPane.showMessageDialog(null, s);
    }
}
