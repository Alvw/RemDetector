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
public class MainViewNew extends JFrame {
    private String title = "Dream Recorder";
    private GraphsViewer graphsViewer;
    private  JMenuBar menu = new JMenuBar();
    private ApparatModel model;
    private Controller controller;

    public MainViewNew(ApparatModel apparatModel, Controller controller) {
        model = apparatModel;
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

    private void formRecordViewer() {
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

    }





    public void syncView() {
        graphsViewer.syncView();
    }

    public void showMessage(String s) {
        JOptionPane.showMessageDialog(this, s);
    }

    public void setStart(long starTime, int period_msec) {
        graphsViewer.setStart(starTime, period_msec);
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
                controller.stopRecording();
            }
        });

        add(menu, BorderLayout.NORTH);
    }
}
