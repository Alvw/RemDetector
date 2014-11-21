package dreamrec;

import bdf.BdfProvider;
import bdf.BdfReader;
import bdf.BdfWriter;
import gui.MainView;
import gui.SettingsWindow;
import gui.View;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import tmp.ApparatModel;

import javax.swing.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Controller {
    public static final int DRM_FREQUENCY = 50;
    public static final int ACC_FREQUENCY = 10;
    private View mainWindow;
    private ApparatModel model;

    private static final Log log = LogFactory.getLog(Controller.class);

    private boolean isRecording = false;
    private BdfProvider device;
    private BdfWriter bdfWriter;

    private int nrOfChannelSamples = 5; //number of channel samples in data frame
    private int nrOfAccelerometerSamples = 1;

    public Controller() {
        mainWindow = new MainView(this);
    }

    public void setMainWindow(MainView mainWindow) {
        this.mainWindow = mainWindow;
    }


    public void startRecording() {
        isRecording = true;
        System.out.println("controller startRecord " + SwingUtilities.isEventDispatchThread());
        while(isRecording) {
            try{
                Thread.sleep(500);
                System.out.println("device run ");
            } catch(InterruptedException e) {
                System.out.println(e);
            }
        }
     //   if (bdfWriter != null) {
           // device.removeDataListener(bdfWriter);
       // }
       // BdfConfig bdfConfig = device.getBdfConfig();
     //   bdfWriter = new BdfWriter(bdfConfig);
     //   device.addBdfDataListener(bdfWriter);
     //   model.clear();
    //    model.setFrequency(DRM_FREQUENCY);
      //  model.setStartTime(System.currentTimeMillis());  //todo remove
    //    // mainWindow.setStart(model.getStartTime(), 1000 / model.getFrequency());
       // incomingDataBuffer = new IncomingDataBuffer();
       // device.addBdfDataListener(incomingDataBuffer);
      /*  try {
            device.startReading();
        } catch (ApplicationException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            System.exit(0);
        }*/
    }

    public void stopRecording() {
        System.out.println("controller stopRecord " + SwingUtilities.isEventDispatchThread());
        isRecording = false;
    /*    if (!isRecording) return;
        isRecording = false;
        try {
            device.stopReading();
        } catch (ApplicationException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        saveToFile();*/
    }

    private void saveToFile() {
        String fileName = new SimpleDateFormat("dd-MM-yyyy_HH-mm").format(new Date(System.currentTimeMillis())) + ".drm";
   /*     try {
            new FileIOManager().saveToFile(new File(fileName), model);
        } catch (ApplicationException e) {
            String msg = "error saving to file " + fileName;
            log.error(msg, e);
            mainWindow.showMessage(msg);
        }    */
    }

    public void closeApplication() {
        stopRecording();
        System.exit(0);
    }

    public void readFromFile(File file) {
        try {
            BdfProvider bdfProvider = new BdfReader(file);
             new SettingsWindow(mainWindow, bdfProvider.getBdfConfig(), this);
             //DataStore dataStore = new DataStore(bdfProvider);
             //mainWindow.setDataStore(dataStore);
             //bdfProvider.startReading();
        } catch (ApplicationException e) {
            mainWindow.showMessage("Bdf file reading is failed!");
        }
    }
}
