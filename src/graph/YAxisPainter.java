package graph;

import data.DataDimension;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class YAxisPainter {
    private static final Color AXIS_COLOR = Color.GREEN;
    private static final Color GRID_COLOR = new Color(0, 80, 0);


    static void paint(Graphics g, double zoom, DataDimension dataDimension) {
        int minValueStep = 50;  //default value between two labels
        int minPointStep = 20; // distance between two labels in pixels
        int minValue = 0;
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
        int valueStep = (int) (minPointStep / (zoom * minValueStep) + 1) * minValueStep;
        int numberOfColumns = (int) (height / (zoom * valueStep));
        Graphics2D g2d = (Graphics2D) g;


        g2d.transform(AffineTransform.getScaleInstance(1.0, -1.0)); // flip transformation

        for (int i = 1; i < numberOfColumns; i++) {
            long gridValue = (minValue / valueStep) * valueStep + i * valueStep;
            double physValue = gridValue * gain;
            int position = (int) Math.round(zoom * (gridValue - minValue));
            g.setColor(GRID_COLOR);
            g.drawLine(0, -position, width, -position);
            String valueText = String.format("%.1f", physValue)+" "+physicalDimension;
            g.setColor(AXIS_COLOR);
            g.drawString(valueText, -(xIndent - 5), - position - 1);
        }

        if (isXCentered) {
            g.setColor(GRID_COLOR);
            for (int i = 1; i < numberOfColumns; i++) {
                long gridValue = (minValue / valueStep) * valueStep + i * valueStep;
                int position = -(int) Math.round(zoom * (gridValue - minValue));
                 g.drawLine(0, -position, width, -position);
            }
        }

        g2d.transform(AffineTransform.getScaleInstance(1.0, -1.0)); // flip transformation
    }


}
