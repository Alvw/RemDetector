package graph;

import data.DataDimension;
import data.DataSet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: GENA
 * Date: 08.05.14
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
class GraphPanel extends JPanel {
    private static final double ZOOM_PLUS_CHANGE = Math.sqrt(2.0);// 2 clicks(rotations) up increase zoom twice
    private static final double ZOOM_MINUS_CHANGE = 1 / ZOOM_PLUS_CHANGE; // similarly 2 clicks(rotations) down reduces zoom twice

    private static final Color DEFAULT_BG_COLOR = Color.black;

    private Color graphColors[] = {Color.YELLOW, Color.RED, Color.CYAN};
    private Color slotColor = Color.MAGENTA;
    private java.util.List<SlotListener> slotListeners = new ArrayList<SlotListener>();

    private List<DataSet> graphList = new ArrayList<DataSet>();
    private double zoom = 0.5;
    private boolean isXCentered = true;
    private int weight = 1;
    private int startIndex;
    private int slotWidth;
    private int slotPosition;
    private int indentX;
    private int indentY;
    private TimeAxisPainter timeAxisPainter = new TimeAxisPainter();


    GraphPanel(int weight,  boolean isXCentered) {
        this.isXCentered = isXCentered;
        this.weight = weight;
        setBackground(DEFAULT_BG_COLOR);

        // MouseListener to zoom Y_Axes
        addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                zooming(e.getWheelRotation());
            }
        });

        //MouseListener to move Slot
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int slotPosition = e.getX() - indentX;
                notifySlotListeners(slotPosition);
            }
        });
    }


    void setGraphs(List<DataSet> graphs) {
        graphList = graphs;
    }

    void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    void setSlotWidth(int slotWidth) {
        this.slotWidth = slotWidth;
    }

    void setSlotPosition(int slotPosition) {
        this.slotPosition = slotPosition;
    }

    void setBgColor(Color bgColor) {
        setBackground(bgColor);
    }

    void setSlotColor(Color slotColor) {
        this.slotColor = slotColor;
    }

    public void setTimeAxisPainter(TimeAxisPainter timeAxisPainter) {
        this.timeAxisPainter = timeAxisPainter;
    }

    void setIndentX(int indentX) {
        this.indentX = indentX;
    }

    void setIndentY(int indentY) {
        this.indentY = indentY;
    }

    void addSlotListener(SlotListener slotListener) {
        slotListeners.add(slotListener);
    }

    private void notifySlotListeners(int newSlotPosition) {
        for (SlotListener listener: slotListeners) {
            listener.moveSlot(newSlotPosition);
        }
    }

    int getWeight() {
        return weight;
    }

    protected int getWorkspaceWidth() {
        return (getSize().width - indentX);
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
        return (getSize().height - indentY);
    }


    protected void transformCoordinate(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(indentX, getMaxY()); // move XY origin to the left bottom point
        g2d.transform(AffineTransform.getScaleInstance(1, -1)); // flip Y-axis
    }

    protected void restoreCoordinate(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(-indentX, getMaxY()); // move XY origin to the left top point
        g2d.transform(AffineTransform.getScaleInstance(1, -1)); // flip Y-axis and zoom it
    }

    private void paintSlot(Graphics g) {
        if(slotWidth > 0) {
            g.setColor(slotColor);
            g.drawRect(slotPosition, 0, slotWidth, getMaxY());
            g.drawLine(slotPosition-1, 0, slotPosition-1, getMaxY());
            g.drawLine(slotPosition-2, 0, slotPosition-2, getMaxY());
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        transformCoordinate(g);
        double frequency = 0;
        DataDimension dataDimension = new DataDimension();
        long startTime = 0;
        if(graphList.size() > 0 && graphList.get(0) != null) {
            frequency = graphList.get(0).getFrequency();
            startTime = graphList.get(0).getStartTime();
            dataDimension = graphList.get(0).getDataDimension();
        }

        YAxisPainter.paint(g, zoom, dataDimension, isXCentered);
        timeAxisPainter.paint(g, startTime, startIndex, frequency);

        int graph_number = 0;
        for (DataSet graph : graphList) {
            Color graphColor = graphColors[graph_number % graphColors.length];
            graph_number++;
            g.setColor(graphColor);
            GraphPainter.paint(g, zoom, startIndex, graph);
        }

        paintSlot(g);

        restoreCoordinate(g);
    }
}