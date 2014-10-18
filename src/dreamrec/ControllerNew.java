package dreamrec;

import bdf.BdfHeaderReader;
import bdf.BdfWriter;
import device.BdfConfig;
import device.BdfDataSourceActive;
import device.BdfSignalConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ControllerNew {
    public static final int DRM_FREQUENCY = 50;
    public static final int ACC_FREQUENCY = 10;
    private MainViewNew mainWindow;
    private ApparatModel model;

    private static final Log log = LogFactory.getLog(Controller.class);

    private boolean isRecording = false;
    private BdfDataSourceActive device;
    private BdfWriter bdfWriter;
    private IncomingDataBuffer incomingDataBuffer;


    private int nrOfChannelSamples = 5; //number of channel samples in data frame
    private int nrOfAccelerometerSamples = 1;


    public ControllerNew(final ApparatModel model, BdfDataSourceActive device) {
        this.model = model;
        this.device = device;
    }


    public void setMainWindow(MainViewNew mainWindow) {
        this.mainWindow = mainWindow;
    }


    public void startRecording() {
        isRecording = true;
        if (bdfWriter != null) {
            device.removeBdfDataListener(bdfWriter);
        }
        BdfConfig bdfConfig = device.getBdfConfig();
        bdfWriter = new BdfWriter(bdfConfig);
        device.addBdfDataListener(bdfWriter);
        model.clear();
        model.setFrequency(DRM_FREQUENCY);
        model.setStartTime(System.currentTimeMillis());  //todo remove
        mainWindow.setStart(model.getStartTime(), 1000 / model.getFrequency());
        incomingDataBuffer = new IncomingDataBuffer();
        device.addBdfDataListener(incomingDataBuffer);
        try {
            device.startReading();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            System.exit(0);
        }
    }

    public void stopRecording() {
        if (!isRecording) return;
        isRecording = false;
        device.stopReading();
        saveToFile();
    }

    private void saveToFile() {
        String fileName = new SimpleDateFormat("dd-MM-yyyy_HH-mm").format(new Date(System.currentTimeMillis())) + ".drm";
        try {
            new FileIOManager().saveToFile(new File(fileName), model);
        } catch (ApplicationException e) {
            String msg = "error saving to file " + fileName;
            log.error(msg, e);
            mainWindow.showMessage(msg);
        }
    }

    public void closeApplication() {
        stopRecording();
        System.exit(0);
    }

    public void readBdfFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnVal = fileChooser.showOpenDialog(mainWindow);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            log.info("Opening: " + file.getName() + ".");
            BdfReader bdfReader = new BdfReader();
            incomingDataBuffer = new IncomingDataBuffer();
            model.clear();
            bdfReader.addDataListener(incomingDataBuffer);
            bdfReader.read(file);
            mainWindow.repaint();
        } else {
            log.info("Open command cancelled by user.");
        }
    }

    public void readFromFile() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("./"));
            fileChooser.setFileFilter(new ExtFileFilter("bdf", "*.drm Dream records"));
            int fileChooserState = fileChooser.showOpenDialog(mainWindow);
            if (fileChooserState == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                BdfHeaderReader bdfHeaderReader = new BdfHeaderReader(selectedFile);
                BdfConfig bdfConfig = bdfHeaderReader.getBdfConfig();
                System.out.println(bdfConfig.getLocalPatientIdentification());
                System.out.println(bdfConfig.getLocalRecordingIdentification());
                System.out.println("StartTime: "+bdfHeaderReader.getStartTime());
                System.out.println("number of chan "+ bdfConfig.getNumberOfSignals());
                System.out.println("record duration "+ bdfConfig.getDurationOfADataRecord());
                List<BdfSignalConfig> signalConfigList = bdfConfig.getSignalConfigList();
                for(BdfSignalConfig signalConfig : signalConfigList){
                    System.out.println(signalConfig.getLabel());
                    System.out.println(signalConfig.getPhysicalDimension());
                    System.out.println(signalConfig.getPhysicalMin());
                    System.out.println(signalConfig.getPhysicalMax());
                    System.out.println(signalConfig.getDigitalMin());
                    System.out.println(signalConfig.getDigitalMax());
                    System.out.println(signalConfig.getNrOfSamplesInEachDataRecord());
                }
            }
        } catch (ApplicationException e) {
            mainWindow.showMessage(e.getMessage());
        }
        mainWindow.syncView();
    }
}
