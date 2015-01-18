package graph;

import data.DataDimension;
import data.DataSet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;

/**
 * Created with IntelliJ IDEA.
 * User: GENA
 * Date: 08.05.14
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
public class GraphPanel extends JPanel {
    protected static final double ZOOM_PLUS_CHANGE = Math.sqrt(2.0);// 2 clicks(rotations) up increase zoom twice
    protected static final double ZOOM_MINUS_CHANGE = 1 / ZOOM_PLUS_CHANGE; // similarly 2 clicks(rotations) down reduces zoom twice
    protected static final Color bgColor = Color.BLACK;
    protected static final Color graphColors[] = {Color.YELLOW, Color.RED, Color.CYAN};

    protected int panelNumber;
    protected double zoom = 0.5;
    protected boolean isAutoZoom;
    protected boolean isXCentered = true;
    protected int weight = 1;
    protected GraphsData graphsData;

    GraphPanel(int weight, boolean isXCentered, int panelNumber, GraphsData graphsData) {
        this.weight = weight;
        this.isXCentered = isXCentered;
        this.graphsData = graphsData;
        this.panelNumber = panelNumber;
        setBackground(bgColor);
        // MouseListener to zoom Y_Axes
        addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                zooming(e.getWheelRotation());
            }
        });
    }

    protected java.util.List<? extends DataSet> getGraphs() {
        return graphsData.getGraphList(panelNumber);
    }

    protected DataDimension getDataDimension() {
        return getGraphs().get(0).getDataDimension();
    }

    protected int getWeight() {
        return weight;
    }

    protected int getWorkspaceWidth() {
        return (getSize().width - graphsData.X_INDENT);
    }

    protected void zooming(int zoomDirection) {
        if (zoomDirection > 0) {
            zoom = zoom * ZOOM_PLUS_CHANGE;
        } else {
            zoom = zoom * ZOOM_MINUS_CHANGE;
        }
        repaint();
    }

    protected int getMaxY() {
        if (isXCentered) {
            return getSize().height / 2;
        }
        return (getSize().height - graphsData.Y_INDENT);
    }


    protected void transformCoordinate(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(graphsData.X_INDENT, getMaxY()); // move XY origin to the left bottom point
        g2d.transform(AffineTransform.getScaleInstance(1, -1)); // flip Y-axis
    }

    protected void restoreCoordinate(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(-graphsData.X_INDENT, getMaxY()); // move XY origin to the left top point
        g2d.transform(AffineTransform.getScaleInstance(1, -1)); // flip Y-axis and zoom it
    }


    protected int getStartIndex() {
        return graphsData.getStartIndex();
    }

    protected double getTimeFrequency() {
        return graphsData.getTimeFrequency();
    }

    protected long getStartTime() {
        return graphsData.getStartTime();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);    //To change body of overridden methods use File | Settings | File Templates.
        transformCoordinate(g);

        YAxisPainter.paint(g, zoom, getDataDimension(), isXCentered);
        TimeAxisPainter.paint(g, getStartTime(), getStartIndex(), getTimeFrequency());

        int graph_number = 0;
        for (DataSet graph : getGraphs()) {
            Color graphColor = graphColors[graph_number % graphColors.length];
            graph_number++;
            g.setColor(graphColor);
            GraphPainter.paint(g, getWorkspaceWidth(), zoom, getStartIndex(), graph);
        }

        restoreCoordinate(g);
    }
}



