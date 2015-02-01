package dreamrec;

import gui.GuiConfig;
import gui.MainWindow;
import properties.ApplicationProperties;
import properties.GuiProperties;
import properties.RemProperties;

import javax.swing.*;

/**
 *
 */
public class DreamRec {
    public static void main(String[] args) {
        try {
            String lookAndFeelClassName = UIManager.getCrossPlatformLookAndFeelClassName();
            // устанавливаем LookAndFeel
            UIManager.setLookAndFeel(lookAndFeelClassName);
        } catch (UnsupportedLookAndFeelException e) {
            System.out.println("Can't use the specified look and feel on this platform.");
        } catch (Exception e) {
            System.out.println("Couldn't get specified look and feel, for some reason.");
        }

        try {
            ApplicationProperties applicationProperties = new ApplicationProperties("application.properties");
            RemProperties remProperties = new RemProperties("rem.properties");
            GuiConfig guiConfig = new GuiProperties("gui.properties");
            String deviceClass = applicationProperties.getDeviceClassName();
            DeviceFabric deviceFabric = new DeviceFabric(deviceClass);
            String[] deviceSignalsLabels = applicationProperties.getDeviceChannelsLabels();
            boolean isFrequencyAutoAdjustment = applicationProperties.isFrequencyAutoAdjustment();
            RemConfigurator remConfigurator = new RemConfigurator(remProperties.getEogRemFrequency(),
                    remProperties.getAccelerometerRemFrequency(), remProperties.getEogRemCutoffPeriod());
            Controller controller = new Controller(deviceFabric);
            controller.setDeviceSignalsLabels(deviceSignalsLabels);
            controller.setRemConfigurator(remConfigurator);
            controller.setFrequencyAutoAdjustment(isFrequencyAutoAdjustment);
            controller.setRemMode(true);

            MainWindow mainWindow = new MainWindow(controller, guiConfig);

        } catch (ApplicationException e) {
            showMessage(e.getMessage());
        }

    }

    public static void showMessage(String s) {
        JOptionPane.showMessageDialog(null, s);
    }
}
