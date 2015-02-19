package gui;

import dreamrec.ApplicationException;
import dreamrec.InputEventHandler;
import dreamrec.RecordingSettings;
import graph.GraphViewer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class MainWindow extends JFrame {
    private final String TITLE = "Dream Recorder";
    private final Color BG_COLOR = Color.BLACK;
    private final Color MENU_BG_COLOR = Color.LIGHT_GRAY;
    private final Color MENU_TEXT_COLOR = Color.BLACK;

    protected GraphViewer graphViewer;
    private JMenuBar menu = new JMenuBar();

    private GuiConfig guiConfig;
    private InputEventHandler eventHandler;

    public MainWindow(InputEventHandler eventHandler, GuiConfig guiConfig) {
        this.eventHandler = eventHandler;
        this.guiConfig = guiConfig;

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // super.windowClosing(e);
                close();
            }
        });
        setTitle(TITLE);
        getContentPane().setBackground(BG_COLOR);
        menu.setBackground(MENU_BG_COLOR);
        menu.setForeground(MENU_TEXT_COLOR);
        menu.setBorder(BorderFactory.createEmptyBorder());
        formMenu();
        setPreferredSize(getWorkspaceDimension());
        pack();
        setVisible(true);
    }

    private void close() {
        try {
            eventHandler.stopRecording();
        } catch (ApplicationException e) {
            showMessage(e.getMessage());
        }
        System.exit(0);
    }

    public void setGraphViewer(GraphViewer graphViewer) {
        if (graphViewer != null) {
            remove(graphViewer);
        }
        graphViewer = graphViewer;
        add(graphViewer, BorderLayout.CENTER);
        graphViewer.requestFocusInWindow();
        validate();
    }

    private void showMessage(String s) {
        JOptionPane.showMessageDialog(this, s);
    }


    private Dimension getWorkspaceDimension() {
        // To get the effective screen size (the size of the screen without the taskbar and etc)
        // GraphicsEnvironment has a method which returns the maximum available size,
        // accounting all taskbars etc. no matter where they are aligned
        Rectangle dimension = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int width = dimension.width;
        int height = dimension.height;
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
                if (file != null) {
                   prepareRecord(file);
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
                prepareRecord(null);
            }
        });

        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    eventHandler.stopRecording();
                } catch (ApplicationException e) {
                    showMessage(e.getMessage());
                }

            }
        });

        add(menu, BorderLayout.NORTH);
    }

    private File chooseFileToRead() {
        String[] extensionList = {"bdf", "edf"};
        String extensionDescription = extensionList[0];
        for (int i = 1; i < extensionList.length; i++) {
            extensionDescription = extensionDescription.concat(", ").concat(extensionList[i]);
        }
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setCurrentDirectory(getDirectoryToRead());
        fileChooser.setFileFilter(new FileNameExtensionFilter(extensionDescription, extensionList));
        int fileChooserState = fileChooser.showOpenDialog(this);
        if (fileChooserState == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            setDirectoryToRead(file.getParent());
            return file;
        }
        return null;
    }

    private File getDirectoryToRead() {
        String directoryToRead = guiConfig.getDirectoryToRead();
        if(directoryToRead == null || ! new File(directoryToRead).isDirectory()) {
            directoryToRead = System.getProperty("user.dir"); // current working directory ("./");
        }
        return new File(directoryToRead);
    }

    private void setDirectoryToRead(String directoryToRead) {
        guiConfig.setDirectoryToRead(directoryToRead);
    }

// methods used by SettingsDialog

    File getDirectoryToSave() {
        String directoryToSave = guiConfig.getDirectoryToSave();
        if(directoryToSave == null || ! new File(directoryToSave).isDirectory()) {
            directoryToSave = System.getProperty("user.dir"); // current working directory ("./");
        }
        return new File(directoryToSave);
    }

    void setDirectoryToSave(String directoryToSave) {
        guiConfig.setDirectoryToSave(directoryToSave);
    }

    void startRecording(RecordingSettings recordingSettings, File file) throws ApplicationException {
        if(recordingSettings.getDirectoryToSave() != file.getParent()) {
            guiConfig.setDirectoryToSave(recordingSettings.getDirectoryToSave());
            eventHandler.startRecording(recordingSettings, file);
        }
    }

    String normalizeFilename(String filename) {
        return eventHandler.normalizeFilename(filename);
    }

    private void prepareRecord(File file) {
        try{
            RecordingSettings recordingSettings = eventHandler.getRecordingSettings(file);
            if(recordingSettings.getDirectoryToSave() == null) {
                recordingSettings.setDirectoryToSave(guiConfig.getDirectoryToSave());
            }
            new SettingsDialog(this, recordingSettings);
        } catch (ApplicationException e) {
            showMessage(e.getMessage());
        }
    }
}
