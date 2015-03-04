package dreamrec;

import bdf.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mac on 17/02/15.
 */
public class Controller  implements InputEventHandler {
    private static final Log log = LogFactory.getLog(Controller.class);

    private List<ControllerListener> listenerList = new ArrayList<ControllerListener>();

    private static final String[] FILE_EXTENSIONS = {"bdf", "edf"};
    private DeviceFabric deviceFabric;
    private String[] deviceSignalsLabels;
    private BdfProvider bdfProvider;
    private RecordingBdfConfig recordingBdfConfig;
    private boolean isFrequencyAutoAdjustment;
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

    public void addListener(ControllerListener listener) {
        listenerList.add(listener);
    }

    public void removeListener(ControllerListener listener) {
        listenerList.remove(listener);
    }


    private void fireDataStoreUpdated(Object dataStore) {
        for (ControllerListener listener : listenerList) {
            listener.dataStoreUpdated(dataStore);
        }
    }

    @Override
    public void startRecording(RecordingSettings recordingSettings, File file) throws ApplicationException {
        stopRecording();

        if(file == null) { // device
            BdfProvider bdfDevice = deviceFabric.getDeviceImplementation();
            bdfProvider = bdfDevice;
            recordingBdfConfig = new RecordingBdfConfig(bdfDevice.getBdfConfig());
        }
        else if (file.isFile()) { // file
            BdfReader bdfReader = new BdfReader(file);
            bdfProvider = bdfReader;
            recordingBdfConfig = bdfReader.getBdfConfig();
        }
        else {
            throw new ApplicationException("File: " + file + " is not valid");
        }

        String dirToSave = recordingSettings.getDirectoryToSave();
        if(dirToSave == null || ! new File(dirToSave).isDirectory()) {
            dirToSave = System.getProperty("user.dir"); // current working directory ("./");
        }

        String filenameToSave = recordingSettings.getFilename();
        filenameToSave = normalizeFilename(filenameToSave);
        File fileToSave = new File(dirToSave, filenameToSave);
        if( ! fileToSave.equals(file)) { // fileToSave does not equal to fileToRead
            System.out.println("write file: "+fileToSave.getAbsolutePath());
            bdfWriter = new BdfWriter(recordingBdfConfig, fileToSave);
            bdfWriter.setFrequencyAutoAdjustment(isFrequencyAutoAdjustment);
            bdfProvider.addBdfDataListener(bdfWriter);
        }


        recordingBdfConfig.setPatientIdentification(recordingSettings.getPatientIdentification());
        recordingBdfConfig.setRecordingIdentification(recordingSettings.getRecordingIdentification());
        recordingBdfConfig.setSignalsLabels(recordingSettings.getChannelsLabels());


        if (isRemMode) {
            RemChannels remChannels = new RemChannels(recordingBdfConfig.getSignalsLabels());
            RemDataStore dataStore  = new RemDataStore(bdfProvider, remChannels);
            dataStore.configure(remConfigurator);
            dataStore.setChannelsMask(recordingSettings.getActiveChannels());
            dataStore.setStartTime(recordingBdfConfig.getStartTime());
            System.out.println("acc fr "+dataStore.getAccXData().getFrequency());
            fireDataStoreUpdated(dataStore);

        } else {
            DataStore dataStore = new DataStore(bdfProvider);
            dataStore.setChannelsMask(recordingSettings.getActiveChannels());
            dataStore.setStartTime(recordingBdfConfig.getStartTime());

            fireDataStoreUpdated(dataStore);
        }

        bdfProvider.startReading();

    }

    @Override
    public void stopRecording() throws ApplicationException {
        if(bdfProvider != null) {
            bdfProvider.stopReading();
            bdfProvider = null;
        }
    }

    @Override
    public RecordingSettings getRecordingSettings(File file) throws ApplicationException {
        RecordingSettings recordingSettings;
        if(file == null) { // device
            BdfConfig deviceBdfConfig = deviceFabric.getDeviceImplementation().getBdfConfig();
            RecordingBdfConfig recordingBdfConfig = new RecordingBdfConfig(deviceBdfConfig);
            recordingBdfConfig.setSignalsLabels(deviceSignalsLabels);
            recordingSettings = new RecordingSettings(recordingBdfConfig);
        }
        else if (file.isFile()) { // file
            RecordingBdfConfig recordingBdfConfig = BdfHeaderReader.readBdfHeader(file);
            recordingSettings = new RecordingSettings(recordingBdfConfig);
            recordingSettings.setFilename(file.getName());
            recordingSettings.setDirectoryToSave(file.getParent());
        }
        else {
            throw new ApplicationException("File: " + file + " is not valid");
        }

        if (isRemMode) {
            boolean[] isChannelsActive = RemChannels.isRemLabels(recordingSettings.getChannelsLabels());
            recordingSettings.setActiveChannels(isChannelsActive);
        }
        return recordingSettings;
    }

    @Override
    public String normalizeFilename(String filename) {
        String defaultFilename = new SimpleDateFormat("dd-MM-yyyy_HH-mm").format(new Date(System.currentTimeMillis()))
                + "." + FILE_EXTENSIONS[0];

        // if filename is default filename
        if (filename == null || filename.isEmpty()) {
            return defaultFilename;
        }
        filename = filename.trim();

        // if filename has no extension
        if (filename.lastIndexOf('.') == -1) {
            filename = filename.concat(".").concat(FILE_EXTENSIONS[0]);
            return filename;
        }
        // if  extension  match with one from given FILE_EXTENSIONS
        // (?i) makes it case insensitive (catch BDF as well as bdf)
        for (String ext : FILE_EXTENSIONS) {
            if (filename.matches("(?i).*\\." + ext)) {
                return filename;
            }
        }
        // If the extension match with NONE from given FILE_EXTENSIONS. We need to replace it
        filename = filename.substring(0, filename.lastIndexOf(".") + 1).concat(FILE_EXTENSIONS[0]);
        return filename;
    }

    @Override
    public String[] getFileExtensions() {
        return FILE_EXTENSIONS;
    }
}
