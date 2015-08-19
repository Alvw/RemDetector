package graph.painters;

import data.DataSeries;
import graph.Graph;
import graph.GraphType;
import graph.colors.ColorSelector;

import java.awt.*;
import java.util.ArrayList;

public class GraphPainter {
    private Color defaultGraphColor = Color.YELLOW;

    public void setDefaultColor(Color graphColor) {
        defaultGraphColor = graphColor;
    }

    public void paint(Graphics g, double zoom, int startIndex, Graph graph) {
        g.setColor(defaultGraphColor);
        DataSeries graphData = graph.getGraphData();
        GraphType graphType = graph.getGraphType();
        ColorSelector colorSelector = graph.getColorSelector();
        if (graphData != null && startIndex >= 0 && startIndex < graphData.size()) {
            int width = g.getClipBounds().width;
            int height = g.getClipBounds().height;
            int endPoint = Math.min(width, (graphData.size() - startIndex));
            int value = graphData.get(startIndex);
            int y = (int) Math.round(zoom * value);
            VerticalLine vLine = new VerticalLine(y);
            for (int x = 0; x < endPoint; x++) {
                value = graphData.get(x + startIndex);
                y = (int) Math.round(zoom * value);
                if(colorSelector != null && colorSelector.getColor(x + startIndex) != null) {
                    g.setColor(colorSelector.getColor(x + startIndex));
                }
                else{
                    g.setColor(defaultGraphColor);
                }
                if(graphType == GraphType.VERTICAL_LINE) {
                    drawVerticalLine(g, x, y, vLine);
                }
                if(graphType == GraphType.LINE) {
                    int xPrevious = 0;
                    if(x + startIndex > 0) {
                       xPrevious = x - 1;
                    }
                    int valuePrevious = graphData.get(xPrevious + startIndex);
                    int yPrevious = (int) Math.round(zoom * valuePrevious);
                    g.drawLine(xPrevious, yPrevious, x, y);
                }
                if(graphType == GraphType.BAR) {
                    g.drawLine(x, 0, x, y);
                }
                if(graphType == GraphType.BOOLEAN) {
                    if(value == 0)  {
                        g.drawLine(x, -height, x, height);
                    }
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
