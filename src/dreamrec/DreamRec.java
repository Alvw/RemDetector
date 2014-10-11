package dreamrec;

import device.Device;

/**
 * Created by IntelliJ IDEA.
 * User: galafit
 * Date: 03/10/14
 * Time: 10:43
 * To change this template use File | Settings | File Templates.
 */
public class DreamRec {
    public static void main(String[] args) {
        ApparatModel model = new ApparatModel();
        ApplicationProperties appProperties = new ApplicationProperties();
        Device device = appProperties.getDeviceImplementation();
        Controller controller = new Controller(model, device);
        MainViewNew mainWindow = new MainViewNew(model, controller);
        controller.setMainWindow(mainWindow);
    }
}
