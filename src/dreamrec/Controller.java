package dreamrec;

import bdf.BdfWriter;
import device.BdfConfig;
import device.BdfDataSourceActive;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class Controller {
    public static final int DRM_FREQUENCY = 50;
    public static final int ACC_FREQUENCY = 10;
    private MainViewNew mainWindow;
    private ApparatModel model;

    private Timer repaintTimer;
    private static final Log log = LogFactory.getLog(Controller.class);

    private boolean isRecording = false;
    private BdfDataSourceActive device;
    private BdfWriter bdfWriter;
    private IncomingDataBuffer incomingDataBuffer;


    private int nrOfChannelSamples = 5; //number of channel samples in data frame
    private int nrOfAccelerometerSamples = 1;


    public Controller(final ApparatModel model, BdfDataSourceActive device) {
        this.model = model;
        this.device = device;
        repaintTimer = new Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                updateModel();
                mainWindow.syncView();
            }
        });
    }


    public void setMainWindow(MainViewNew mainWindow) {
        this.mainWindow = mainWindow;
    }

    public boolean isRecording() {
        return isRecording;
    }

    protected void updateModel() {
        while (incomingDataBuffer.available()) {
            int[] frame = incomingDataBuffer.poll();
            for (int i = 0; i < nrOfChannelSamples; i++) {
                model.addCh1Data(frame[i]);
            }

            for (int i = 0; i < nrOfAccelerometerSamples; i++) {
                model.addAcc1Data(frame[nrOfChannelSamples + i]);
            }
            for (int i = 0; i < nrOfAccelerometerSamples; i++) {
                model.addAcc2Data(frame[nrOfChannelSamples + nrOfAccelerometerSamples + i]);
            }
            for (int i = 0; i < nrOfAccelerometerSamples; i++) {
                model.addAcc3Data(frame[nrOfChannelSamples + 2 * nrOfAccelerometerSamples + i]);
            }
        }
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
        repaintTimer.start();
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
        repaintTimer.stop();
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
            updateModel();
            mainWindow.repaint();
        } else {
            log.info("Open command cancelled by user.");
        }
    }

    public void readFromFile() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("./"));
            fileChooser.setFileFilter(new ExtFileFilter("drm", "*.drm Dream records"));
            int fileChooserState = fileChooser.showOpenDialog(mainWindow);
            if (fileChooserState == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                new FileIOManager().readFromFile(selectedFile, model);
                mainWindow.setTitle(selectedFile.getName());
                mainWindow.setStart(model.getStartTime(), 1000 / model.getFrequency());
            }
        } catch (ApplicationException e) {
            mainWindow.showMessage(e.getMessage());
        }
        mainWindow.syncView();
    }

   /* BdfConfig getBdfConfig() {
        BdfConfig bdfConfig = new BdfConfig();
        bdfConfig.setDurationOfADataRecord(0.1);
        bdfConfig.setFileNameToSave("tralivali1.bdf");
        bdfConfig.setLocalPatientIdentification("xxx");
        bdfConfig.setLocalRecordingIdentification("yyy");
        bdfConfig.setNumberOfSignals(6);
        List<BdfSignalConfig> bdfSignalConfigList = new ArrayList<BdfSignalConfig>();

        BdfSignalConfig ch1Config = new BdfSignalConfig();
        ch1Config.setDigitalMax(8388607);
        ch1Config.setDigitalMin(-8388608);
        int gain = 6;
        ch1Config.setPhysicalMin(-(2400000 / gain));
        ch1Config.setPhysicalMax(2400000 / gain);
        ch1Config.setLabel("EOG");
        ch1Config.setNrOfSamplesInEachDataRecord(5);
        ch1Config.setPhysicalDimension("uV");
        bdfSignalConfigList.add(ch1Config);

        for (int i = 0; i < 3; i++) {
            BdfSignalConfig adc10config = new BdfSignalConfig();
            adc10config.setDigitalMax(4095);
            adc10config.setDigitalMin(-4096);
            adc10config.setPhysicalMin(-1000);
            adc10config.setPhysicalMax(1000);
            adc10config.setLabel("Accelerometer" + i);
            adc10config.setNrOfSamplesInEachDataRecord(1);
            adc10config.setPhysicalDimension("mV");
            bdfSignalConfigList.add(adc10config);
        }
        BdfSignalConfig batteryConfig = new BdfSignalConfig();
        batteryConfig.setDigitalMax(4095);
        batteryConfig.setDigitalMin(-4096);
        batteryConfig.setPhysicalMin(-1000);
        batteryConfig.setPhysicalMax(1000);
        batteryConfig.setLabel("Battery");
        batteryConfig.setNrOfSamplesInEachDataRecord(1);
        batteryConfig.setPhysicalDimension("mV");
        bdfSignalConfigList.add(batteryConfig);
        //-----------------
        BdfSignalConfig eventConfig = new BdfSignalConfig();
        eventConfig.setDigitalMax(100);
        eventConfig.setDigitalMin(-100);
        eventConfig.setPhysicalMin(-100);
        eventConfig.setPhysicalMax(100);
        eventConfig.setLabel("Event");
        eventConfig.setNrOfSamplesInEachDataRecord(1);
        eventConfig.setPhysicalDimension("N");
        bdfSignalConfigList.add(eventConfig);

        bdfConfig.setSignalConfigList(bdfSignalConfigList);
        return bdfConfig;
    }*/
}
