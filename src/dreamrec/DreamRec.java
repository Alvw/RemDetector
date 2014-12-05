package dreamrec;

import gui.View;

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
            DeviceFabric deviceFabric = new DeviceFabric(applicationConfig);
            Controller controller = new Controller(applicationConfig, deviceFabric.getDeviceImplementation());
            View mainWindow = new View(controller);
            controller.setView(mainWindow);

        } catch (ApplicationException e) {

        }

    }
}
