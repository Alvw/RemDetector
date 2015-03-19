package graph;

import data.DataSet;

public class Graph {
    private DataSet graphData;
    private GraphType graphType;

    public Graph(DataSet graphData, GraphType graphType) {
        this.graphData = graphData;
        this.graphType = graphType;
    }

    public DataSet getGraphData() {
        return graphData;
    }

    public GraphType getGraphType() {
        return graphType;
    }
}
