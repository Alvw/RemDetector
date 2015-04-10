package tmp;


import graph.GraphViewer;

import javax.swing.*;
import java.awt.*;

public class TestSpindleView extends JFrame {
    private String title = "Spindle Test";
    private GraphViewer graphViewer;
    private  JMenuBar menu = new JMenuBar();


    public TestSpindleView() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(title);

        graphViewer = new GraphViewer();
    //    graphViewer.setPreferredSize(getWorkspaceDimention());

        graphViewer.addGraphPanel(8, true);
        graphViewer.addGraphPanel(8, true);
        graphViewer.addPreviewPanel(1, true);


/*        DataSeries testData = new TestSpindle().getDataStream();
        DataSeries filteredData = new FilterBandPass_Alfa(testData);
        graphsViewer.addGraph(0, testData);
        graphsViewer.addGraph(1, filteredData);

        graphsViewer.addPreview(0, new CompressorMaximizing(filteredData, graphsViewer.getCompression()));

        add(graphsViewer, BorderLayout.CENTER);*/

        pack();
        setVisible(true);
    }

    public void syncView() {
       // graphViewer.syncView();
    }

    public void showMessage(String s) {
        JOptionPane.showMessageDialog(this, s);
    }

   /* public void setStart(long starTime, int period_msec) {
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
        TestSpindleView testWindow = new TestSpindleView();
        testWindow.syncView();
    }
}
