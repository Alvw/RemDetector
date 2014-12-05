package dreamrec;

import bdf.BdfProvider;
import bdf.BdfReader;
import bdf.BdfRecordsJoiner;
import bdf.RecordingBdfConfig;
import gui.View;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Controller {
    private View mainWindow;

    private static final Log log = LogFactory.getLog(Controller.class);

    private boolean isRecording = false;
    private BdfDevice bdfDevice;
    private BdfProvider bdfProvider;
    private RecordingBdfConfig recordingBdfConfig;
    private int eogRemFrequency;
    private int accelerometerRemFrequency;
    private DataStore dataStore;
    private ApplicationConfig applicationConfig;
    private String currentDirToRead;
    private String currentDirToSave;
    private File fileToRead;
    private File fileToSave;

    public Controller(ApplicationConfig applicationConfig, BdfDevice bdfDevice) {
        this.applicationConfig = applicationConfig;
        this.bdfDevice = bdfDevice;
        eogRemFrequency = applicationConfig.getEogRemFrequency();
        accelerometerRemFrequency = applicationConfig.getAccelerometerRemFrequency();
        currentDirToRead = applicationConfig.getDirectoryToRead();
        currentDirToSave = applicationConfig.getDirectoryToSave();
    }

    public void setView(View view) {
        mainWindow = view;
        mainWindow.setCurrentDirToRead(currentDirToRead);
    }

    public void stopRecording() {
        if (isRecording) {
            try {
                bdfProvider.stopReading();
                bdfProvider.removeBdfDataListener(dataStore);
                isRecording = false;
            } catch (ApplicationException e) {
                mainWindow.showMessage(e.getMessage());
            }
        }

        if (!isRecording) return;
        isRecording = false;
    }

    private void saveToFile(String filename, String dir) {
        if (filename == null) {
            filename = new SimpleDateFormat("dd-MM-yyyy_HH-mm").format(new Date(System.currentTimeMillis())) + ".drm";
        }
    }

    public void closeApplication() {
        stopRecording();
        applicationConfig.setDirectoryToRead(currentDirToRead);
        applicationConfig.setDirectoryToSave(currentDirToSave);
        System.exit(0);
    }


    public void setFileBdfProvider(File file) {
        if(isRecording) {
            stopRecording();
        }
        try {
            if (file != null && file.exists() && file.isFile()) {
                BdfReader bdfReader = new BdfReader(file);
                bdfProvider = bdfReader;
                recordingBdfConfig = bdfReader.getBdfConfig();
                RecordingSettings recordingSettings = new RecordingSettings(recordingBdfConfig);
                boolean[] isChannelsActive = RemConfig.isRemLabels(recordingBdfConfig.getSignalsLabels());
                recordingSettings.setActiveChannels(isChannelsActive);
                recordingSettings.setFile(file);
                currentDirToRead = file.getParent();
                fileToRead = file;
                mainWindow.openRecordingSettingsPreview(recordingSettings);
            } else {
                throw new ApplicationException("File: " + file + " is not valid");
            }

        } catch (ApplicationException e) {
            mainWindow.showMessage("Bdf File reading is failed!");
        }
    }

    public void setDeviceBdfProvider() {
        if (!isRecording) {
            bdfProvider = bdfDevice;
            recordingBdfConfig = new RecordingBdfConfig(bdfDevice.getBdfConfig());
            RecordingSettings recordingSettings = new RecordingSettings(recordingBdfConfig);
            boolean[] isChannelsActive = RemConfig.isRemLabels(recordingBdfConfig.getSignalsLabels());
            recordingSettings.setActiveChannels(isChannelsActive);
            recordingSettings.setDirectoryToSave(currentDirToSave);
            mainWindow.openRecordingSettingsPreview(recordingSettings);
        }
    }


    public void startDataReading(RecordingSettings recordingSettings) throws ApplicationException {
        RemConfig remConfig = new RemConfig(recordingSettings.getChannelsLabels());
        RemConfigurator remConfigurator = new RemConfigurator(recordingBdfConfig, remConfig, eogRemFrequency, accelerometerRemFrequency);
        remConfigurator.setActiveChannels(recordingSettings.getActiveChannels());
        int numberOfRecordsToJoin = remConfigurator.getNumberOfRecordsToJoin();
        BdfProvider joinedBdfProvider = new BdfRecordsJoiner(bdfProvider, numberOfRecordsToJoin);
        if (dataStore != null) {
            dataStore.clear(); // to free memory occupied by old DataStore
        }
        dataStore = new DataStore(joinedBdfProvider, remConfigurator.getDividers());
        dataStore.addListener(mainWindow);
        dataStore.setStartTime(recordingBdfConfig.getStartTime());
        GraphsConfigurator.configurate(mainWindow, dataStore);
        joinedBdfProvider.startReading();
        isRecording = true;
        System.out.println("file to save: " + recordingSettings.getFile().getAbsolutePath());
    }


    public long getStartTime() {
        return dataStore.getStartTime();
    }

}
