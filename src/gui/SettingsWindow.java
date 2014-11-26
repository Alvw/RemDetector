package gui;

import bdf.BdfConfig;
import bdf.BdfSignalConfig;
import dreamrec.ApplicationException;
import dreamrec.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;


/**
 *
 */
public class SettingsWindow extends JDialog  {

    private String patientIdentificationLabel = "Patient";
    private String recordingIdentificationLabel = "Record";
    private String title = "EDF Recorder";
    private String start = "Start";
    private String stop = "Stop";
    private String cancel = "Cancel";
    private String changeDir = "Directory";

    private int IDENTIFICATION_LENGTH = 40;
    private int FILENAME_LENGTH = 40;
    private int DIRNAME_LENGTH = 40;
    private int CHANNEL_NAME_LENGTH = 16;
    private int DIVIDER_LENGTH = 2;

    private String DEFAULT_FILENAME = "date-time.bdf";

    private JButton startButton = new JButton(start);
    private JButton cancelButton = new JButton(cancel);
    private JButton changeDirButton = new JButton(changeDir);

    private JLabel[] channelFrequency;
    private JCheckBox[] channelEnable;
    private JTextField[] channelName;
    private JTextField[] channelDivider;

    private JTextField patientIdentification;
    private JTextArea recordIdentification;

    private JTextField fileToSave;
    private JTextField dirToSave;

    private JComponent[] channelsHeaders = {new JLabel("Number"), new JLabel("Name"),
            new JLabel("Frequency (Hz)"), new JLabel("Divider"), new JLabel("Enable")};

    private BdfConfig bdfConfig;
    private Controller controller;
    private int[] frequencyDividers;
    private int numberOfChannels;
    private File fileToRead;


    public SettingsWindow(Frame owner, BdfConfig bdfConfig,  Controller controller) {
        super(owner, ModalityType.APPLICATION_MODAL);
        this.bdfConfig = bdfConfig;
        this.controller = controller;
        numberOfChannels = bdfConfig.getNumberOfSignals();
        frequencyDividers = createDefaultDividers(bdfConfig);
        init();
        arrangeForm();
        setActions();
        setVisible(true);
    }

    private int[] createDefaultDividers(BdfConfig bdfConfig) {
        int[] dividers = new int[bdfConfig.getNumberOfSignals()];
        for(int i = 0; i < dividers.length; i++) {
            dividers[i] = 1;
        }
        return dividers;
    }


    private void init() {
        patientIdentification = new JTextField(IDENTIFICATION_LENGTH);
        recordIdentification = new JTextArea(2,IDENTIFICATION_LENGTH);
        fileToSave = new JTextField(FILENAME_LENGTH);
        dirToSave = new JTextField(DIRNAME_LENGTH);

        channelFrequency = new JLabel[numberOfChannels];
        channelEnable = new JCheckBox[numberOfChannels];
        channelName = new JTextField[numberOfChannels];
        channelDivider = new JTextField[numberOfChannels];

        for (int i = 0; i < numberOfChannels; i++) {
            channelFrequency[i] = new JLabel();
            channelEnable[i] = new JCheckBox();
            channelName[i] = new JTextField(CHANNEL_NAME_LENGTH);
            channelDivider[i] = new JTextField();
            channelDivider[i].setDocument(new NumberDocument(DIVIDER_LENGTH));
            channelDivider[i].setColumns(DIVIDER_LENGTH);
        }

        loadData();
    }

    private void loadData() {
       // patientIdentification.setText(bdfConfig.getLocalPatientIdentification());
       // recordIdentification.setText(bdfConfig.getLocalRecordingIdentification());
        BdfSignalConfig[] signalsConfigList = bdfConfig.getSignalsConfigList();
        double[] frequencies = bdfConfig.getSignalsFrequencies();
        for (int i = 0; i < numberOfChannels; i++) {
            channelFrequency[i].setText(String.valueOf(frequencies[i]));
            channelName[i].setText(signalsConfigList[i].getLabel());
            channelDivider[i].setText(String.valueOf(frequencyDividers[i]));
        }
    }

    private void saveData() {
       // bdfConfig.setLocalPatientIdentification(getPatientIdentification());
       // bdfConfig.setLocalRecordingIdentification(getRecordIdentification());
        BdfSignalConfig[] signalsConfigList = bdfConfig.getSignalsConfigList();
        try {
            for (int i = 0; i < numberOfChannels; i++) {
                signalsConfigList[i].setLabel(getChannelName(i));
                if(isChannelEnable(i)) {
                    frequencyDividers[i] = getChannelDivider(i);
                }
                else {
                    frequencyDividers[i] = 0;
                }
            }
        } catch (ApplicationException e) {
            showMessage(e.getMessage());
        }
    }

    private void arrangeForm() {
        setTitle(title);

        int hgap = 5;
        int vgap = 0;
        JPanel patientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap, vgap));
        patientPanel.add(new JLabel(patientIdentificationLabel));
        patientPanel.add(patientIdentification);

        JPanel recordingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap, vgap));
        recordingPanel.add(new JLabel(recordingIdentificationLabel));
        recordingPanel.add(recordIdentification);


        hgap = 5;
        vgap = 5;
        JPanel identificationPanel = new JPanel(new BorderLayout(hgap, vgap));
        identificationPanel.add(patientPanel, BorderLayout.NORTH);
        identificationPanel.add(recordingPanel, BorderLayout.CENTER);
        hgap = 20;
        vgap = 20;
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, hgap, vgap));
        topPanel.add(identificationPanel);


        hgap = 20;
        vgap = 5;
        JPanel channelsPanel = new JPanel(new TableLayout(channelsHeaders.length, new TableOption(TableOption.CENTRE, TableOption.CENTRE), hgap, vgap));
        for (JComponent component : channelsHeaders) {
            channelsPanel.add(component);
        }
        for (int i = 0; i < numberOfChannels; i++) {
            channelsPanel.add(new JLabel(" " + (i + 1) + " "));
            channelsPanel.add(channelName[i]);
            channelsPanel.add(channelFrequency[i]);
            channelsPanel.add(channelDivider[i]);
            channelsPanel.add(channelEnable[i]);
        }

        hgap = 0;
        vgap = 10;
        JPanel channelsBorderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap, vgap));
        channelsBorderPanel.setBorder(BorderFactory.createTitledBorder("Channels"));
        channelsBorderPanel.add(channelsPanel);


        hgap = 5;
        vgap = 0;
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, hgap, vgap));
        filePanel.add(new JLabel("File Name"));
        filePanel.add(fileToSave);

        JPanel dirPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, hgap, vgap));
        dirPanel.add(changeDirButton);
        dirPanel.add(dirToSave);

        hgap = 15;
        vgap = 5;
        JPanel saveAsPanel = new JPanel(new BorderLayout(hgap, vgap));
        saveAsPanel.add(filePanel, BorderLayout.NORTH);
        saveAsPanel.add(dirPanel, BorderLayout.CENTER);

        hgap = 5;
        vgap = 10;
        JPanel saveAsBorderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, hgap, vgap));
        saveAsBorderPanel.setBorder(BorderFactory.createTitledBorder("Save As"));
        saveAsBorderPanel.add(saveAsPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cancelButton);
        buttonPanel.add(startButton);

        hgap = 0;
        vgap = 5;
        JPanel bottomPanel = new JPanel(new BorderLayout(hgap, vgap));
        bottomPanel.add(saveAsBorderPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);


        // Root Panel of the SettingsWindow
        add(topPanel, BorderLayout.NORTH);
        add(channelsBorderPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);


        pack();
        // place the window to the screen center
        setLocationRelativeTo(null);
    }

    private void setActions() {
        patientIdentification.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                patientIdentification.selectAll();
            }
        });


        recordIdentification.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                recordIdentification.selectAll();
            }
        });

        changeDirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dir = chooseDirToSave();
                if (dir != null) {
                    dirToSave.setText(dir);
                }
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
            }
        });
    }

    public String chooseDirToSave() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a directory to save");
     /*   String currentDir = GuiConfig.getCurrentDir();
        if(currentDir == null || !(new File(currentDir).exists())) {
            currentDir = System.getProperty("user.dir"); // current working directory ("./")
        } */
       // fileChooser.setCurrentDirectory(new File(currentDir));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int fileChooserState = fileChooser.showOpenDialog(this);
        if (fileChooserState == JFileChooser.APPROVE_OPTION) {
            String dir = fileChooser.getSelectedFile().getAbsolutePath();
          //  GuiConfig.setCurrentDir(dir);
            return dir;
        }
        return null;
    }

    public String getFileToSave() {
        String[] extensionList = {"bdf", "edf"};
        String filename = fileToSave.getText();
        // if filename is default filename
        if(filename.equals(DEFAULT_FILENAME)) {
            return null;
        }
        // if filename has no extension
        if(filename.lastIndexOf('.') == -1) {
            filename = filename.concat(".").concat(extensionList[0]);
            return filename;
        }
        // if  extension  match with one from given extensionList
        // (?i) makes it case insensitive (catch BDF as well as bdf)
        for(String ext : extensionList) {
            if(filename.matches("(?i).*\\."+ext)) {
                return filename;
            }
        }
        // If the extension match with NONE from given extensionList. We need to replace it
        filename = filename.substring(0, filename.lastIndexOf(".") + 1).concat(extensionList[0]);
        return filename;
    }

    private void disableEnableFields(boolean isEnable) {
        patientIdentification.setEnabled(isEnable);
        recordIdentification.setEnabled(isEnable);
        fileToSave.setEnabled(isEnable);


        for (int i = 0; i < numberOfChannels; i++) {
            channelEnable[i].setEnabled(isEnable);
            channelName[i].setEnabled(isEnable);
            channelFrequency[i].setEnabled(isEnable);
        }
    }


    private void disableFields() {
        boolean isEnable = false;
        disableEnableFields(isEnable);


    }


    private void enableFields() {
        boolean isEnable = true;
        disableEnableFields(isEnable);
        for (int i = 0; i < numberOfChannels; i++) {
            if (!isChannelEnable(i)) {
                enableChannel(i, false);
            }
        }
   /*     if (!bdfHeaderData.getAdsConfiguration().isAccelerometerEnabled()) {
            enableAccelerometer(false);
        }*/
    }


    private void enableChannel(int channelNumber, boolean isEnable) {
        channelFrequency[channelNumber].setEnabled(isEnable);
        channelName[channelNumber].setEnabled(isEnable);
    }


    private boolean isChannelEnable(int channelNumber) {
        return channelEnable[channelNumber].isSelected();
    }

    private String getChannelName(int channelNumber) {
        return channelName[channelNumber].getText();
    }

    private int getChannelDivider (int channelNumber) throws ApplicationException {
        String divString = channelDivider[channelNumber].getText();
        try {
            Integer div = Integer.parseInt(divString);
            return div;
        } catch (NumberFormatException e) {
             throw new ApplicationException("Channels Dividers should be Integer");
        }
    }


    private String getPatientIdentification() {
        return patientIdentification.getText();
    }

    private String getRecordIdentification() {
        return recordIdentification.getText();
    }

    public void showMessage(String s) {
        JOptionPane.showMessageDialog(this, s);
    }


}