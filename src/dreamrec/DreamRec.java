package dreamrec;

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
        Controller controller = new Controller(model);
        MainViewNew mainWindow = new MainViewNew(model, controller);
        controller.setMainWindow(mainWindow);
    }
}
