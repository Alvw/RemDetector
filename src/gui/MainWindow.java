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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class MainWindow extends JFrame {
    private final String TITLE = "Dream Recorder";
    private final Color MENU_BG_COLOR = Color.LIGHT_GRAY;
    private final Color MENU_TEXT_COLOR = Color.BLACK;

    protected GraphsView graphsView;
    private JMenuBar menu = new JMenuBar();
    private Controller controller;
    private String currentDirToRead = System.getProperty("user.dir"); // current working directory ("./")
    private String currentDirToSave = System.getProperty("user.dir"); // current working directory ("./")
    private GuiConfig guiConfig;

    public MainWindow(Controller controller, GuiConfig guiConfig) {
        this.controller = controller;
        this.guiConfig = guiConfig;
        String dirToRead = guiConfig.getDirectoryToRead();
        if(dirToRead != null && new File(dirToRead).exists()) {
            currentDirToRead = dirToRead;
        }
        String dirToSave = guiConfig.getDirectoryToSave();
        if(dirToSave != null && new File(dirToSave).exists()) {
            currentDirToSave = dirToSave;
        }
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
               // super.windowClosing(e);
                close();
            }
        });
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

    private void close() {
        try{
            guiConfig.setDirectoryToRead(currentDirToRead);
            guiConfig.setDirectoryToSave(currentDirToSave);
            controller.closeApplication();
        }catch (ApplicationException e)  {
            showMessage(e.getMessage());
        }
    }

    public void setDataView(DataView dataView) {
        if (graphsView != null) {
            remove(graphsView);
        }
        graphsView = dataView;
        graphsView.setPreferredSize(getWorkspaceDimension());
        add(graphsView, BorderLayout.CENTER);
        pack();
    }

    public void showMessage(String s) {
        JOptionPane.showMessageDialog(this, s);
    }

    public String getCurrentDirToSave() {
        return currentDirToSave;
    }

    public void setCurrentDirToSave(String currentDirToSave) {
        this.currentDirToSave = currentDirToSave;
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
                if (file != null) {
                    try {
                        RecordingSettings recordingSettings = controller.setFileBdfProvider(file);
                        new SettingsWindow(MainWindow.this, recordingSettings);
                    } catch (ApplicationException e) {
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
                    new SettingsWindow(MainWindow.this, recordingSettings);
                } catch (ApplicationException e) {
                    showMessage(e.getMessage());
                }

            }
        });

        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    controller.stopRecording();
                } catch (ApplicationException e) {
                    showMessage(e.getMessage());
                }

            }
        });

        add(menu, BorderLayout.NORTH);
    }

    public File chooseFileToRead() {
        String[] extensionList = {"bdf", "edf"};
        String extensionDescription = extensionList[0];
        for (int i = 1; i < extensionList.length; i++) {
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

    public void startReading(RecordingSettings recordingSettings) throws ApplicationException {
        DataView dataView = controller.startDataReading(recordingSettings);
        setDataView(dataView);
    }
}
