package dreamrec;

import bdf.*;
import gui.DataView;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Controller {

    private static final Log log = LogFactory.getLog(Controller.class);

    private boolean isRecording = false;
    private BdfDevice bdfDevice;
    private BdfProvider bdfProvider;
    private RecordingBdfConfig recordingBdfConfig;
    private DataStore dataStore;
    private boolean isFrequencyAutoAdjustment;
    private File fileToRead;
    private BdfWriter bdfWriter;
    private RemConfigurator remConfigurator;

    public Controller(BdfDevice bdfDevice, RemConfigurator remConfigurator, boolean isFrequencyAutoAdjustment) {
        this.bdfDevice = bdfDevice;
        this.remConfigurator = remConfigurator;
        this.isFrequencyAutoAdjustment = isFrequencyAutoAdjustment;
    }



    public void stopRecording() throws ApplicationException {
        if (isRecording) {
            bdfProvider.stopReading();
            bdfProvider.removeBdfDataListener(dataStore);
            bdfProvider.removeBdfDataListener(bdfWriter);
            isRecording = false;
         }

        if (!isRecording) return;
        isRecording = false;
    }

    private void saveToFile(String dir)  throws  ApplicationException{
        String filename = new SimpleDateFormat("dd-MM-yyyy_HH-mm").format(new Date(System.currentTimeMillis())) + ".drm";
        saveToFile(new File(filename, dir));
    }

    private void saveToFile(File file)  throws  ApplicationException{
       if(fileToRead != null && fileToRead.equals(file)) {
            BdfHeaderWriter.writeBdfHeader(recordingBdfConfig, file);
        }
        else {
            bdfWriter = new BdfWriter(recordingBdfConfig, file);
            bdfWriter.setFrequencyAutoAdjustment(isFrequencyAutoAdjustment);
            bdfProvider.addBdfDataListener(bdfWriter);
        }

    }

    public void closeApplication() throws ApplicationException {
        stopRecording();
        System.exit(0);
    }


    public RecordingSettings setFileBdfProvider(File file)  throws ApplicationException {
        if(isRecording) {
            stopRecording();
        }
        if (file != null && file.exists() && file.isFile()) {
            BdfReader bdfReader = new BdfReader(file);
            bdfProvider = bdfReader;
            recordingBdfConfig = bdfReader.getBdfConfig();
            fileToRead = file;
            return getRecordingSettings();
        } else {
            throw new ApplicationException("File: " + file + " is not valid");
        }
    }

    public RecordingSettings setDeviceBdfProvider() throws ApplicationException {
        if (!isRecording) {
            bdfProvider = bdfDevice;
            recordingBdfConfig = new RecordingBdfConfig(bdfDevice.getBdfConfig());
            fileToRead = null;
            return getRecordingSettings();
        }
        else {
            throw new ApplicationException("Recording is already start");
        }
    }


    public DataView startDataReading(RecordingSettings recordingSettings) throws ApplicationException {
        recordingBdfConfig.setPatientIdentification(recordingSettings.getPatientIdentification());
        recordingBdfConfig.setRecordingIdentification(recordingSettings.getRecordingIdentification());
        recordingBdfConfig.setSignalsLabels(recordingSettings.getChannelsLabels());
        int numberOfRecordsToJoin = remConfigurator.getNumberOfRecordsToJoin(recordingBdfConfig);
        BdfProvider joinedBdfProvider = new BdfRecordsJoiner(bdfProvider, numberOfRecordsToJoin);
        if (dataStore != null) {
            dataStore.clear(); // stop update timer and free memory occupied by old DataStore
        }
        dataStore = new DataStore(joinedBdfProvider, recordingSettings.getActiveChannels(), remConfigurator.getPreFilters(recordingBdfConfig));
        DataView dataView = new DataView();
        GraphsConfigurator.configurate(dataView, dataStore);
        dataStore.addListener(dataView);
        dataStore.setStartTime(recordingBdfConfig.getStartTime());

        File fileToSave = recordingSettings.getFile();
        String currentDirToSave = recordingSettings.getDirectoryToSave();
        if(fileToSave != null) {
            saveToFile(fileToSave);
        }
        else {
            saveToFile(currentDirToSave);
        }

        joinedBdfProvider.startReading();
        isRecording = true;
        System.out.println("file to save: " + recordingSettings.getFile().getAbsolutePath());
        return dataView;
    }

    private RecordingSettings getRecordingSettings() {
        RecordingSettings recordingSettings = new RecordingSettings(recordingBdfConfig);
        boolean[] isChannelsActive = RemConfig.isRemLabels(recordingBdfConfig.getSignalsLabels());
        recordingSettings.setActiveChannels(isChannelsActive);
        recordingSettings.setFile(fileToRead);
        return recordingSettings;
    }

}
