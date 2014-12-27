package graph;

import data.DataSet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: GENA
 * Date: 08.05.14
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
public class GraphPanel extends JPanel {
    protected java.util.List<DataSet> graphs = new ArrayList<DataSet>();//panel can have a several graphs. Max 3 for simplicity

    protected static final double ZOOM_PLUS_CHANGE = Math.sqrt(2.0);// 2 clicks(rotations) up increase zoom twice
    protected static final double ZOOM_MINUS_CHANGE = 1 / ZOOM_PLUS_CHANGE; // similarly 2 clicks(rotations) down reduces zoom twice
    protected static final Color bgColor = Color.BLACK;
    protected static final Color axisColor = Color.GREEN;
    protected static final Color graphColors[] = {Color.YELLOW, Color.RED, Color.CYAN};

    protected double zoom = 0.5;
    protected boolean isAutoZoom;
    protected boolean isXCentered = true;
    protected int weight = 1;
    protected GraphsData graphsData;

    GraphPanel(int weight, boolean isXCentered, GraphsData graphsData) {
        this.weight = weight;
        this.isXCentered = isXCentered;
        this.graphsData = graphsData;
        setBackground(bgColor);
        // MouseListener to zoom Y_Axes
        addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                zooming(e.getWheelRotation());
            }
        });
    }

    protected int getWeight() {
        return weight;
    }

    protected double getTimeFrequency() {
         return graphsData.getTimeFrequency();
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

    protected void addGraph(DataSet graphData) {
        graphs.add(graphData);
        repaint();
    }


    protected void setAutoZoom(boolean isAutoZoom) {
        this.isAutoZoom = isAutoZoom;
        repaint();
    }

    protected void paintAxisY(Graphics g) {

        int minValueStep = 50;  //default value between two labels
        int minPointStep = 20; // distance between two labels in pixels
        int minValue = 0;

        g.setColor(axisColor);

        int valueStep = (int)(minPointStep/(zoom*minValueStep)+1)*minValueStep;
        int numberOfColumns = (int)(getMaxY()/(zoom*valueStep));
        Graphics2D g2d = (Graphics2D) g;
        g2d.transform(AffineTransform.getScaleInstance(1.0, -1.0)); // flip transformation

        for (int i = 1; i < numberOfColumns+1; i++) {
            long gridValue = (minValue/valueStep)*valueStep + i*valueStep;
            int position = (int)Math.round(zoom*(gridValue - minValue));
            g.drawLine(12, -position, +18, -position);
            String valueText = String.valueOf(gridValue);
            g.drawString(valueText, -25, -position+5);
        }

        if(isXCentered) {
            for (int i = 1; i < numberOfColumns+1; i++) {
                long gridValue = (minValue/valueStep)*valueStep + i*valueStep;
                int position = -(int)Math.round(zoom*(gridValue - minValue));
                g.drawLine(12, -position, +18, -position);
            }
        }

        g2d.transform(AffineTransform.getScaleInstance(1.0, -1.0)); // flip transformation
    }

    protected void paintAxisX(Graphics g) {

        int SECOND_HALF = 500; //milliseconds
        int SECOND = 1000; //milliseconds
        int SECONDS_10 = 10 * SECOND; //milliseconds
        int MINUTE = 60*SECOND;//milliseconds
        int point_distance_msec = (int) (1000/getTimeFrequency());

        g.setColor(axisColor);
        Graphics2D g2d = (Graphics2D) g;
        g2d.transform(AffineTransform.getScaleInstance(1.0, -1.0)); // flip transformation

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        long newStartTime = (graphsData.getStartTime()/ point_distance_msec)* point_distance_msec + point_distance_msec;
        for (int i = 0; i  < getWorkspaceWidth(); i++) {
            long iTime = newStartTime + (long)((getStartIndex() + i) * point_distance_msec);
            if((iTime % SECONDS_10) == 0){
                // Paint Rectangle
                g.fillRect(i - 1, -4, 3, 9);
            }
            else if((iTime % SECOND) == 0){
                // Paint Stroke
                g.drawLine(i, -2, i, +2);
            }
            else if((iTime % SECOND_HALF) == 0){
                // Paint Point
                g.drawLine(i, 0, i, 0);
            }

          /*  if((iTime % MINUTE) == 0){
                String timeStamp = dateFormat.format(new Date(iTime)) ;
                // Paint Time Stamp
                g.drawString(timeStamp, i - 15, +18);
            }  */

            if((iTime % SECONDS_10) == 0){
                String timeStamp = dateFormat.format(new Date(iTime));
                // Paint Time Stamp
                g.drawString(timeStamp, i - 15, +18);
            }

        }

        g2d.transform(AffineTransform.getScaleInstance(1.0, -1.0)); // flip transformation

    }

    protected int getMaxY() {
        if(isXCentered) {
            return getSize().height/2;
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

    protected int getValue(int x, DataSet graph) {
        double frequency = graph.getFrequency();
        double timeFrequency = getTimeFrequency();
        int value = 0;
        // points x corresponds to frequencyBase
        if(timeFrequency == 0 || frequency == timeFrequency) {
            value = graph.get(x);
        }
        else if(frequency < timeFrequency) {
            int index = (int)(frequency * x / timeFrequency);
            value = graph.get(index);
        }
        else if(frequency > timeFrequency) {
            if(x == 0) {
                value = graph.get(x);
            }
            else {
                int indexStart = (int)(frequency * (x-1) / timeFrequency);
                int indexEnd = (int)(frequency * x / timeFrequency);
                for(int index = indexStart; index < indexEnd; index++) {
                    value += graph.get(index);
                }
                value = value/(indexEnd - indexStart);
            }
        }
        return value;
    }
    protected int getStartIndex() {
        return graphsData.getStartIndex();
    }

    protected void paintGraphs(Graphics g) {
        int graph_number = 0;
        int startPoint = getStartIndex();
        for (DataSet graph : graphs) {
            Color graphColor = graphColors[graph_number];
            graph_number++;
            if (graph != null) {
                int size = (int)(graph.size() * getTimeFrequency() / graph.getFrequency());
                int endPoint = Math.min(getWorkspaceWidth(), (size - startPoint));
                VerticalLine vLine = new VerticalLine();
                for (int x = 0; x < endPoint; x++) {
                    //int value = graph.get(x + startPoint);
                    int value = getValue(x + startPoint, graph);
                    if(value == DataSet.UNDEFINED) {
                        vLine.clear();
                    }
                    else {
                        g.setColor(graphColor);
                        int y = (int) Math.round(zoom * value);
                        drawVerticalLine(g, x, y, vLine);
                    }
                }
            }
        }
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);    //To change body of overridden methods use File | Settings | File Templates.
        transformCoordinate(g);

        paintAxisY(g);

        if(getTimeFrequency() != 0) {
            paintAxisX(g);
        }

        paintGraphs(g);

        restoreCoordinate(g);
    }

    private void drawVerticalLine(Graphics g, int x, int y, VerticalLine vLine) {
        vLine.setNewBounds(y);
        g.drawLine(x, vLine.min, x, vLine.max);
    }

    class VerticalLine {
        int max = 0;
        int min = -1;

        public void clear() {
            max = 0;
            min = -1;
        }

        void setNewBounds(int y) {
            if (y >= min && y <= max) {
                min = max = y;
            } else if (y > max) {
                min = max + 1;
                max = y;
            } else if (y < min) {
                max = min - 1;
                min = y;
            }
        }
    }
}



