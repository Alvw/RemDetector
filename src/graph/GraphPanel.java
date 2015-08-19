package graph;

import data.DataSeries;
import data.Scaling;
import graph.painters.GraphPainter;
import graph.painters.XAxisPainter;
import graph.painters.YAxisPainter;

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
public class GraphPanel extends JPanel {
    private static final double ZOOM_PLUS_CHANGE = Math.sqrt(2.0);// 2 clicks(rotations) up increase zoom twice
    private static final double ZOOM_MINUS_CHANGE = 1 / ZOOM_PLUS_CHANGE; // similarly 2 clicks(rotations) down reduces zoom twice

    private static final Color DEFAULT_BG_COLOR = Color.black;

    private Color graphColors[] = {Color.YELLOW, new Color(0,150, 250), Color.RED, Color.GREEN, Color.MAGENTA, Color.ORANGE};
    private Color slotColor = new Color(255, 0, 100);
    private java.util.List<SlotListener> slotListeners = new ArrayList<SlotListener>();
    private java.util.List<FourierListener> fourierListeners = new ArrayList<FourierListener>();

    private List<Graph> graphList = new ArrayList<Graph>();
    private double zoom = 0.5;
    private boolean isXCentered = true;
    private int weight = 1;
    private int startIndex;
    private int slotWidth;
    private int slotPosition;
    private int indentX;
    private int indentY;

    private GraphPainter graphPainter = new GraphPainter();
    private XAxisPainter xAxisPainter = new XAxisPainter();
    private YAxisPainter yAxisPainter = new YAxisPainter();


    public GraphPanel(int weight, boolean isXCentered) {
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
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int slotPosition = e.getX() - indentX;
                    notifySlotListeners(slotPosition);
                }
                if (SwingUtilities.isRightMouseButton(e)) {
                    if(graphList.size() > 0) {
                        notifyFourierListeners(graphList.get(0).getGraphData(), startIndex);
                    }
                }
            }
        });
    }


    public void setGraphs(List<Graph> graphList) {
        this.graphList = graphList;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void setSlotWidth(int slotWidth) {
        this.slotWidth = slotWidth;
    }

    public void setSlotPosition(int slotPosition) {
        this.slotPosition = slotPosition;
    }

    public void setSlotColor(Color slotColor) {
        this.slotColor = slotColor;
    }

    public void setxAxisPainter(XAxisPainter xAxisPainter) {
        this.xAxisPainter = xAxisPainter;
    }

    public void setYAxisPainter(YAxisPainter yAxisPainter) {
        this.yAxisPainter = yAxisPainter;
    }

    public void setIndentX(int indentX) {
        this.indentX = indentX;
    }

    public void setIndentY(int indentY) {
        this.indentY = indentY;
    }

    void addSlotListener(SlotListener slotListener) {
        slotListeners.add(slotListener);
    }

    private void notifySlotListeners(int newSlotPosition) {
        for (SlotListener listener : slotListeners) {
            listener.moveSlot(newSlotPosition);
        }
    }

    void addFourierListener(FourierListener fourierListener) {
        fourierListeners.add(fourierListener);
    }

    private void notifyFourierListeners(DataSeries graph, int startIndex) {
        for (FourierListener listener : fourierListeners) {
            listener.doFourier(graph, startIndex);
        }
    }

    int getWeight() {
        return weight;
    }


    private void zooming(int zoomDirection) {
        if (zoomDirection > 0) {
            zoom = zoom * ZOOM_PLUS_CHANGE;
        } else {
            zoom = zoom * ZOOM_MINUS_CHANGE;
        }
        repaint();
    }

    private int getMaxY() {
        if (isXCentered) {
            return getSize().height / 2;
        }
        return (getSize().height - indentY);
    }


    private void transformCoordinate(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(indentX, getMaxY()); // move XY origin to the left bottom point
        g2d.transform(AffineTransform.getScaleInstance(1, -1)); // flip Y-axis
    }

    private void restoreCoordinate(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(-indentX, getMaxY()); // move XY origin to the left top point
        g2d.transform(AffineTransform.getScaleInstance(1, -1)); // flip Y-axis and zoom it
    }

    private void paintSlot(Graphics g) {
        int height = getHeight();
        g.setColor(slotColor);
        if (slotWidth > 2) {
            g.fillRect(slotPosition, -height, slotWidth, 2 * height);
        }
        if (slotWidth > 0 && slotWidth <= 2) {
            g.drawLine(slotPosition, -height, slotPosition, 2 * height);
            g.drawLine(slotPosition - 1, -height, slotPosition - 1, 2 * height);
            g.drawLine(slotPosition + 1, -height, slotPosition + 1, 2 * height);
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        transformCoordinate(g);
        if (graphList.size() > 0 && graphList.get(0) != null) {
            Scaling scaling = graphList.get(0).getGraphData().getScaling();
            xAxisPainter.paint(g, startIndex, scaling);
            yAxisPainter.paint(g, zoom, scaling);
        }

        int graph_number = 0;
        for (Graph graph : graphList) {
            Color graphColor = graphColors[graph_number % graphColors.length];
            graph_number++;
            graphPainter.setDefaultColor(graphColor);
            graphPainter.paint(g, zoom, startIndex, graph);
        }
        paintSlot(g);
        restoreCoordinate(g);

    }
}