package dreamrec;

import data.DataStream;
import filters.*;
import graph.GraphsViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Main Window of our program...
 */
public class MainView extends JFrame {
    private String title = "Dream Recorder";
    private GraphsViewer graphsViewer;
    private  JMenuBar menu = new JMenuBar();
    private ApparatModel model;
    private Controller controller;

    public MainView(ApparatModel apparatModel, Controller controller) {
        model = apparatModel;
        this.controller = controller;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(title);

        formMenu();

        // Key Listener to change MovementLimit in model
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                graphsViewer.dispatchEvent(e); // send KeyEvent to graphsViewer
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_UP) {
                    model.movementLimitUp();
                    graphsViewer.syncView();
                }

                if (key == KeyEvent.VK_DOWN) {
                    model.movementLimitDown();
                    graphsViewer.syncView();
                }
            }
        });

        graphsViewer = new GraphsViewer();
        graphsViewer.setPreferredSize(getWorkspaceDimention());
        add(graphsViewer, BorderLayout.CENTER);

        formGenaViewer();

        pack();
       // setFocusable(true);
        setVisible(true);
    }

    private void formGenaViewer() {
        graphsViewer.addGraphPanel(4, true);
        graphsViewer.addGraphPanel(4, true);
        graphsViewer.addGraphPanel(4, true);
        graphsViewer.addGraphPanel(4, true);
        graphsViewer.addGraphPanel(4, false);

        graphsViewer.addCompressedGraphPanel(4, false);
        graphsViewer.addCompressedGraphPanel(4, false);
        graphsViewer.addCompressedGraphPanel(4, false);
        graphsViewer.addCompressedGraphPanel(2, true);

        DataStream channel_1 = model.getCh1DataStream();
//        DataStream channel_2 = model.getCh2DataStream();
        graphsViewer.addGraph(0, new FilterHiPass(channel_1, 100));
//        graphsViewer.addGraph(0, new FilterOffset_1(channel_2, graphsViewer));

        DataStream filteredData1 = new FilterDerivative_N(channel_1, 1);

        DataStream filteredData2 = new FilterDerivative_N(channel_1, 2);
        DataStream filteredDataAlfa_2 = new FilterBandPass_Alfa_2(channel_1);
        DataStream filteredTest_3 = new FilterTest_3(filteredData2);

        graphsViewer.addGraph(1, filteredData1);
        graphsViewer.addGraph(2, filteredData2);
        graphsViewer.addGraph(3, filteredDataAlfa_2);
        graphsViewer.addGraph(4, filteredTest_3);


        graphsViewer.addCompressedGraph(0, new CompressorAveragingAbs(filteredData1, graphsViewer.getCompression()));
        graphsViewer.addCompressedGraph(1, new CompressorAveragingAbs(filteredData2, graphsViewer.getCompression()));

        DataStream rem =  model.getSleepStream();
        DataStream compressedRem =  new CompressorMaximizing(new FilterAbs(rem), graphsViewer.getCompression());
        graphsViewer.addCompressedGraph(2, compressedRem);

        DataStream compressedPositionGraph = new CompressorAveraging(model.getAccPositionStream(), graphsViewer.getCompression());
        graphsViewer.addCompressedGraph(3, compressedPositionGraph);

    }


    private void formPowerViewer() {
        graphsViewer.addGraphPanel(4, true);
        graphsViewer.addGraphPanel(4, true);
        //graphsViewer.addGraphPanel(4, true);
       // graphsViewer.addGraphPanel(4, true);
       // graphsViewer.addGraphPanel(4, true);

        graphsViewer.addCompressedGraphPanel(4, false);
       // graphsViewer.addCompressedGraphPanel(4, false);
      //  graphsViewer.addCompressedGraphPanel(4, false);

        DataStream channel_1 = model.getCh1DataStream();
        DataStream accelerationRem =  new FilterDerivativeRem(new FilterDerivativeRem(channel_1));
        DataStream alfa_orig_1 = new FilterHiPass(new FilterBandPass_Alfa(channel_1), 2);
        DataStream alfa_1 = new FilterAlfa(channel_1);
        DataStream spindle = model.getSpindleStream();
        graphsViewer.addGraph(4,alfa_orig_1);

        graphsViewer.addGraph(0, new FilterOffset_1(channel_1, graphsViewer));
        graphsViewer.addGraph(1, new FilterDerivative(channel_1));
        graphsViewer.addGraph(2, alfa_1);
        graphsViewer.addGraph(3, new FilterPower(alfa_orig_1, 1));



        DataStream compressedAccelerationRem =  new CompressorMaximizing(accelerationRem, graphsViewer.getCompression());
        graphsViewer.addCompressedGraph(0, compressedAccelerationRem);
        DataStream compressedAlfa_1 =  new CompressorMaximizing(alfa_1, graphsViewer.getCompression());
        graphsViewer.addCompressedGraph(1, compressedAlfa_1);

        DataStream compressedSpindle =  new CompressorMaximizing(spindle, graphsViewer.getCompression());
        graphsViewer.addCompressedGraph(2, compressedSpindle);
    }



    private void formSpindleViewer() {
        graphsViewer.addGraphPanel(4, true);
        graphsViewer.addGraphPanel(4, false);
        graphsViewer.addGraphPanel(4, false);
        graphsViewer.addGraphPanel(4, true);

        graphsViewer.addCompressedGraphPanel(4, false);
        graphsViewer.addCompressedGraphPanel(4, false);
        graphsViewer.addCompressedGraphPanel(4, false);

        DataStream channel_1 = model.getCh1DataStream();
        DataStream channel_2 = model.getCh2DataStream();
        graphsViewer.addGraph(0, new FilterOffset_1(channel_1, graphsViewer));
        graphsViewer.addGraph(0, new FilterOffset_1(channel_2, graphsViewer));

        DataStream alfa_orig_1 = new FilterHiPass(new FilterBandPass_Alfa(channel_1), 2);
        DataStream alfa_1 = new FilterAlfa(channel_1);
        DataStream spindle = model.getSpindleStream();
        graphsViewer.addGraph(1, alfa_1);
        graphsViewer.addGraph(1, new FilterThreshold(alfa_1, 25, 25));
        graphsViewer.addGraph(2,spindle);
        graphsViewer.addGraph(3,alfa_orig_1);

        DataStream accelerationRem =  new FilterDerivativeRem(new FilterDerivativeRem(channel_1));
        DataStream compressedAccelerationRem =  new CompressorMaximizing(accelerationRem, graphsViewer.getCompression());
        graphsViewer.addCompressedGraph(0, compressedAccelerationRem);
        DataStream compressedAlfa_1 =  new CompressorMaximizing(alfa_1, graphsViewer.getCompression());
        graphsViewer.addCompressedGraph(1, compressedAlfa_1);

        DataStream compressedSpindle =  new CompressorMaximizing(spindle, graphsViewer.getCompression());
        graphsViewer.addCompressedGraph(2, compressedSpindle);

    }


    private void formGalaViewer() {
        graphsViewer.addGraphPanel(2, true);
       // graphsViewer.addGraphPanel(2, true);
        graphsViewer.addGraphPanel(2, true);
        graphsViewer.addGraphPanel(2, false);
        graphsViewer.addGraphPanel(2, false);

        graphsViewer.addCompressedGraphPanel(2, false);
        graphsViewer.addCompressedGraphPanel(2, false);
        graphsViewer.addCompressedGraphPanel(2, false);

        DataStream channel_1 = model.getCh2DataStream();
        DataStream rem = model.getSleepStream();
        DataStream velocity =  new FilterAbs(new FilterDerivative(channel_1));
        DataStream acceleration =  new FilterDerivative(velocity);

        DataStream velocityRem =  new FilterAbs(new FilterDerivativeRem(channel_1));
        DataStream velocityThreshold =  new FilterThreshold(velocity, 10);
        DataStream velocityRemThreshold =  new FilterThreshold(velocityRem, 10);

        DataStream accelerationRem =  new FilterDerivativeRem(new FilterDerivativeRem(channel_1));
        DataStream accelerationThreshold =  new FilterThreshold(acceleration, 10);

        graphsViewer.addGraph(0, new FilterOffset_1(channel_1, graphsViewer));

       // graphsViewer.addGraph(2, acceleration);
       // graphsViewer.addGraph(1, accelerationRem);
        graphsViewer.addGraph(1, rem);
        graphsViewer.addGraph(2, velocityRem);
      //  graphsViewer.addGraph(2, velocityRemThreshold);
        graphsViewer.addGraph(3, velocity);
      //  graphsViewer.addGraph(3, velocityThreshold);


        DataStream compressedDreamGraph = new CompressorAveraging(new FilterDerivativeAbs(channel_1), graphsViewer.getCompression());
       // graphsViewer.addCompressedGraph(0, compressedDreamGraph);
        DataStream compressedRem =  new CompressorMaximizing(new FilterAbs(rem), graphsViewer.getCompression());
        DataStream compressedVelocityRem =  new CompressorMaximizing(velocityRem, graphsViewer.getCompression());
       // graphsViewer.addCompressedGraph(1, compressedVelocityRem);
        DataStream compressedVelocityThresholdRem =  new CompressorMaximizing(new FilterThreshold_mod(velocityRem), graphsViewer.getCompression());
        DataStream compressedAccelerationRem =  new CompressorMaximizing(accelerationRem, graphsViewer.getCompression());
        DataStream compressedAcceleration =  new CompressorMaximizing(acceleration, graphsViewer.getCompression());

        graphsViewer.addCompressedGraph(2, compressedDreamGraph);
        graphsViewer.addCompressedGraph(0, compressedAccelerationRem);
        graphsViewer.addCompressedGraph(1, compressedRem);
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

        add(menu, BorderLayout.NORTH);
    }
}
