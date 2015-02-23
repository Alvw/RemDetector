package graph.painters;

import data.DataSet;

import java.awt.*;

public class GraphPainter {
    private Color graphColor = Color.YELLOW;


    public void setColor(Color graphColor) {
        this.graphColor = graphColor;
    }

    public void paint(Graphics g, double zoom, int startIndex, DataSet graph) {
        g.setColor(graphColor);
        if (graph != null && startIndex >= 0 && startIndex < graph.size()) {
            int width = g.getClipBounds().width;
            int height = g.getClipBounds().height;
            int value = graph.get(startIndex);
            int y = (int) Math.round(zoom * value);
            int endPoint = Math.min(width, (graph.size() - startIndex));
            VerticalLine vLine = new VerticalLine(y);
            for (int x = 0; x < endPoint; x++) {
                value = graph.get(x + startIndex);
                if(value == DataSet.UNDEFINED) {
                    g.setColor(new Color(0,150, 250));
                    //drawVerticalLine(g, x, 0, vLine);
                    g.drawLine(x, 0, x, height);
                }
                else{
                    g.setColor(graphColor);
                    y = (int) Math.round(zoom * value);
                    drawVerticalLine(g, x, y, vLine);
                }

            }
        }
    }

    private void drawVerticalLine(Graphics g, int x, int y, VerticalLine vLine) {
        vLine.setNewBounds(y);
        g.drawLine(x, vLine.min, x, vLine.max);
    }

    class VerticalLine {
        int max = 0;
        int min = -1;

        VerticalLine(int y) {
            setNewBounds(y);
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
