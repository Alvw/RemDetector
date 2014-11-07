package dreamrec;

import bdf.BdfConfig;
import bdf.BdfReader;
import bdf.BdfWriter;
import bdf.BdfSource;
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
    private BdfSource device;
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
        if (bdfWriter != null) {
           // device.removeDataListener(bdfWriter);
        }
       // BdfConfig bdfConfig = device.getBdfConfig();
     //   bdfWriter = new BdfWriter(bdfConfig);
        device.addBdfDataListener(bdfWriter);
        model.clear();
        model.setFrequency(DRM_FREQUENCY);
        model.setStartTime(System.currentTimeMillis());  //todo remove
        // mainWindow.setStart(model.getStartTime(), 1000 / model.getFrequency());
       // incomingDataBuffer = new IncomingDataBuffer();
       // device.addBdfDataListener(incomingDataBuffer);
        try {
            device.startReading();
        } catch (ApplicationException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            System.exit(0);
        }
    }

    public void stopRecording() {
        if (!isRecording) return;
        isRecording = false;
        try {
            device.stopReading();
        } catch (ApplicationException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        saveToFile();
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


    public void readFromFile() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("./"));
            fileChooser.setFileFilter(new ExtFileFilter("bdf", "*.bdf"));
            int fileChooserState = fileChooser.showOpenDialog(mainWindow);
            if (fileChooserState == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                BdfSource bdfSource = new BdfReader(selectedFile);
                DataStore dataStore = new DataStore(bdfSource);
                mainWindow.setDataStore(dataStore);
                long startTime = System.currentTimeMillis();
                bdfSource.startReading();
                long endTime = System.currentTimeMillis();
                System.out.println("reading finished "+(endTime-startTime));

            }
        } catch (ApplicationException e) {
            mainWindow.showMessage("Bdf file reading is failed!");
        }
    }
}
