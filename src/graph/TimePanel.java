package graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

public class TimePanel extends JPanel{
    private int indentX;
    private int startIndex;
    private long startTime;
    private double frequency;
    private TimeAxisPainter timePainter = new TimeAxisPainter();

    JButton plusButton = new JButton("+");
    JButton minusButton = new JButton("-");

    public TimePanel() {
        timePainter.isAxisPaint(false);
        timePainter.isGridPaint(false);
        setBackground(Color.black);
        setLayout(new FlowLayout(0));
        plusButton.setMargin(new Insets(0,4,0,4));
        minusButton.setMargin(new Insets(0,4,0,4));
        plusButton.setFocusable(false);
        minusButton.setFocusable(false);
        add(minusButton);
        add(plusButton);
    }

    public void addPlusButtonListener(ActionListener listener) {
        plusButton.addActionListener(listener);
    }

    public void addMinusButtonListener(ActionListener listener) {
        minusButton.addActionListener(listener);
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
