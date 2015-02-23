package graph.painters;

import java.awt.*;
import java.awt.List;
import java.util.*;

/**
 * Created by mac on 22/02/15.
 */
public class XAxisBasePainter {
    private Color axisColor = Color.GREEN;
    private Color gridColor = new Color(0, 40, 0);
    private Color strongGridColor = new Color(0, 60, 0);
    private Color zebraColor = new Color(40, 40, 40);


    private void paintValueMark(Graphics g, int i) {
        // Paint Stroke 2-3 times
        g.drawLine(i, -3, i, +3);
        g.drawLine(i + 1, -3, i + 1, +3);
        g.drawLine(i - 1, -3, i - 1, +3);
    }

    private void paintGridMark(Graphics g, int i) {
        // Paint Stroke
        g.drawLine(i, -2, i, +2);
    }

    private void paintInterGridMark(Graphics g, int i) {
        // Paint Point
        g.drawLine(i, 0, i, 0);
    }

    private void paintValueStamp(Graphics g, int i, String valueStamp) {
        // Paint Time Stamp
        FontMetrics fm = g.getFontMetrics(g.getFont());
        int stampWidth = fm.stringWidth(valueStamp);
        int stampHeight = fm.getHeight();
        int stampShift = stampWidth / 2;
        g.drawString(valueStamp, i - stampShift, stampHeight+2);
       // g.drawString(valueStamp, i - stampShift, - 6);
    }

    public void paintGrid(Graphics g, int startIndex, java.util.List<Integer> gridMarks) {
        g.setColor(gridColor);
        for (int i = 0; i < gridMarks.size() - 1; i++) {
            int point = gridMarks.get(i) - startIndex;
            if (point >= 0) {
                g.drawLine(point, g.getClipBounds().height, point, -g.getClipBounds().height);
            }
        }
    }

    public void paintStrongGrid(Graphics g, int startIndex, java.util.List<Integer> gridMarks) {
        g.setColor(strongGridColor);
        for (int i = 0; i < gridMarks.size() - 1; i++) {
            int point = gridMarks.get(i) - startIndex;
            if (point >= 0) {
                g.drawLine(point, g.getClipBounds().height, point, -g.getClipBounds().height);
                // g.drawLine(point - 1, g.getClipBounds().height, point - 1, -g.getClipBounds().height);
                g.drawLine(point + 1, g.getClipBounds().height, point + 1, -g.getClipBounds().height);
            }
        }
    }


    public void paintZebra(Graphics g, int startIndex, java.util.List<Integer> gridMarks) {
        g.setColor(zebraColor);
        boolean flag = false;
        for (int i = 0; i < gridMarks.size() - 1; i++) {
            int point1 = gridMarks.get(i) - startIndex;
            int point2 = gridMarks.get(i+1) - startIndex;
            if(point1 < 0 && point2 > 0) {
                g.fillRect(0, -g.getClipBounds().height, point2, 2*g.getClipBounds().height);
                flag = true;
            }

            if (flag && point1 >= 0) {
                g.fillRect(point1, -g.getClipBounds().height, point2-point1, 2*g.getClipBounds().height);
            }
            flag = !flag;
        }
    }

    public void paintAxis(Graphics g, int startIndex, java.util.List<Integer> valueMarks, java.util.List<Integer> gridMarks, java.util.List<Integer> interGridMarks) {
        g.setColor(axisColor);
        for (int i = 0; i < interGridMarks.size() - 1; i++) {
            int point = interGridMarks.get(i) - startIndex;
            if (point >= 0) {
                paintInterGridMark(g, point);
            }
        }
        for (int i = 0; i < gridMarks.size() - 1; i++) {
            int point = gridMarks.get(i) - startIndex;
            if (point >= 0) {
                paintGridMark(g, point);
            }
        }
        for (int i = 0; i < valueMarks.size() - 1; i++) {
            int markIndex = valueMarks.get(i);
            int point = markIndex - startIndex;
            if (point >= 0) {
                paintValueMark(g, point);
            }
        }
    }

    public void paintValues(Graphics g, int startIndex, HashMap<Integer, String> valueStamps) {
        g.setColor(axisColor);
        for (int i : valueStamps.keySet()) {
            int point = i - startIndex;
            if (point >= 0) {
                //  paintValueMark(g, point);
                paintValueStamp(g, point, valueStamps.get(i));
            }
        }
    }
}
