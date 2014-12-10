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
import java.io.File;

public class MainWindow extends JFrame {
    private final String TITLE = "Dream Recorder";
    private final Color MENU_BG_COLOR = Color.LIGHT_GRAY;
    private final Color MENU_TEXT_COLOR = Color.BLACK;

    protected GraphsView graphsView;
    private JMenuBar menu = new JMenuBar();
    private Controller controller;
    private String currentDirToRead = System.getProperty("user.dir"); // current working directory ("./")


    public MainWindow(Controller controller) {
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
        if (graphsView != null) {
            remove(graphsView);
        }
        graphsView = dataView;
        graphsView.setPreferredSize(getWorkspaceDimension());
        add(graphsView, BorderLayout.CENTER);
        pack();
    }

    public void setCurrentDirToRead(String currentDirToRead) {
        if (currentDirToRead != null && new File(currentDirToRead).exists()) {
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
