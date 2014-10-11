package tmp;


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
      Controller controller = new Controller(model);
      MainView mainWindow = new MainView(model, controller);
     // controller.setView(mainWindow);
    }
}
