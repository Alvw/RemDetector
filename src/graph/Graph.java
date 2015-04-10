package graph;

import data.DataSeries;

public class Graph {
    private DataSeries graphData;
    private GraphType graphType;

    public Graph(DataSeries graphData, GraphType graphType) {
        this.graphData = graphData;
        this.graphType = graphType;
    }

    public DataSeries getGraphData() {
        return graphData;
    }

    public GraphType getGraphType() {
        return graphType;
    }
}
