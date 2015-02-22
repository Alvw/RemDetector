package graph;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

public class TimePanel extends JPanel {
    private int indentX;
    private int startIndex;
    private long startTime;
    private double frequency;
    private TimeAxisPainter timePainter = new TimeAxisPainter();

    JButton plusButton = new SmallButton("+");
    JButton minusButton = new SmallButton("-");

    public TimePanel() {
        timePainter.isAxisPaint(false);
        timePainter.isGridPaint(false);
        setBackground(Color.black);
        setLayout(new FlowLayout(0));
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

    public void transformCoordinate(Graphics g) {
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

    private class ButtonBorder extends BevelBorder {
        public ButtonBorder() {
            super(BevelBorder.RAISED);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(3, 6, 3, 6);
        }
    }

    private class SmallButton extends JButton {
        Color baseBg = new Color(220, 220, 240);
        Color highlightBg = new Color(180, 200, 250);
        public SmallButton(String s) {
            super(s);
            setFocusable(false);
            setOpaque(true);
            setBackground(baseBg);
            setBorder(new ButtonBorder());

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent mouseEvent) {
                    super.mousePressed(mouseEvent);
                    setBackground(highlightBg);
                }

                @Override
                public void mouseReleased(MouseEvent mouseEvent) {
                    super.mouseReleased(mouseEvent);
                    setBackground(baseBg);
                }

            });
        }
    }
}
