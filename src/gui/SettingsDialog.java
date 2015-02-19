package gui;

import dreamrec.ApplicationException;
import dreamrec.RecordingSettings;
import gui.layouts.TableLayout;
import gui.layouts.TableOption;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

class SettingsDialog extends JDialog {

    private final String PATIENT_IDENTIFICATION_LABEL = "Patient";
    private final String RECORD_IDENTIFICATION_LABEL = "Record";
    private final String START_BUTTON_LABEL = "Start";
    private final String CANCEL_BUTTON_LABEL = "Cancel";
    private final String CHANGE_DIR_BUTTON_LABEL = "Directory";

    private final int IDENTIFICATION_LENGTH = 40;
    private final int FILENAME_LENGTH = 40;
    private final int DIRNAME_LENGTH = 40;
    private final int CHANNEL_NAME_LENGTH = 16;

    private final String TITLE = "Settings: ";

    private JButton startButton = new JButton(START_BUTTON_LABEL);
    private JButton cancelButton = new JButton(CANCEL_BUTTON_LABEL);
    private JButton changeDirButton = new JButton(CHANGE_DIR_BUTTON_LABEL);

    private JLabel[] channelsFrequenciesFields;
    private JCheckBox[] isChannelsActiveFields;
    private JTextField[] channelsLabelsFields;

    private JTextField patientIdentificationField;
    private JTextArea recordIdentificationField;

    private JTextField filenameToSaveField;
    private JTextField dirToSaveField;


    private JComponent[] headersForChannelsSettings = {new JLabel("Number"), new JLabel("Name"),
            new JLabel("Frequency (Hz)"), new JLabel("Enable")};

    private JPanel saveAsBorderPanel;


    private RecordingSettings resultSettings;
    private MainWindow parent;
    private File fileToRead;


    public SettingsDialog(MainWindow parent,  RecordingSettings initialSettings) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.parent = parent;

        String title = TITLE;
        int numberOfChannels = 0;

        if (initialSettings != null) {
            numberOfChannels = initialSettings.getChannelsLabels().length;
            String filename = initialSettings.getFilename();
            if( filename != null) {
                fileToRead = new File(initialSettings.getDirectoryToSave(), filename);
                title = TITLE + filename;
            }
        }
        setTitle(title);

        patientIdentificationField = new JTextField(IDENTIFICATION_LENGTH);
        recordIdentificationField = new JTextArea(2, IDENTIFICATION_LENGTH);
        patientIdentificationField.setDocument(new FixSizeDocument(IDENTIFICATION_LENGTH * 2));
        recordIdentificationField.setDocument(new FixSizeDocument(IDENTIFICATION_LENGTH * 2));
        filenameToSaveField = new JTextField(FILENAME_LENGTH);
        dirToSaveField = new JTextField(DIRNAME_LENGTH);
        dirToSaveField.setEnabled(false);

        channelsFrequenciesFields = new JLabel[numberOfChannels];
        isChannelsActiveFields = new JCheckBox[numberOfChannels];
        channelsLabelsFields = new JTextField[numberOfChannels];

        for (int i = 0; i < numberOfChannels; i++) {
            channelsFrequenciesFields[i] = new JLabel();
            channelsLabelsFields[i] = new JTextField(CHANNEL_NAME_LENGTH);
            channelsLabelsFields[i].setDocument(new FixSizeDocument(CHANNEL_NAME_LENGTH));
            isChannelsActiveFields[i] = new JCheckBox();
            isChannelsActiveFields[i].addItemListener(new setChannelActiveListener(i));
        }
        arrangeForm();
        setActions();
        loadData(initialSettings);
        setVisible(true);
    }

    public static RecordingSettings showDialog(MainWindow parent,  RecordingSettings initialSettings) {
        SettingsDialog settingsDialog = new SettingsDialog(parent, initialSettings);
        return settingsDialog.getResultSettings();
    }


    private RecordingSettings getResultSettings() {
        return resultSettings;
    }



    private void loadData(RecordingSettings initialSettings) {
        if (initialSettings != null) {
            patientIdentificationField.setText(initialSettings.getPatientIdentification());
            recordIdentificationField.setText(initialSettings.getRecordingIdentification());

            int[] frequencies = initialSettings.getChannelsFrequencies();
            String[] labels = initialSettings.getChannelsLabels();
            boolean[] isActives = initialSettings.getActiveChannels();

            int numberOfChannels = labels.length;
            for (int i = 0; i < numberOfChannels; i++) {
                channelsFrequenciesFields[i].setText(String.valueOf(frequencies[i]));
                channelsLabelsFields[i].setText(labels[i]);
                isChannelsActiveFields[i].setSelected(true);
                isChannelsActiveFields[i].setSelected(isActives[i]);
            }

            filenameToSaveField.setText(initialSettings.getFilename());
            String dirToSave = initialSettings.getDirectoryToSave();
            if(dirToSave == null || ! new File(dirToSave).isDirectory()) {
                dirToSave = System.getProperty("user.dir"); // current working directory ("./");
            }
            dirToSaveField.setText(dirToSave);
            // saveAsBorderPanel.setVisible(false);
            pack();
        }

    }

    private void setActions() {
        patientIdentificationField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                patientIdentificationField.selectAll();
            }
        });

        filenameToSaveField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                filenameToSaveField.selectAll();
            }
        });

        changeDirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dir = chooseDirToSave();
                if (dir != null) {
                    dirToSaveField.setText(dir);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dispose();
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String filename = getFileToSave();
                filename = parent.normalizeFilename(filename);
                File fileToSave = new File(getDirToSave(), filename);
                if ( ! fileToSave.equals(fileToRead) && fileToSave.isFile()) {
                    int dialogButton = JOptionPane.YES_NO_OPTION;
                    String dialogTitle = "Choose option";
                    String dialogMsg = "The file: " + fileToSave.getAbsolutePath() + " is already exists! \nAre you sure you want to change it?";
                    int dialogResult = JOptionPane.showConfirmDialog(SettingsDialog.this, dialogMsg, dialogTitle, dialogButton);
                    if (dialogResult == JOptionPane.NO_OPTION) {
                        return;
                    }
                }

                getRecordingSettings();

                try{
                    parent.startRecording(resultSettings, fileToRead);
                    dispose();
                } catch (ApplicationException e) {
                    showMessage(e.getMessage());
                }
            }
        });
    }


    private void getRecordingSettings() {
        resultSettings = new RecordingSettings(getChannelsLabels());
        resultSettings.setRecordingIdentification(getRecordIdentification());
        resultSettings.setPatientIdentification(getPatientIdentification());
        resultSettings.setActiveChannels(getActiveChannels());
        resultSettings.setChannelsFrequencies(getChannelsFrequencies());
        resultSettings.setFilename(getFileToSave());
        resultSettings.setDirectoryToSave(getDirToSave());
        getChannelsFrequencies();
    }


    private void arrangeForm() {
        int hgap = 10;
        int vgap = 0;

        JPanel patientPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, hgap, vgap));
        patientPanel.add(new JLabel(PATIENT_IDENTIFICATION_LABEL));
        patientPanel.add(patientIdentificationField);
        patientIdentificationField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JPanel recordingPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, hgap, vgap));
        recordingPanel.add(new JLabel(RECORD_IDENTIFICATION_LABEL));
        recordingPanel.add(recordIdentificationField);
        recordIdentificationField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        hgap = 5;
        vgap = 5;
        JPanel identificationPanel = new JPanel(new BorderLayout(hgap, vgap));
        identificationPanel.add(patientPanel, BorderLayout.CENTER);
        identificationPanel.add(recordingPanel, BorderLayout.SOUTH);

        hgap = 20;
        vgap = 0;
        JPanel topPanel = new JPanel(new BorderLayout(hgap, vgap));
        topPanel.add(identificationPanel,BorderLayout.CENTER);

        JPanel channelsBorderPanel = new JPanel();

        hgap = 20;
        vgap = 5;
        JPanel channelsPanel = new JPanel(new TableLayout(headersForChannelsSettings.length, new TableOption(TableOption.CENTRE, TableOption.CENTRE), hgap, vgap));
        for (JComponent component : headersForChannelsSettings) {
            channelsPanel.add(component);
        }
        int numberOfChannels = channelsLabelsFields.length;
        for (int i = 0; i < numberOfChannels; i++) {
            channelsPanel.add(new JLabel(" " + (i + 1) + " "));
            channelsPanel.add(channelsLabelsFields[i]);
            channelsPanel.add(channelsFrequenciesFields[i]);
            channelsPanel.add(isChannelsActiveFields[i]);
        }

        if (numberOfChannels > 0) {
            hgap = 0;
            vgap = 10;
            channelsBorderPanel.setLayout(new FlowLayout(FlowLayout.LEFT, hgap, vgap));
            channelsBorderPanel.setBorder(BorderFactory.createTitledBorder("Channels"));
            channelsBorderPanel.add(channelsPanel);
        }

        hgap = 5;
        vgap = 0;
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, hgap, vgap));
        filePanel.add(new JLabel("File Name  "));
        filePanel.add(filenameToSaveField);

        JPanel dirPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, hgap, vgap));
        dirPanel.add(changeDirButton);
        dirPanel.add(dirToSaveField);

        hgap = 15;
        vgap = 5;
        JPanel saveAsPanel = new JPanel(new BorderLayout(hgap, vgap));
        saveAsPanel.add(filePanel, BorderLayout.NORTH);
        saveAsPanel.add(dirPanel, BorderLayout.CENTER);

        hgap = 5;
        vgap = 10;
        saveAsBorderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, hgap, vgap));
        saveAsBorderPanel.setBorder(BorderFactory.createTitledBorder("Save As"));
        saveAsBorderPanel.add(saveAsPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(cancelButton);


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



    public String chooseDirToSave() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a directory to save");
        fileChooser.setCurrentDirectory(parent.getDirectoryToSave());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int fileChooserState = fileChooser.showOpenDialog(this);
        if (fileChooserState == JFileChooser.APPROVE_OPTION) {
            String dir = fileChooser.getSelectedFile().getAbsolutePath();
            parent.setDirectoryToSave(dir);
            return dir;
        }
        return null;
    }

   private String getDirToSave() {
        return dirToSaveField.getText();
    }

   private String getFileToSave() {
        return filenameToSaveField.getText();
    }

    private boolean[] getActiveChannels() {
        boolean[] isActives = new boolean[isChannelsActiveFields.length];
        for (int i = 0; i < isChannelsActiveFields.length; i++) {
            isActives[i] = isChannelsActiveFields[i].isSelected();
        }
        return isActives;
    }

    private int[] getChannelsFrequencies() {

        int[] frequencies = new int[channelsFrequenciesFields.length];
        for (int i = 0; i < channelsFrequenciesFields.length; i++) {
            frequencies[i] = Integer.parseInt(channelsFrequenciesFields[i].getText());
        }
        return frequencies;
    }

    private String[] getChannelsLabels() {
        String[] labels = new String[channelsLabelsFields.length];
        for (int i = 0; i < channelsLabelsFields.length; i++) {
            labels[i] = channelsLabelsFields[i].getText();
        }
        return labels;
    }

    private String getPatientIdentification() {
        return patientIdentificationField.getText();
    }

    private String getRecordIdentification() {
        return recordIdentificationField.getText();
    }

    private void showMessage(String s) {
        JOptionPane.showMessageDialog(this, s);
    }

    private class setChannelActiveListener implements ItemListener {
        private int channelNumber;

        private setChannelActiveListener(int channelNumber) {
            this.channelNumber = channelNumber;
        }

        @Override
        public void itemStateChanged(ItemEvent itemEvent) {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                channelsLabelsFields[channelNumber].setEnabled(true);
            } else {
                channelsLabelsFields[channelNumber].setEnabled(false);
            }
        }
    }
}