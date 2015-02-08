package graph;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class TimePanel extends JPanel{
    private int indentX;
    private int startIndex;
    private long startTime;
    private double frequency;
    private TimeAxisPainter timePainter = new TimeAxisPainter();

    public TimePanel() {
        timePainter.isAxisPaint(false);
        timePainter.isGridPaint(false);
        setBackground(Color.black);
    }

    public void setIndentX(int indentX) {
        this.indentX = indentX;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public double getFrequency() {
        return frequency;
    }

    public  void transformCoordinate(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(indentX, g.getClipBounds().getHeight()); // move XY origin to the left bottom point
        g2d.transform(AffineTransform.getScaleInstance(1, -1)); // flip Y-axis
    }

    public void restoreCoordinate(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(-indentX, g.getClipBounds().getHeight()); // move XY origin to the left top point
        g2d.transform(AffineTransform.getScaleInstance(1, -1)); // flip Y-axis and zoom it
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        transformCoordinate(g);
        timePainter.paint(g, startTime, startIndex, frequency);
        restoreCoordinate(g);
    }
}
