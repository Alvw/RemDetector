package com.github.dreamrec;

import com.dream.ApparatModel;
import com.dream.MainView;
import com.dream.MainViewNew;

/**
 * Created by IntelliJ IDEA.
 * User: galafit
 * Date: 03/10/14
 * Time: 10:43
 * To change this template use File | Settings | File Templates.
 */
public class DreamRecNew {
    public static void main(String[] args) {
        ApparatModel model = new ApparatModel();
        ControllerNew controller = new ControllerNew(model);
        MainViewNew mainWindow = new MainViewNew(model, controller);
        controller.setMainWindow(mainWindow);
    }
}
