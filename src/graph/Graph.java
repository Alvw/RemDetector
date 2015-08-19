package graph;

import data.DataSeries;
import graph.colors.ColorSelector;

public class Graph {
    private DataSeries graphData;
    private ColorSelector colorSelector;
    private GraphType graphType;

    public Graph(DataSeries graphData, GraphType graphType) {
        this.graphData = graphData;
        this.graphType = graphType;
    }

    public Graph(DataSeries graphData,  GraphType graphType, ColorSelector colorSelector) {
        this(graphData, graphType);
        this.colorSelector = colorSelector;

    }

    public DataSeries getGraphData() {
        return graphData;
    }

    public GraphType getGraphType() {
        return graphType;
    }

    public ColorSelector getColorSelector() {
        return colorSelector;
    }

    public void setGraphData(DataSeries graphData) {
        this.graphData = graphData;
    }

    public void setColorSelector(ColorSelector colorSelector) {
        this.colorSelector = colorSelector;
    }

    public void setGraphType(GraphType graphType) {
        this.graphType = graphType;
    }
}
