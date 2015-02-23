package fft;

import data.DataSet;
import graph.GraphViewer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by mac on 23/02/15.
 */

public class FourieView extends JFrame {
    protected GraphViewer graphViewer;

    public FourieView(DataSet graph)  {
        graphViewer = new GraphViewer(false, false);
        graphViewer.setyIndent(20);
        add(graphViewer, BorderLayout.CENTER);
        graphViewer.requestFocusInWindow();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(500, 500));
        graphViewer.addGraphPanel(1, false);
        graphViewer.addGraph(graph);
        pack();
        setVisible(true);
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
