package dreamrec;

import bdf.BdfProvider;
import bdf.BdfReader;
import bdf.RecordingBdfConfig;
import gui.GuiConfig;
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
    private RecordingBdfConfig recordingBdfConfig;
    private int eogRemFrequency;
    private int accelerometerRemFrequency;
    private GuiConfig guiConfig;

    public Controller(int eogRemFrequency, int accelerometerRemFrequency) {
        this.eogRemFrequency = eogRemFrequency;
        this.accelerometerRemFrequency = accelerometerRemFrequency;
        this.guiConfig = guiConfig;
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

    public void setBdfProvider(File file) {
        try {
            BdfReader bdfReader = new BdfReader(file);
            recordingBdfConfig = bdfReader.getBdfConfig();
            bdfProvider = bdfReader;
            RecordingSettings recordingSettings = new RecordingSettings(recordingBdfConfig);
            boolean[] isChannelsActive = RemConfig.isRemLabels(recordingBdfConfig.getSignalsLabels());
            recordingSettings.setActiveChannels(isChannelsActive);
            recordingSettings.setFile(file);
            mainWindow.openRecordingSettingsPreview(recordingSettings);
        } catch (ApplicationException e) {
            mainWindow.showMessage("Bdf file reading is failed!");
        }
    }


    public void startDataReading(RecordingSettings recordingSettings) throws ApplicationException{
        if(! isRecording) {
            RemConfig remConfig = new RemConfig(recordingSettings.getChannelsLabels());
            RemAdapter remAdapter = new RemAdapter(recordingBdfConfig,remConfig, eogRemFrequency, accelerometerRemFrequency);
            remAdapter.setActiveChannels(recordingSettings.getActiveChannels());
            DataStore dataStore = new DataStore(bdfProvider, remAdapter.getDividers());
            mainWindow.setDataStore(dataStore);
            bdfProvider.startReading();
            isRecording = true;
            System.out.println("file to save: "+recordingSettings.getFile().getAbsolutePath());
        }
    }

    public String getCurrentDirToRead() {
         return guiConfig.getDirectoryToRead();
    }

    public String getCurrentDirToSave() {
        return guiConfig.getDirectoryToSave();
    }

    public void setCurrentDirToRead(String dir) {
        guiConfig.setDirectoryToRead(dir);

    }

    public void setCurrentDirToSave(String dir) {
        guiConfig.setDirectoryToSave(dir);
    }
}
