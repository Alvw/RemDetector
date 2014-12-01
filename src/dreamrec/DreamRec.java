package dreamrec;

import gui.GuiConfig;
import gui.MainView;

import java.util.concurrent.LinkedBlockingQueue;

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
            ApplicationFactory applicationFactory = new ApplicationFactory(applicationConfig);
            Controller controller = new Controller(applicationConfig.getEogRemFrequency(), applicationConfig.getAccelerometerRemFrequency());
            MainView mainView= new MainView(controller);
            controller.setView(mainView);

        } catch (ApplicationException e) {

        }

    }
}
