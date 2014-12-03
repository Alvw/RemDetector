package gui;

import bdf.SignalConfig;
import dreamrec.ApplicationException;
import dreamrec.Controller;
import dreamrec.RecordingSettings;

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
    private String start = "Start";
    private String stop = "Stop";
    private String cancel = "Cancel";
    private String changeDir = "Directory";

    private int IDENTIFICATION_LENGTH = 40;
    private int FILENAME_LENGTH = 40;
    private int DIRNAME_LENGTH = 40;
    private int CHANNEL_NAME_LENGTH = 16;

    private String DEFAULT_FILENAME = "date-time.bdf";
    private String DEFAULT_TITLE = "Recording Settings";

    private JButton startButton = new JButton(start);
    private JButton cancelButton = new JButton(cancel);
    private JButton changeDirButton = new JButton(changeDir);

    private JLabel[] channelsFrequencies;
    private JCheckBox[] activeChannels;
    private JTextField[] channelsLabels;

    private JTextField patientIdentification;
    private JTextArea recordIdentification;

    private JTextField fileToSave;
    private JTextField dirToSave;

    private JComponent[] channelsHeaders = {new JLabel("Number"), new JLabel("Name"),
            new JLabel("Frequency (Hz)"),  new JLabel("Enable")};

    private RecordingSettings recordingSettings;
    private String currentDir = System.getProperty("user.dir"); // current working directory ("./");
    private Controller controller;


    public SettingsWindow(JFrame owner, Controller controller, RecordingSettings recordingSettings) {
        super(owner, ModalityType.APPLICATION_MODAL);
        this.recordingSettings = recordingSettings;
        this.controller = controller;
        setTitle(DEFAULT_TITLE);
        init();
        arrangeForm();
        setActions();
        setVisible(true);
    }


    private void init() {
        patientIdentification = new JTextField(IDENTIFICATION_LENGTH);
        recordIdentification = new JTextArea(2,IDENTIFICATION_LENGTH);
        patientIdentification.setDocument(new FixSizeDocument(IDENTIFICATION_LENGTH * 2));
        recordIdentification.setDocument(new FixSizeDocument(IDENTIFICATION_LENGTH * 2));
        fileToSave = new JTextField(FILENAME_LENGTH);
        dirToSave = new JTextField(DIRNAME_LENGTH);
        dirToSave.setEnabled(false);

        int numberOfChannels = recordingSettings.getChannelsLabels().length;

        channelsFrequencies = new JLabel[numberOfChannels];
        activeChannels = new JCheckBox[numberOfChannels];
        channelsLabels = new JTextField[numberOfChannels];

        for (int i = 0; i < numberOfChannels; i++) {
            channelsFrequencies[i] = new JLabel();
            channelsLabels[i] = new JTextField(CHANNEL_NAME_LENGTH);
            channelsLabels[i].setDocument(new FixSizeDocument(CHANNEL_NAME_LENGTH));
            activeChannels[i] = new JCheckBox();
            activeChannels[i].addItemListener(new EnableChannelListener(i));
        }

        loadData();
    }


    private class EnableChannelListener implements ItemListener {
        private int channelNumber;

        private EnableChannelListener(int channelNumber) {
            this.channelNumber = channelNumber;
        }

        @Override
        public void itemStateChanged(ItemEvent itemEvent) {
            if(itemEvent.getStateChange() == ItemEvent.SELECTED) {
                channelsLabels[channelNumber].setEnabled(true);
            }
            else {
                channelsLabels[channelNumber].setEnabled(false);
            }
        }
    }


    private void loadData() {
        patientIdentification.setText(recordingSettings.getPatientIdentification());
        recordIdentification.setText(recordingSettings.getRecordingIdentification());

        double[] frequencies = recordingSettings.getChannelsFrequencies();
        String[] labels = recordingSettings.getChannelsLabels();
        boolean[] isActives = recordingSettings.getActiveChannels();

        int numberOfChannels = labels.length;

        for (int i = 0; i < numberOfChannels; i++) {
            int frequency = (int) Math.round(frequencies[i]);
            channelsFrequencies[i].setText(String.valueOf(frequency));
            channelsLabels[i].setText(labels[i]);
            activeChannels[i].setSelected(true);
            activeChannels[i].setSelected(isActives[i]);
        }

        File file = recordingSettings.getFile();
        String dirName = recordingSettings.getDirectoryToSave();

        if(dirName != null ) {
            File dir = new File(dirName);
            if(dir.isDirectory() && dir.exists()) {
                currentDir = dirName;
            }
        }
        if(file != null & file.isFile()) {
            fileToSave.setText(file.getName());
            dirToSave.setText(file.getParent());
            setTitle(file.getName());
        }
        else{
            fileToSave.setText(DEFAULT_FILENAME);
            dirToSave.setText(currentDir);
        }
    }

    private RecordingSettings saveData() {
        RecordingSettings resultingSettings = new RecordingSettings(getChannelsLabels());
        resultingSettings.setRecordingIdentification(getRecordIdentification());
        resultingSettings.setPatientIdentification(getPatientIdentification());
        resultingSettings.setActiveChannels(getActiveChannels());

        String filename = getFileToSave();
        if(filename != null) {
            resultingSettings.setFile(new File(getDirToSave(), filename));
        }
        resultingSettings.setDirectoryToSave(currentDir);
        return resultingSettings;
    }


    private void arrangeForm() {
        int hgap = 10;
        int vgap = 0;
        JPanel patientPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, hgap, vgap));
        patientPanel.add(new JLabel(patientIdentificationLabel));
        patientPanel.add(patientIdentification);
        patientIdentification.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JPanel recordingPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, hgap, vgap));
        recordingPanel.add(new JLabel(recordingIdentificationLabel));
        recordingPanel.add(recordIdentification);
        recordIdentification.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));


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
        int numberOfChannels = channelsLabels.length;
        for (int i = 0; i < numberOfChannels; i++) {
            channelsPanel.add(new JLabel(" " + (i + 1) + " "));
            channelsPanel.add(channelsLabels[i]);
            channelsPanel.add(channelsFrequencies[i]);
            channelsPanel.add(activeChannels[i]);
        }

        hgap = 0;
        vgap = 10;
        JPanel channelsBorderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap, vgap));
        channelsBorderPanel.setBorder(BorderFactory.createTitledBorder("Channels"));
        channelsBorderPanel.add(channelsPanel);


        hgap = 5;
        vgap = 0;
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, hgap, vgap));
        filePanel.add(new JLabel("File Name  "));
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
        changeDirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dir = chooseDirToSave();
                if (dir != null) {
                    dirToSave.setText(dir);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                close();
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    controller.startDataReading(saveData());
                    close();
                } catch (ApplicationException e) {
                    showMessage(e.getMessage());
                    //  System.exit(0);
                }
            }
        });
    }

    public void close() {
        SettingsWindow.this.dispose();
    }

    public String chooseDirToSave() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a directory to save");
        fileChooser.setCurrentDirectory(new File(currentDir));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int fileChooserState = fileChooser.showOpenDialog(this);
        if (fileChooserState == JFileChooser.APPROVE_OPTION) {
            String dir = fileChooser.getSelectedFile().getAbsolutePath();
            currentDir = dir;
            return dir;
        }
        return null;
    }

    public String getDirToSave() {
         return dirToSave.getText();
    }

    public String getFileToSave() {
        String[] extensionList = {"bdf", "edf"};
        String filename = fileToSave.getText();
        // if filename is default filename
        if(filename.equals(DEFAULT_FILENAME)) {
          //  return null;
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


    private boolean[] getActiveChannels() {
        boolean[] isActives = new boolean[activeChannels.length];
        for(int i = 0; i < activeChannels.length; i++) {
            isActives[i] = activeChannels[i].isSelected();
        }
        return isActives;
    }

    private String[] getChannelsLabels() {
        String[] labels = new String[channelsLabels.length];
        for(int i = 0; i < channelsLabels.length; i++) {
            labels[i] = channelsLabels[i].getText();
        }
        return labels;
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