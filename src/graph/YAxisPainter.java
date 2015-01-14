package graph;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class YAxisPainter {
    private static final Color AXIS_COLOR = Color.GREEN;
    private static final Color GRID_COLOR = new Color(50, 50, 50);


   static void paint(Graphics g, double zoom, boolean isXCentered) {
        int minValueStep = 50;  //default value between two labels
        int minPointStep = 20; // distance between two labels in pixels
        int minValue = 0;

        int height = g.getClipBounds().height;
        int width = g.getClipBounds().width;
        int valueStep = (int)(minPointStep/(zoom*minValueStep)+1)*minValueStep;
        int numberOfColumns = (int)(height/(zoom*valueStep));
        Graphics2D g2d = (Graphics2D) g;
        g2d.transform(AffineTransform.getScaleInstance(1.0, -1.0)); // flip transformation

        for (int i = 1; i < numberOfColumns+1; i++) {
            long gridValue = (minValue/valueStep)*valueStep + i*valueStep;
            int position = (int)Math.round(zoom*(gridValue - minValue));
            g.setColor(GRID_COLOR);
            g.drawLine(0, -position, width, -position);
            String valueText = String.valueOf(gridValue);
            g.setColor(AXIS_COLOR);
            g.drawString(valueText, -30, -position-1);
        }

        if(isXCentered) {
            g.setColor(GRID_COLOR);
            for (int i = 1; i < numberOfColumns+1; i++) {
                long gridValue = (minValue/valueStep)*valueStep + i*valueStep;
                int position = -(int)Math.round(zoom*(gridValue - minValue));
                // g.drawLine(0, -position, width, -position);
            }
        }

        g2d.transform(AffineTransform.getScaleInstance(1.0, -1.0)); // flip transformation
    }


}
