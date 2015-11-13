package fft;

import data.CompressionType;
import data.DataSeries;
import graph.GraphType;
import graph.GraphViewer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by mac on 23/02/15.
 */

public class FourierViewer extends JDialog {
    private GraphViewer graphViewer;

    public FourierViewer(DataSeries graph)  {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(getDimension());
        graphViewer = new GraphViewer(false, false);
        graphViewer.setYIndent(20);
        graphViewer.addGraphPanel(1, false);
        graphViewer.addGraph(graph);
        add(graphViewer, BorderLayout.CENTER);
        showGraph(graph);
        pack();
        setVisible(true);
    }

    public void showGraph(DataSeries graph) {
        graphViewer.removeGraphs(0);
        graphViewer.addGraph(graph, GraphType.LINE, CompressionType.AVERAGE);
    }

    private Dimension getDimension() {
        // To get the effective screen size (the size of the screen without the taskbar and etc)
        // GraphicsEnvironment has a method which returns the maximum available size,
        // accounting all taskbars etc. no matter where they are aligned
        Rectangle dimension = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int width = dimension.width - 20;
        int height = dimension.height/2;
        return new Dimension(width, height);
    }
}
