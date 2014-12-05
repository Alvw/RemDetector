package gui;

import data.DataSet;
import dreamrec.Controller;
import dreamrec.DataStoreListener;
import dreamrec.RecordingSettings;
import graph.GraphsView;


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public  class View extends JFrame implements DataStoreListener {
    private final String TITLE = "Dream Recorder";
    private final Color MENU_BG_COLOR = Color.GRAY;
    private final Color MENU_TEXT_COLOR = Color.BLACK;

    protected GraphsView graphsView;
    private  JMenuBar menu = new JMenuBar();
    private Controller controller;
    private boolean isStartUpdating = false;
    private String currentDirToRead = System.getProperty("user.dir"); // current working directory ("./")


    public View(Controller controller) {
        this.controller = controller;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(TITLE);
        menu.setBackground(MENU_BG_COLOR);
        menu.setForeground(MENU_TEXT_COLOR);
        menu.setBorder(BorderFactory.createEmptyBorder());
        formMenu();

        clear();
        setVisible(true);
    }

    public void clear() {
        if(graphsView != null) {
            remove(graphsView);
        }
        graphsView = new GraphsView();
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


    public void addGraphPanel(int weight, boolean isXCentered) {
       graphsView.addGraphPanel(weight, isXCentered);
       pack();
    }

    public void addPreviewPanel(int weight, boolean isXCentered) {
        graphsView.addPreviewPanel(weight, isXCentered);
        pack();
    }

    /*
     * Add Graphs to the last graph panel. If there is no graph panel create one
     */
    public void addGraphs(DataSet... graphs) {
        graphsView.addGraphs(graphs);
    }

    /*
     * Add Previews to the last preview panel. If there is no preview panel create one
     */
    public void addPreviews(DataSet... previews) {
        graphsView.addPreviews(previews);
    }

    @Override
    public void onDataStoreUpdate() {
        if(!isStartUpdating) {
            graphsView.setStart(controller.getStartTime());
        }

        graphsView.syncView();
    }

    public void setCompression(int compression) {
        graphsView.setCompression(compression);
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
                    controller.setFileBdfProvider(file);
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
               controller.setDeviceBdfProvider();
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

    public void openRecordingSettingsPreview(RecordingSettings recordingSettings) {
        new SettingsWindow(this, controller, recordingSettings);
    }

    public GraphsView getGraphsView() {
        return graphsView;
    }

}
