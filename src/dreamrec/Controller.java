package dreamrec;

import bdf.*;
import gui.DataView;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import prefilters.PreFilter;

import java.io.File;

public class Controller {

    private static final Log log = LogFactory.getLog(Controller.class);
    private final double PREVIEW_TIME_FREQUENCY = 50.0 / 750;
    private boolean isRecording = false;
    private DeviceFabric deviceFabric;
    private String[] deviceSignalsLabels;
    private BdfProvider bdfProvider;
    private RecordingBdfConfig recordingBdfConfig;
    private boolean isFrequencyAutoAdjustment;
    private File fileToRead;
    private BdfWriter bdfWriter;
    private RemConfigurator remConfigurator;
    private boolean isRemMode;

    public Controller(DeviceFabric deviceFabric) {
        this.deviceFabric = deviceFabric;
    }

    public void setDeviceSignalsLabels(String[] deviceSignalsLabels) {
        this.deviceSignalsLabels = deviceSignalsLabels;
    }

    public void setRemMode(boolean isRemMode) {
        this.isRemMode = isRemMode;
    }

    public void setRemConfigurator(RemConfigurator remConfigurator) {
        this.remConfigurator = remConfigurator;
    }

    public void setFrequencyAutoAdjustment(boolean isFrequencyAutoAdjustment) {
        this.isFrequencyAutoAdjustment = isFrequencyAutoAdjustment;
    }

    public void stopRecording() throws ApplicationException {
        System.out.println("stopRecording ");
        if (isRecording) {
            bdfProvider.stopReading();
            bdfProvider = null;
            isRecording = false;
            System.out.println("stop finished ");
        }
    }

    private void saveToFile(File file) throws ApplicationException {
        if (file == null) {
            throw new ApplicationException("File to save is not specified");
        }
        if (fileToRead != null && fileToRead.equals(file)) {
            System.out.println("write header");
            //BdfHeaderWriter.writeBdfHeader(recordingBdfConfig, file);
        } else {
            System.out.println("write file");
            bdfWriter = new BdfWriter(recordingBdfConfig, file);
            bdfWriter.setFrequencyAutoAdjustment(isFrequencyAutoAdjustment);
            bdfProvider.addBdfDataListener(bdfWriter);
        }

    }

    public void closeApplication() {
        try {
            stopRecording();
        } catch (ApplicationException e) {
            log.error(e);
        }
        System.exit(0);
    }


    public RecordingSettings setFileBdfProvider(File file) throws ApplicationException {
        if (isRecording) {
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
            BdfProvider bdfDevice = deviceFabric.getDeviceImplementation();
            bdfProvider = bdfDevice;
            recordingBdfConfig = new RecordingBdfConfig(bdfDevice.getBdfConfig());
            recordingBdfConfig.setSignalsLabels(deviceSignalsLabels);
            fileToRead = null;
            return getRecordingSettings();
        } else {
            throw new ApplicationException("Recording is already start");
        }
    }


    public DataView startDataReading(RecordingSettings recordingSettings) throws ApplicationException {
        recordingBdfConfig.setPatientIdentification(recordingSettings.getPatientIdentification());
        recordingBdfConfig.setRecordingIdentification(recordingSettings.getRecordingIdentification());
        recordingBdfConfig.setSignalsLabels(recordingSettings.getChannelsLabels());
        saveToFile(recordingSettings.getFile());
        DataView dataView = new DataView();
        PreFilter[] prefilters = new PreFilter[recordingBdfConfig.getNumberOfSignals()];
        DataStore dataStore;
        if (isRemMode) {
            RemChannels remChannels = new RemChannels(recordingBdfConfig.getSignalsLabels());
            if (remConfigurator != null) {
                int numberOfRecordsToJoin = remConfigurator.getNumberOfRecordsToJoin(recordingBdfConfig);
                bdfProvider = new BdfRecordsJoiner(bdfProvider, numberOfRecordsToJoin);
                prefilters = remConfigurator.getPreFilters(recordingBdfConfig, remChannels);
            }
            dataStore = new RemDataStore(bdfProvider, remChannels);
            dataStore.setPreFilters(prefilters);
            dataStore.setChannelsMask(recordingSettings.getActiveChannels());
            GraphsConfigurator.configureRem(dataView, (RemDataStore) dataStore);
        } else {
            dataStore = new DataStore(bdfProvider);
            dataStore.setChannelsMask(recordingSettings.getActiveChannels());
            GraphsConfigurator.configure(dataView, dataStore);
        }
        dataStore.addListener(dataView);
        dataStore.setStartTime(recordingBdfConfig.getStartTime());
        dataView.setPreviewFrequency(PREVIEW_TIME_FREQUENCY);
        bdfProvider.startReading();
        isRecording = true;
        return dataView;
    }


    private RecordingSettings getRecordingSettings() {
        RecordingSettings recordingSettings = new RecordingSettings(recordingBdfConfig);
        if (isRemMode) {
            boolean[] isChannelsActive = RemChannels.isRemLabels(recordingBdfConfig.getSignalsLabels());
            recordingSettings.setActiveChannels(isChannelsActive);
        }
        recordingSettings.setFile(fileToRead);
        return recordingSettings;
    }

}
