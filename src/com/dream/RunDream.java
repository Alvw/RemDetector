package com.dream;


import com.github.dreamrec.ControllerNew;

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
      ControllerNew controller = new ControllerNew(model);
      MainView mainWindow = new MainView(model, controller);
     // controller.setView(mainWindow);
    }
}
