package dreamrec;

import device.DataSource;
import gui.SettingsWindow;

/**
 * Created by IntelliJ IDEA.
 * User: galafit
 * Date: 03/10/14
 * Time: 10:43
 * To change this template use File | Settings | File Templates.
 */
public class DreamRec {
    public static void main(String[] args) {
        ApplicationProperties appProperties = new ApplicationProperties();
        DataSource device = appProperties.getDeviceImplementation();
        Controller controller = new Controller();
       // SettingsWindow settingsWindow = new SettingsWindow();
    }
}
