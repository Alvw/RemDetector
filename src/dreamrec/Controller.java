package dreamrec;

import bdf.BdfProvider;
import bdf.BdfReader;
import bdf.BdfWriter;
import gui.MainView;
import gui.SettingsWindow;
import gui.View;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Controller {
    public static final int DRM_FREQUENCY = 50;
    public static final int ACC_FREQUENCY = 10;
    private View mainWindow;

    private static final Log log = LogFactory.getLog(Controller.class);

    private boolean isRecording = false;
    private BdfProvider bdfProvider;
    private BdfWriter bdfWriter;




    public Controller() {
        mainWindow = new MainView(this);
    }

    public void setView(View view) {
       mainWindow = view;
    }

    public void stopRecording() {
        if(isRecording) {
            try {
                bdfProvider.stopReading();
                isRecording = false;
            } catch (ApplicationException e) {
                mainWindow.showMessage(e.getMessage());
            }
        }

        if (!isRecording) return;
        isRecording = false;
    }

    private void saveToFile(String filename, String dir) {
        if(filename == null) {
            filename = new SimpleDateFormat("dd-MM-yyyy_HH-mm").format(new Date(System.currentTimeMillis())) + ".drm";
        }
    }

    public void closeApplication() {
        stopRecording();
        System.exit(0);
    }

    public void openPreview(File file) {
        try {
            bdfProvider = new BdfReader(file);
            new SettingsWindow(mainWindow, bdfProvider.getBdfConfig(), this);
        } catch (ApplicationException e) {
            mainWindow.showMessage("Bdf file reading is failed!");
        }
    }

    public void openPreview() {
        try {
            bdfProvider = ApplicationFactory.getDeviceImplementation();
            new SettingsWindow(mainWindow, bdfProvider.getBdfConfig(), this);
        } catch (ApplicationException e) {
            mainWindow.showMessage(e.getMessage());
        }
    }

    public void startRecording(int[] dividers) {
        if(! isRecording) {
            try {
                DataStore dataStore = new DataStore(bdfProvider, dividers);
                mainWindow.setDataStore(dataStore);
                bdfProvider.startReading();
                isRecording = true;
            } catch (ApplicationException e) {
                mainWindow.showMessage(e.getMessage());
                System.exit(0);
            }
        }
    }
}
