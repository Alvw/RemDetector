package tmp;


import dreamrec.Controller;
import graph.GraphViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Main Window of our program...
 */
public class GenaView extends JFrame {
    private String title = "Dream Recorder";
    private GraphViewer graphViewer;
    private  JMenuBar menu = new JMenuBar();
    private ApparatModel model;
    private Controller controller;

    public GenaView(ApparatModel apparatModel, Controller controller) {
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
                graphViewer.dispatchEvent(e); // send KeyEvent to graphsViewer
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_UP) {
                    model.movementLimitUp();
                   // graphViewer.syncView();
                }

                if (key == KeyEvent.VK_DOWN) {
                    model.movementLimitDown();
                   // graphViewer.syncView();
                }
            }
        });

        graphViewer = new GraphViewer();
        graphViewer.setPreferredSize(getWorkspaceDimention());
        add(graphViewer, BorderLayout.CENTER);

      // formGenaViewer();

        pack();
       // setFocusable(true);
        setVisible(true);
    }

 /*   private void formGenaViewer() {
        graphsViewer.addGraphPanel(4, true);
        graphsViewer.addGraphPanel(4, true);
        graphsViewer.addGraphPanel(4, true);
        graphsViewer.addGraphPanel(4, true);
        graphsViewer.addGraphPanel(4, false);

        graphsViewer.addPreviewPanel(4, false);
        graphsViewer.addPreviewPanel(4, false);
        graphsViewer.addPreviewPanel(4, false);
        graphsViewer.addPreviewPanel(2, true);

        DataSeries channel_1 = model.getCh1DataStream();
//        DataStream channel_2 = model.getCh2DataStream();
        graphsViewer.addGraph(0, new FilterHiPass(channel_1, 100));
//        graphsViewer.addGraph(0, new FilterOffset_1(channel_2, graphsViewer));

        DataSeries filteredData1 = new FilterDerivative_N(channel_1, 1);

        DataSeries filteredData2 = new FilterDerivative_N(channel_1, 2);
        DataSeries filteredDataAlfa_2 = new FilterBandPass_Alfa_2(channel_1);
        DataSeries filteredTest_3 = new FilterTest_3(filteredData2);

        graphsViewer.addGraph(1, filteredData1);
        graphsViewer.addGraph(2, filteredData2);
        graphsViewer.addGraph(3, filteredDataAlfa_2);
        graphsViewer.addGraph(4, filteredTest_3);


        graphsViewer.addPreview(0, new CompressorAveragingAbs(filteredData1, graphsViewer.getCompression()));
        graphsViewer.addPreview(1, new CompressorAveragingAbs(filteredData2, graphsViewer.getCompression()));

        DataSeries rem =  model.getSleepStream();
        DataSeries compressedRem =  new CompressorMaximizing(new FilterAbs(rem), graphsViewer.getCompression());
        graphsViewer.addPreview(2, compressedRem);

        DataSeries compressedPositionGraph = new CompressorAveraging(model.getAccPositionStream(), graphsViewer.getCompression());
        graphsViewer.addPreview(3, compressedPositionGraph);

    }


    private void formPowerViewer() {
        graphsViewer.addGraphPanel(4, true);
        graphsViewer.addGraphPanel(4, true);
        //graphsViewer.addGraphPanel(4, true);
       // graphsViewer.addGraphPanel(4, true);
       // graphsViewer.addGraphPanel(4, true);

        graphsViewer.addPreviewPanel(4, false);
       // graphsViewer.addPreviewPanel(4, false);
      //  graphsViewer.addPreviewPanel(4, false);

        DataSeries channel_1 = model.getCh1DataStream();
        DataSeries accelerationRem =  new FilterDerivativeRem(new FilterDerivativeRem(channel_1));
        DataSeries alfa_orig_1 = new FilterHiPass(new FilterBandPass_Alfa(channel_1), 2);
        DataSeries alfa_1 = new FilterAlfa(channel_1);
        DataSeries spindle = model.getSpindleStream();
        graphsViewer.addGraph(4,alfa_orig_1);

        graphsViewer.addGraph(0, new FilterOffset_1(channel_1, graphsViewer));
        graphsViewer.addGraph(1, new FilterDerivative(channel_1));
        graphsViewer.addGraph(2, alfa_1);
        graphsViewer.addGraph(3, new FilterPower(alfa_orig_1, 1));



        DataSeries compressedAccelerationRem =  new CompressorMaximizing(accelerationRem, graphsViewer.getCompression());
        graphsViewer.addPreview(0, compressedAccelerationRem);
        DataSeries compressedAlfa_1 =  new CompressorMaximizing(alfa_1, graphsViewer.getCompression());
        graphsViewer.addPreview(1, compressedAlfa_1);

        DataSeries compressedSpindle =  new CompressorMaximizing(spindle, graphsViewer.getCompression());
        graphsViewer.addPreview(2, compressedSpindle);
    }



    private void formSpindleViewer() {
        graphsViewer.addGraphPanel(4, true);
        graphsViewer.addGraphPanel(4, false);
        graphsViewer.addGraphPanel(4, false);
        graphsViewer.addGraphPanel(4, true);

        graphsViewer.addPreviewPanel(4, false);
        graphsViewer.addPreviewPanel(4, false);
        graphsViewer.addPreviewPanel(4, false);

        DataSeries channel_1 = model.getCh1DataStream();
        DataSeries channel_2 = model.getCh2DataStream();
        graphsViewer.addGraph(0, new FilterOffset_1(channel_1, graphsViewer));
        graphsViewer.addGraph(0, new FilterOffset_1(channel_2, graphsViewer));

        DataSeries alfa_orig_1 = new FilterHiPass(new FilterBandPass_Alfa(channel_1), 2);
        DataSeries alfa_1 = new FilterAlfa(channel_1);
        DataSeries spindle = model.getSpindleStream();
        graphsViewer.addGraph(1, alfa_1);
        graphsViewer.addGraph(1, new FilterThreshold(alfa_1, 25, 25));
        graphsViewer.addGraph(2,spindle);
        graphsViewer.addGraph(3,alfa_orig_1);

        DataSeries accelerationRem =  new FilterDerivativeRem(new FilterDerivativeRem(channel_1));
        DataSeries compressedAccelerationRem =  new CompressorMaximizing(accelerationRem, graphsViewer.getCompression());
        graphsViewer.addPreview(0, compressedAccelerationRem);
        DataSeries compressedAlfa_1 =  new CompressorMaximizing(alfa_1, graphsViewer.getCompression());
        graphsViewer.addPreview(1, compressedAlfa_1);

        DataSeries compressedSpindle =  new CompressorMaximizing(spindle, graphsViewer.getCompression());
        graphsViewer.addPreview(2, compressedSpindle);

    }


    private void formGalaViewer() {
        graphsViewer.addGraphPanel(2, true);
       // graphsViewer.addGraphPanel(2, true);
        graphsViewer.addGraphPanel(2, true);
        graphsViewer.addGraphPanel(2, false);
        graphsViewer.addGraphPanel(2, false);

        graphsViewer.addPreviewPanel(2, false);
        graphsViewer.addPreviewPanel(2, false);
        graphsViewer.addPreviewPanel(2, false);

        DataSeries channel_1 = model.getCh2DataStream();
        DataSeries rem = model.getSleepStream();
        DataSeries velocity =  new FilterAbs(new FilterDerivative(channel_1));
        DataSeries acceleration =  new FilterDerivative(velocity);

        DataSeries velocityRem =  new FilterAbs(new FilterDerivativeRem(channel_1));
        DataSeries velocityThreshold =  new FilterThreshold(velocity, 10);
        DataSeries velocityRemThreshold =  new FilterThreshold(velocityRem, 10);

        DataSeries accelerationRem =  new FilterDerivativeRem(new FilterDerivativeRem(channel_1));
        DataSeries accelerationThreshold =  new FilterThreshold(acceleration, 10);

        graphsViewer.addGraph(0, new FilterOffset_1(channel_1, graphsViewer));

       // graphsViewer.addGraph(2, acceleration);
       // graphsViewer.addGraph(1, accelerationRem);
        graphsViewer.addGraph(1, rem);
        graphsViewer.addGraph(2, velocityRem);
      //  graphsViewer.addGraph(2, velocityRemThreshold);
        graphsViewer.addGraph(3, velocity);
      //  graphsViewer.addGraph(3, velocityThreshold);


        DataSeries compressedDreamGraph = new CompressorAveraging(new FilterDerivativeAbs(channel_1), graphsViewer.getCompression());
       // graphsViewer.addPreview(0, compressedDreamGraph);
        DataSeries compressedRem =  new CompressorMaximizing(new FilterAbs(rem), graphsViewer.getCompression());
        DataSeries compressedVelocityRem =  new CompressorMaximizing(velocityRem, graphsViewer.getCompression());
       // graphsViewer.addPreview(1, compressedVelocityRem);
        DataSeries compressedVelocityThresholdRem =  new CompressorMaximizing(new FilterThreshold_mod(velocityRem), graphsViewer.getCompression());
        DataSeries compressedAccelerationRem =  new CompressorMaximizing(accelerationRem, graphsViewer.getCompression());
        DataSeries compressedAcceleration =  new CompressorMaximizing(acceleration, graphsViewer.getCompression());

        graphsViewer.addPreview(2, compressedDreamGraph);
        graphsViewer.addPreview(0, compressedAccelerationRem);
        graphsViewer.addPreview(1, compressedRem);
    }

    public void syncView() {
        graphsViewer.syncView();
    }

    public void showMessage(String s) {
        JOptionPane.showMessageDialog(this, s);
    }

    /* public void setStart(long starTime, int period_msec) {
        graphsViewer.setStart(starTime, period_msec);
    }
*/
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
               // controller.readFromFile();
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
