package gui;

import bdf.RecordingBdfConfig;
import dreamrec.Controller;
import dreamrec.DataStore;
import dreamrec.DataStoreListener;
import dreamrec.RecordingSettings;
import graph.GraphsViewer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public abstract class View extends JFrame implements DataStoreListener {
    private String title = "Dream Recorder";
    private  JMenuBar menu = new JMenuBar();
    private Controller controller;
    private boolean isStartUpdating = false;
    private Color MENU_BG_COLOR = Color.GRAY;
    private Color MENU_TEXT_COLOR = Color.BLACK;

    protected DataStore model;
    protected GraphsViewer graphsViewer;


    public View(Controller controller) {
        this.controller = controller;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(title);
        menu.setBackground(MENU_BG_COLOR);
        menu.setForeground(MENU_TEXT_COLOR);
        menu.setBorder(BorderFactory.createEmptyBorder());
        formMenu();

        graphsViewer = new GraphsViewer();
        graphsViewer.setPreferredSize(getWorkspaceDimension());
        add(graphsViewer, BorderLayout.CENTER);

        pack();
        setVisible(true);
    }


    public void showMessage(String s) {
        JOptionPane.showMessageDialog(this, s);
    }

    public void setDataStore(DataStore dataStore)  {
        model = dataStore;
        dataStore.addListener(this);
        addGraphs();
        pack();
    }

    protected abstract void addGraphs();

    @Override
    public void onDataStoreUpdate() {
        if(!isStartUpdating) {
            graphsViewer.setStart(model.getStartTime());
            isStartUpdating = true;
        }

        graphsViewer.syncView();
    }

    public void setCompression(int compression) {
        graphsViewer.setCompression(compression);
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
            public void actionPerformed(ActionEvent e) {
                File file = chooseFileToRead();
                if(file != null) {
                    controller.setBdfProvider(file);
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
            public void actionPerformed(ActionEvent e) {
               // controller.setBdfProvider();
            }
        });

        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.stopRecording();
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
        String currentDir = getCurrentDirToRead();
        if(currentDir == null || !(new File(currentDir).exists())) {
            currentDir = System.getProperty("user.dir"); // current working directory ("./")
        }
        fileChooser.setCurrentDirectory(new File(currentDir));
        fileChooser.setFileFilter(new FileNameExtensionFilter(extensionDescription, extensionList));
        int fileChooserState = fileChooser.showOpenDialog(this);
        if (fileChooserState == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            setCurrentDirToRead(file.getParent());
            return file;
        }
        return null;
    }

    public void openRecordingSettingsPreview(RecordingSettings recordingSettings) {
        new SettingsWindow(this, controller, recordingSettings);
    }


    public String getCurrentDirToRead() {
        return null;
    }

    public String getCurrentDirToSave() {
        return controller.getCurrentDirToSave();
    }

    public void setCurrentDirToRead(String dir) {
        //controller.setCurrentDirToRead(dir);
    }

    public void setCurrentDirToSave(String dir) {
        controller.setCurrentDirToSave(dir);
    }
}