package tmp;

import data.DataSeries;
import filters.*;
import graph.GraphViewer;

import javax.swing.*;
import java.awt.*;

/**
 * Main Window of our program...
 */
public class TestView extends JFrame {
    private String title = "Dream Recorder";
    private GraphViewer graphViewer;
    private  JMenuBar menu = new JMenuBar();
    private int COMPRESSION = 50;

    public TestView() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(title);

        graphViewer = new GraphViewer();
      //  graphViewer.setPreferredSize(getWorkspaceDimention());

        graphViewer.addGraphPanel(1, true);
        graphViewer.addGraphPanel(1, true);
//        graphsViewer.addGraphPanel(1, true);

        graphViewer.addPreviewPanel(1, false);
     //   graphViewer.setCompression(COMPRESSION);

        TestData data = new TestData();
        DataSeries testData = data.getCosStream();

        DataSeries filteredData = new FilterBandPass_Alfa(testData);
//        DataStream filteredData = new FilterBandPass_Delta_1(testData);

     //   DataSeries compressedFilteredData = new CompressorMaximizing(filteredData, graphViewer.getCompression());

/*        graphsViewer.addGraph(0, testData);
        graphsViewer.addGraph(0, data.getPeriodStream());

        graphsViewer.addGraph(1, filteredData);

        graphsViewer.addPreview(0, compressedFilteredData);

        add(graphsViewer, BorderLayout.CENTER);*/

        pack();
       // setFocusable(true);
        setVisible(true);
    }

    public void syncView() {
       // graphViewer.syncView();
    }

    public void showMessage(String s) {
        JOptionPane.showMessageDialog(this, s);
    }

  /*  public void setStart(long starTime, int period_msec) {
        graphsViewer.setStart(starTime, period_msec);
    }*/

    private Dimension getWorkspaceDimention() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int width = dimension.width - 20;
        int height = dimension.height - 150;
        return new Dimension(width, height);
    }

    public static void main(String[] args) {
        TestView testWindow = new TestView();
        testWindow.syncView();
    }
}
