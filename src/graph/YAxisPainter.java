package graph;

import data.DataDimension;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class YAxisPainter {
    private static final Color AXIS_COLOR = Color.GREEN;
    private static final Color GRID_COLOR = new Color(0, 80, 0);


    static void paint(Graphics g, double zoom, DataDimension dataDimension) {
        FontMetrics fm = g.getFontMetrics(g.getFont());
        int fontHeight = fm.getHeight();
        int minPointStep = fontHeight + 4; // distance between two labels in pixels
        double gain = 1;
        String physicalDimension = "";
        if (dataDimension != null) {
            physicalDimension = dataDimension.getPhysicalDimension();
            gain = (dataDimension.getPhysicalMax() - dataDimension.getPhysicalMin()) / (dataDimension.getDigitalMax() - dataDimension.getDigitalMin());
        }

        Rectangle r = g.getClipBounds();
        int xIndent = - r.x;
        int height = r.height + r.y;
        int width = r.width;
        boolean isXCentered = false;
        if(Math.abs(r.height  + 2 * r.y) <= 1) {
            isXCentered = true;
        }

        double minValueStep = minPointStep * gain / zoom;
        int exponent = (int) Math.log10(minValueStep);
        int[] steps = {1, 2, 5, 10};

        String stringFormat = "%.0f";
        if(Math.log10(minValueStep) < 0) {
            exponent = exponent - 1;
            stringFormat = "%."+Math.abs(exponent)+"f";
        }
        double valueStep = 0;
        int j=0;
        while(valueStep == 0 && j < steps.length) {
            double value = steps[j] * Math.pow(10, exponent);
            j++;
            if (value >= minValueStep) {
                valueStep = value;
            }
        }
        int pointStep = (int) (valueStep * zoom / gain);
        if(pointStep < 1) {
            pointStep = 1;
        }
        int numberOfColumns = (int) (height / pointStep);
        Graphics2D g2d = (Graphics2D) g;

        g2d.transform(AffineTransform.getScaleInstance(1.0, -1.0)); // flip transformation

        for (int i = 1; i < numberOfColumns; i++) {
            double gridValue = i*valueStep;
            int position = i*pointStep;
            g.setColor(GRID_COLOR);
            g.drawLine(0, -position, width, -position);
            String valueText = "+"+String.format(stringFormat, gridValue)+" "+physicalDimension;
            g.setColor(AXIS_COLOR);
            g.drawString(valueText, -(xIndent - 5), - position - 1);
        }

        if (isXCentered) {
            for (int i = 1; i < numberOfColumns; i++) {
                double gridValue = i*valueStep;
                int position = -(i*pointStep);
                g.setColor(GRID_COLOR);
                g.drawLine(0, -position, width, -position);
                String valueText = "-"+String.format(stringFormat, gridValue)+" "+physicalDimension;
                g.setColor(AXIS_COLOR);
                g.drawString(valueText, -(xIndent - 5), - position - 1);
            }
        }

        g2d.transform(AffineTransform.getScaleInstance(1.0, -1.0)); // flip transformation
    }


}
