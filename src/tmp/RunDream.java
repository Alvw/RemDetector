package tmp;


import device.BdfDataSourceActive;
import dreamrec.*;

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
        BdfDataSourceActive device = appProperties.getDeviceImplementation();
        Controller controller = new Controller(model, device);
      MainViewNew mainWindow = new MainViewNew(model, controller);
      controller.setMainWindow(mainWindow);
    }
}
