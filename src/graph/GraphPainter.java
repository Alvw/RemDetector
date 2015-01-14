package graph;

import data.DataSet;

import java.awt.*;

public class GraphPainter {

    static void paint(Graphics g, int width1, double zoom, int startIndex, DataSet graph) {
                int width = g.getClipBounds().width;
                int startPoint = startIndex;
                if (startPoint < 0) {
                    return;
                }

                if (graph != null) {

                    if (startPoint >= graph.size()) {
                return;
            }
            int endPoint = Math.min(width, (graph.size() - startPoint));
            VerticalLine vLine = new VerticalLine();
            for (int x = 0; x < endPoint; x++) {
                int value = graph.get(x + startPoint);
                if (value == DataSet.UNDEFINED) {
                    vLine.clear();
                } else {
                    int y = (int) Math.round(zoom * value);
                    drawVerticalLine(g, x, y, vLine);
                }
            }
        }
    }

    private static void drawVerticalLine(Graphics g, int x, int y, VerticalLine vLine) {
        vLine.setNewBounds(y);
        g.drawLine(x, vLine.min, x, vLine.max);
    }

    static class VerticalLine {
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
