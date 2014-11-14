package tmp;

import data.DataSet;
import filters.*;
import graph.GraphsViewer;

import javax.swing.*;
import java.awt.*;

/**
 * Main Window of our program...
 */
public class TestView extends JFrame {
    private String title = "Dream Recorder";
    private GraphsViewer graphsViewer;
    private  JMenuBar menu = new JMenuBar();
    private int COMPRESSION = 50;

    public TestView() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(title);

        graphsViewer = new GraphsViewer();
        graphsViewer.setPreferredSize(getWorkspaceDimention());

        graphsViewer.addGraphPanel(1, true);
        graphsViewer.addGraphPanel(1, true);
//        graphsViewer.addGraphPanel(1, true);

        graphsViewer.addPreviewPanel(1, false);
        graphsViewer.setCompression(COMPRESSION);

        TestData data = new TestData();
        DataSet testData = data.getCosStream();

        DataSet filteredData = new FilterBandPass_Alfa(testData);
//        DataStream filteredData = new FilterBandPass_Delta_1(testData);

        DataSet compressedFilteredData = new CompressorMaximizing(filteredData, graphsViewer.getCompression());

/*        graphsViewer.addGraphs(0, testData);
        graphsViewer.addGraphs(0, data.getPeriodStream());

        graphsViewer.addGraphs(1, filteredData);

        graphsViewer.addPreviews(0, compressedFilteredData);

        add(graphsViewer, BorderLayout.CENTER);*/

        pack();
       // setFocusable(true);
        setVisible(true);
    }

    public void syncView() {
        graphsViewer.syncView();
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
