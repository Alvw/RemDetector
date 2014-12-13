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
 *
 */
public class DreamRec {
    public static void main(String[] args) {
        try {
            File configDirectory = new File(System.getProperty("user.dir"), "config");
            ApplicationProperties applicationProperties = new ApplicationProperties(new File(configDirectory, "application.properties"));
            RemConfig remConfig = new RemProperties(new File(configDirectory, "rem.properties"));
            GuiConfig guiConfig = new GuiProperties(new File(configDirectory, "gui.properties"));
            String deviceClass = applicationProperties.getDeviceClassName();
            DeviceFabric deviceFabric = new DeviceFabric(deviceClass);
            BdfDevice bdfDevice = deviceFabric.getDeviceImplementation();
            DeviceBdfConfig bdfConfig = bdfDevice.getBdfConfig();
            bdfConfig.setSignalsLabels(applicationProperties.getDeviceChannelsLabels(bdfConfig.getNumberOfSignals()));
            boolean isFrequencyAutoAdjustment = applicationProperties.isFrequencyAutoAdjustment();
            RemUtils remConfigurator = new RemUtils(remConfig);
            Controller controller = new Controller(bdfDevice, remConfigurator, isFrequencyAutoAdjustment);

            MainWindow mainWindow = new MainWindow(controller, guiConfig);

        } catch (ApplicationException e) {
            showMessage(e.getMessage());
        }

    }

    public static void showMessage(String s) {
        JOptionPane.showMessageDialog(null, s);
    }
}
