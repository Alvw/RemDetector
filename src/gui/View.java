package gui;

import dreamrec.ApplicationException;
import dreamrec.Controller;
import dreamrec.RecordingSettings;
import graph.GraphsView;


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

public  class View extends JFrame {
    private final String TITLE = "Dream Recorder";
    private final Color MENU_BG_COLOR = Color.GRAY;
    private final Color MENU_TEXT_COLOR = Color.BLACK;

    protected GraphsView graphsView;
    private  JMenuBar menu = new JMenuBar();
    private Controller controller;
    private String currentDirToRead = System.getProperty("user.dir"); // current working directory ("./")


    public View(Controller controller) {
        this.controller = controller;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(TITLE);
        menu.setBackground(MENU_BG_COLOR);
        menu.setForeground(MENU_TEXT_COLOR);
        menu.setBorder(BorderFactory.createEmptyBorder());
        formMenu();
        graphsView = new GraphsView();
        graphsView.setPreferredSize(getWorkspaceDimension());
        add(graphsView, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    public void setDataView(DataView dataView) {
        if(graphsView != null) {
            remove(graphsView);
        }
        graphsView = dataView;
        graphsView.setPreferredSize(getWorkspaceDimension());
        add(graphsView, BorderLayout.CENTER);
        pack();
    }

    public void setCurrentDirToRead(String currentDirToRead) {
        if(currentDirToRead != null && new File(currentDirToRead).exists()) {
            this.currentDirToRead = currentDirToRead;
        }
    }

    public void showMessage(String s) {
        JOptionPane.showMessageDialog(this, s);
    }


    private Dimension getWorkspaceDimension() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int width = dimension.width - 20;
        int height = dimension.height - 150;
        return new Dimension(width, height);
    }


    private void formMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setBackground(MENU_BG_COLOR);
        fileMenu.setForeground(MENU_TEXT_COLOR);
        menu.add(fileMenu);
        JMenuItem open = new JMenuItem("Open");
        fileMenu.add(open);

        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                File file = chooseFileToRead();
                if(file != null) {
                    try {
                        RecordingSettings recordingSettings = controller.setFileBdfProvider(file);
                        new SettingsWindow(View.this, recordingSettings);
                    }
                    catch (ApplicationException e) {
                        showMessage(e.getMessage());
                    }

                }
            }
        });

        JMenu recordMenu = new JMenu("Record");
        recordMenu.setBackground(MENU_BG_COLOR);
        recordMenu.setForeground(MENU_TEXT_COLOR);
        menu.add(recordMenu);
        JMenuItem start = new JMenuItem("Start");
        JMenuItem stop = new JMenuItem("Stop");
        recordMenu.add(start);
        recordMenu.add(stop);

        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    RecordingSettings recordingSettings = controller.setDeviceBdfProvider();
                    new SettingsWindow(View.this, recordingSettings);
                }
                catch (ApplicationException e) {
                    showMessage(e.getMessage());
                }

            }
        });

        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    controller.stopRecording();
                }
                catch (ApplicationException e) {
                    showMessage(e.getMessage());
                }

            }
        });

        add(menu, BorderLayout.NORTH);
    }

    public File chooseFileToRead() {
        String[] extensionList = {"bdf", "edf"};
        String extensionDescription = extensionList[0];
        for(int i = 1; i < extensionList.length; i++) {
            extensionDescription = extensionDescription.concat(", ").concat(extensionList[i]);
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(currentDirToRead));
        fileChooser.setFileFilter(new FileNameExtensionFilter(extensionDescription, extensionList));
        int fileChooserState = fileChooser.showOpenDialog(this);
        if (fileChooserState == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            currentDirToRead = file.getParent();
            return file;
        }
        return null;
    }


    class SettingsWindow extends JDialog  {

        private final String PATIENT_IDENTIFICATION_LABEL = "Patient";
        private final String RECORD_IDENTIFICATION_LABEL = "Record";
        private final String START_BUTTON_LABEL = "Start";
        private final String CANCEL_BUTTON_LABEL = "Cancel";
        private final String CHANGE_DIR_BUTTON_LABEL = "Directory";

        private final int IDENTIFICATION_LENGTH = 40;
        private final int FILENAME_LENGTH = 40;
        private final int DIRNAME_LENGTH = 40;
        private final int CHANNEL_NAME_LENGTH = 16;

        private final String DEFAULT_FILENAME = "date-time.bdf";
        private final String TITLE = "Recording Settings";

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
                new JLabel("Frequency (Hz)"),  new JLabel("Enable")};

        private RecordingSettings recordingSettings;
        private View mainWindow;
        private String currentDir = System.getProperty("user.dir"); // current working directory ("./");


        public SettingsWindow(View owner, RecordingSettings recordingSettings) {
            super(owner, ModalityType.APPLICATION_MODAL);
            this.recordingSettings = recordingSettings;
            mainWindow = owner;
            setTitle(TITLE);
            init();
            arrangeForm();
            setActions();
            setVisible(true);
        }


        private void init() {
            patientIdentificationField = new JTextField(IDENTIFICATION_LENGTH);
            recordIdentificationField = new JTextArea(2,IDENTIFICATION_LENGTH);
            patientIdentificationField.setDocument(new FixSizeDocument(IDENTIFICATION_LENGTH * 2));
            recordIdentificationField.setDocument(new FixSizeDocument(IDENTIFICATION_LENGTH * 2));
            filenameToSaveField = new JTextField(FILENAME_LENGTH);
            dirToSaveField = new JTextField(DIRNAME_LENGTH);
            dirToSaveField.setEnabled(false);

            int numberOfChannels = recordingSettings.getChannelsLabels().length;

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

            loadData();
        }


        private void loadData() {
            patientIdentificationField.setText(recordingSettings.getPatientIdentification());
            recordIdentificationField.setText(recordingSettings.getRecordingIdentification());

            int[] frequencies = recordingSettings.getChannelsFrequencies();
            String[] labels = recordingSettings.getChannelsLabels();
            boolean[] isActives = recordingSettings.getActiveChannels();

            int numberOfChannels = labels.length;

            for (int i = 0; i < numberOfChannels; i++) {
                channelsFrequenciesFields[i].setText(String.valueOf(frequencies[i]));
                channelsLabelsFields[i].setText(labels[i]);
                isChannelsActiveFields[i].setSelected(true);
                isChannelsActiveFields[i].setSelected(isActives[i]);
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
                filenameToSaveField.setText(file.getName());
                dirToSaveField.setText(file.getParent());
                setTitle(file.getName());
            }
            else{
                filenameToSaveField.setText(DEFAULT_FILENAME);
                dirToSaveField.setText(currentDir);
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
            identificationPanel.add(patientPanel, BorderLayout.NORTH);
            identificationPanel.add(recordingPanel, BorderLayout.CENTER);
            hgap = 20;
            vgap = 20;
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, hgap, vgap));
            topPanel.add(identificationPanel);


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

            hgap = 0;
            vgap = 10;
            JPanel channelsBorderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap, vgap));
            channelsBorderPanel.setBorder(BorderFactory.createTitledBorder("Channels"));
            channelsBorderPanel.add(channelsPanel);


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
                        dirToSaveField.setText(dir);
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
                        DataView dataView = controller.startDataReading(saveData());
                        View.this.setDataView(dataView);
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
            return dirToSaveField.getText();
        }

        public String getFileToSave() {
            String[] extensionList = {"bdf", "edf"};
            String filename = filenameToSaveField.getText();
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
            boolean[] isActives = new boolean[isChannelsActiveFields.length];
            for(int i = 0; i < isChannelsActiveFields.length; i++) {
                isActives[i] = isChannelsActiveFields[i].isSelected();
            }
            return isActives;
        }

        private String[] getChannelsLabels() {
            String[] labels = new String[channelsLabelsFields.length];
            for(int i = 0; i < channelsLabelsFields.length; i++) {
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

        public void showMessage(String s) {
            JOptionPane.showMessageDialog(this, s);
        }

        private class setChannelActiveListener implements ItemListener {
            private int channelNumber;

            private setChannelActiveListener(int channelNumber) {
                this.channelNumber = channelNumber;
            }

            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if(itemEvent.getStateChange() == ItemEvent.SELECTED) {
                    channelsLabelsFields[channelNumber].setEnabled(true);
                }
                else {
                    channelsLabelsFields[channelNumber].setEnabled(false);
                }
            }
        }
    }

}
