package tmp;


import device.Device;
import dreamrec.ApplicationProperties;
import dreamrec.Controller;
import dreamrec.ApparatModel;
import dreamrec.MainView;

/**
 * Created with IntelliJ IDEA.
 * User: GENA
 * Date: 07.05.14
 * Time: 21:36
 * To change this template use File | Settings | File Templates.
 */
public class RunDream {
    public static void main(String[] args) {
      ApparatModel model = new ApparatModel();
       ApplicationProperties appProperties = new ApplicationProperties();
        Device device = appProperties.getDeviceImplementation();
        Controller controller = new Controller(model, device);
      MainView mainWindow = new MainView(model, controller);
     // controller.setView(mainWindow);
    }
}
