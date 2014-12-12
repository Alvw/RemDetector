package dreamrec;

import gui.GuiConfig;
import gui.MainWindow;

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
            ApplicationConfig applicationConfig = new ApplicationProperties();
            GuiConfig guiConfig = new GuiProperties();
            DeviceFabric deviceFabric = new DeviceFabric(applicationConfig);
            RemConfigurator remConfigurator = new RemConfigurator(applicationConfig.getEogRemFrequency(),
                    applicationConfig.getAccelerometerRemFrequency(),
                    applicationConfig.getEogRemCutoffPeriod());
            Controller controller = new Controller(deviceFabric.getDeviceImplementation(),
                    remConfigurator, applicationConfig.isFrequencyAutoAdjustment());
            MainWindow mainWindow = new MainWindow(controller, guiConfig);

        } catch (ApplicationException e) {

        }

    }
}
