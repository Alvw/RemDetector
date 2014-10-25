package dreamrec;

import data.DataStream;
import filters.*;
import graph.GraphsViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main Window of our program...
 */
public class MainView extends View implements DataStoreListener{
    private String title = "Dream Recorder";
    private GraphsViewer graphsViewer;
    private  JMenuBar menu = new JMenuBar();
    private DataStore model;
    private Controller controller;
    private boolean isStartUpdating = false;

    public MainView(Controller controller) {
        this.controller = controller;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(title);

        formMenu();

        graphsViewer = new GraphsViewer();
        graphsViewer.setPreferredSize(getWorkspaceDimention());
        add(graphsViewer, BorderLayout.CENTER);

        formRecordViewer();

        pack();
        setVisible(true);
    }

    public void setDataStore(DataStore dataStore)  {
        model = dataStore;
        dataStore.addListener(this);
        addGraphs();
    }

 /*   private void formRecordViewer() {
        graphsViewer.addGraphPanel(1, true);
        graphsViewer.addGraphPanel(1, true);
        graphsViewer.addGraphPanel(1, true);
         graphsViewer.addGraphPanel(1, false);

        graphsViewer.addCompressedGraphPanel(1, false);

        DataStream channel_1 = model.getCh1DataStream();
        DataStream alfa = new FilterBandPass_Alfa_2(channel_1);

       // graphsViewer.addGraph(0, new FilterOffset_1(channel_1, graphsViewer));
       //  graphsViewer.addGraph(0, new FilterHiPass(channel_1, 100));
         graphsViewer.addGraph(0, new FilterFirst(channel_1, graphsViewer));
         graphsViewer.addGraph(1, new FilterDerivative(channel_1));
         graphsViewer.addGraph(2, alfa);
         graphsViewer.addGraph(3, model.getAcc1DataStream());
        graphsViewer.addGraph(3, model.getAcc2DataStream());
        graphsViewer.addGraph(3, model.getAcc3DataStream());


        DataStream velocityRem =  new FilterAbs(new FilterDerivativeRem(channel_1));
        DataStream compressedVelocityRem =  new CompressorMaximizing(velocityRem, graphsViewer.getCompression());
        graphsViewer.addCompressedGraph(0, compressedVelocityRem);

        DataStream compressedPositionGraph = new CompressorAveraging(model.getAccPositionStream(), graphsViewer.getCompression());
        graphsViewer.addCompressedGraph(1, compressedPositionGraph);

    } */

    private void formRecordViewer() {
        graphsViewer.addGraphPanel(1, true);
        graphsViewer.addGraphPanel(1, true);


        graphsViewer.addCompressedGraphPanel(1, false);
    }

    private void addGraphs() {
        DataStream channel_1 = model.getSignalData(0);
        DataStream channel_2 = model.getSignalData(1);

        // graphsViewer.addGraph(0, new FilterOffset_1(channel_1, graphsViewer));
        //  graphsViewer.addGraph(0, new FilterHiPass(channel_1, 100));
        graphsViewer.addGraph(0, new FilterOffset_1(channel_1, graphsViewer));
        graphsViewer.addGraph(1, new FilterDerivative(channel_1));




        DataStream velocityRem =  new FilterAbs(new FilterDerivativeRem(channel_1));
        DataStream compressedVelocityRem =  new CompressorMaximizing(velocityRem, graphsViewer.getCompression());
        graphsViewer.addCompressedGraph(0, compressedVelocityRem);
    }


    @Override
    public void onDataStoreUpdate() {
        if(!isStartUpdating) {
            graphsViewer.setStart(model.getStartTime(), model.getSignalFrequency(0)/10);
            isStartUpdating = true;
        }

        graphsViewer.syncView();
    }


    public void showMessage(String s) {
        JOptionPane.showMessageDialog(this, s);
    }


    private Dimension getWorkspaceDimention() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int width = dimension.width - 20;
        int height = dimension.height - 150;
        return new Dimension(width, height);
    }


    private void formMenu() {

        JMenu fileMenu = new JMenu("File");
        menu.add(fileMenu);
        JMenuItem open = new JMenuItem("Open");
        fileMenu.add(open);

        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.readFromFile();
            }
        });


        JMenu recordMenu = new JMenu("Record");
        menu.add(recordMenu);
        JMenuItem start = new JMenuItem("Start");
        JMenuItem stop = new JMenuItem("Stop");
        recordMenu.add(start);
        recordMenu.add(stop);

        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.startRecording();
            }
        });

        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller     .stopRecording();
            }
        });

        add(menu, BorderLayout.NORTH);
    }
}
