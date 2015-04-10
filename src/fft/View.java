package fft;

import data.DataSeries;
import graph.GraphViewer;

import javax.swing.*;
import java.awt.*;

public class View extends JFrame {
    protected GraphViewer graphViewer;

    public View()  {
        graphViewer = new GraphViewer(false, false, false, false);
        add(graphViewer, BorderLayout.CENTER);
        graphViewer.requestFocusInWindow();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(getWorkspaceDimension());
        pack();
        setVisible(true);
    }

    public void addGraph(DataSeries graph) {
        graphViewer.addGraphPanel(1, true);
        graphViewer.addGraph(graph);
    }

    private Dimension getWorkspaceDimension() {
        // To get the effective screen size (the size of the screen without the taskbar and etc)
        // GraphicsEnvironment has a method which returns the maximum available size,
        // accounting all taskbars etc. no matter where they are aligned
        Rectangle dimension = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int width = dimension.width;
        int height = dimension.height;
        return new Dimension(width, height);
    }
}
