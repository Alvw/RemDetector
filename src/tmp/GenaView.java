package tmp;

import dreamrec.Controller;
import graph.GraphsView;

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
    private GraphsView graphsView;
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
                graphsView.dispatchEvent(e); // send KeyEvent to graphsViewer
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_UP) {
                    model.movementLimitUp();
                   // graphsView.syncView();
                }

                if (key == KeyEvent.VK_DOWN) {
                    model.movementLimitDown();
                   // graphsView.syncView();
                }
            }
        });

        graphsView = new GraphsView();
        graphsView.setPreferredSize(getWorkspaceDimention());
        add(graphsView, BorderLayout.CENTER);

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

        DataSet channel_1 = model.getCh1DataStream();
//        DataStream channel_2 = model.getCh2DataStream();
        graphsViewer.addGraphs(0, new FilterHiPass(channel_1, 100));
//        graphsViewer.addGraphs(0, new FilterOffset_1(channel_2, graphsViewer));

        DataSet filteredData1 = new FilterDerivative_N(channel_1, 1);

        DataSet filteredData2 = new FilterDerivative_N(channel_1, 2);
        DataSet filteredDataAlfa_2 = new FilterBandPass_Alfa_2(channel_1);
        DataSet filteredTest_3 = new FilterTest_3(filteredData2);

        graphsViewer.addGraphs(1, filteredData1);
        graphsViewer.addGraphs(2, filteredData2);
        graphsViewer.addGraphs(3, filteredDataAlfa_2);
        graphsViewer.addGraphs(4, filteredTest_3);


        graphsViewer.addPreviews(0, new CompressorAveragingAbs(filteredData1, graphsViewer.getCompression()));
        graphsViewer.addPreviews(1, new CompressorAveragingAbs(filteredData2, graphsViewer.getCompression()));

        DataSet rem =  model.getSleepStream();
        DataSet compressedRem =  new CompressorMaximizing(new FilterAbs(rem), graphsViewer.getCompression());
        graphsViewer.addPreviews(2, compressedRem);

        DataSet compressedPositionGraph = new CompressorAveraging(model.getAccPositionStream(), graphsViewer.getCompression());
        graphsViewer.addPreviews(3, compressedPositionGraph);

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

        DataSet channel_1 = model.getCh1DataStream();
        DataSet accelerationRem =  new FilterDerivativeRem(new FilterDerivativeRem(channel_1));
        DataSet alfa_orig_1 = new FilterHiPass(new FilterBandPass_Alfa(channel_1), 2);
        DataSet alfa_1 = new FilterAlfa(channel_1);
        DataSet spindle = model.getSpindleStream();
        graphsViewer.addGraphs(4,alfa_orig_1);

        graphsViewer.addGraphs(0, new FilterOffset_1(channel_1, graphsViewer));
        graphsViewer.addGraphs(1, new FilterDerivative(channel_1));
        graphsViewer.addGraphs(2, alfa_1);
        graphsViewer.addGraphs(3, new FilterPower(alfa_orig_1, 1));



        DataSet compressedAccelerationRem =  new CompressorMaximizing(accelerationRem, graphsViewer.getCompression());
        graphsViewer.addPreviews(0, compressedAccelerationRem);
        DataSet compressedAlfa_1 =  new CompressorMaximizing(alfa_1, graphsViewer.getCompression());
        graphsViewer.addPreviews(1, compressedAlfa_1);

        DataSet compressedSpindle =  new CompressorMaximizing(spindle, graphsViewer.getCompression());
        graphsViewer.addPreviews(2, compressedSpindle);
    }



    private void formSpindleViewer() {
        graphsViewer.addGraphPanel(4, true);
        graphsViewer.addGraphPanel(4, false);
        graphsViewer.addGraphPanel(4, false);
        graphsViewer.addGraphPanel(4, true);

        graphsViewer.addPreviewPanel(4, false);
        graphsViewer.addPreviewPanel(4, false);
        graphsViewer.addPreviewPanel(4, false);

        DataSet channel_1 = model.getCh1DataStream();
        DataSet channel_2 = model.getCh2DataStream();
        graphsViewer.addGraphs(0, new FilterOffset_1(channel_1, graphsViewer));
        graphsViewer.addGraphs(0, new FilterOffset_1(channel_2, graphsViewer));

        DataSet alfa_orig_1 = new FilterHiPass(new FilterBandPass_Alfa(channel_1), 2);
        DataSet alfa_1 = new FilterAlfa(channel_1);
        DataSet spindle = model.getSpindleStream();
        graphsViewer.addGraphs(1, alfa_1);
        graphsViewer.addGraphs(1, new FilterThreshold(alfa_1, 25, 25));
        graphsViewer.addGraphs(2,spindle);
        graphsViewer.addGraphs(3,alfa_orig_1);

        DataSet accelerationRem =  new FilterDerivativeRem(new FilterDerivativeRem(channel_1));
        DataSet compressedAccelerationRem =  new CompressorMaximizing(accelerationRem, graphsViewer.getCompression());
        graphsViewer.addPreviews(0, compressedAccelerationRem);
        DataSet compressedAlfa_1 =  new CompressorMaximizing(alfa_1, graphsViewer.getCompression());
        graphsViewer.addPreviews(1, compressedAlfa_1);

        DataSet compressedSpindle =  new CompressorMaximizing(spindle, graphsViewer.getCompression());
        graphsViewer.addPreviews(2, compressedSpindle);

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

        DataSet channel_1 = model.getCh2DataStream();
        DataSet rem = model.getSleepStream();
        DataSet velocity =  new FilterAbs(new FilterDerivative(channel_1));
        DataSet acceleration =  new FilterDerivative(velocity);

        DataSet velocityRem =  new FilterAbs(new FilterDerivativeRem(channel_1));
        DataSet velocityThreshold =  new FilterThreshold(velocity, 10);
        DataSet velocityRemThreshold =  new FilterThreshold(velocityRem, 10);

        DataSet accelerationRem =  new FilterDerivativeRem(new FilterDerivativeRem(channel_1));
        DataSet accelerationThreshold =  new FilterThreshold(acceleration, 10);

        graphsViewer.addGraphs(0, new FilterOffset_1(channel_1, graphsViewer));

       // graphsViewer.addGraphs(2, acceleration);
       // graphsViewer.addGraphs(1, accelerationRem);
        graphsViewer.addGraphs(1, rem);
        graphsViewer.addGraphs(2, velocityRem);
      //  graphsViewer.addGraphs(2, velocityRemThreshold);
        graphsViewer.addGraphs(3, velocity);
      //  graphsViewer.addGraphs(3, velocityThreshold);


        DataSet compressedDreamGraph = new CompressorAveraging(new FilterDerivativeAbs(channel_1), graphsViewer.getCompression());
       // graphsViewer.addPreviews(0, compressedDreamGraph);
        DataSet compressedRem =  new CompressorMaximizing(new FilterAbs(rem), graphsViewer.getCompression());
        DataSet compressedVelocityRem =  new CompressorMaximizing(velocityRem, graphsViewer.getCompression());
       // graphsViewer.addPreviews(1, compressedVelocityRem);
        DataSet compressedVelocityThresholdRem =  new CompressorMaximizing(new FilterThreshold_mod(velocityRem), graphsViewer.getCompression());
        DataSet compressedAccelerationRem =  new CompressorMaximizing(accelerationRem, graphsViewer.getCompression());
        DataSet compressedAcceleration =  new CompressorMaximizing(acceleration, graphsViewer.getCompression());

        graphsViewer.addPreviews(2, compressedDreamGraph);
        graphsViewer.addPreviews(0, compressedAccelerationRem);
        graphsViewer.addPreviews(1, compressedRem);
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
