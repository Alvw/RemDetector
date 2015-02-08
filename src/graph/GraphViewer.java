package graph;

import data.DataSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class GraphViewer extends JPanel{
    private static final Log log = LogFactory.getLog(GraphViewer.class);
    private static final int DEFAULT_GRAPH_PANEL_WEIGHT = 1;
    private static final int DEFAULT_PREVIEW_PANEL_WEIGHT = 1;

    private static final boolean IS_GRAPH_X_CENTERED_DEFAULT = true;
    private static final boolean IS_PREVIEW_X_CENTERED_DEFAULT = true;

    private int xIndent = 50;
    private int yIndent = 2;
    private Color bgColor = Color.BLACK;
    private Color previewBgColor = new Color(25, 25, 25);

    private GraphModel graphModel;
    private GraphView graphView;
    private GraphController graphController;
    private GraphPresenter graphPresenter;

    public GraphViewer() {
        graphModel = new GraphModel();
        graphController = new GraphController(graphModel);
        graphView = new GraphView(graphController);
        graphPresenter = new GraphPresenter(graphModel, graphView);
        graphController.addListener(graphPresenter);

        graphView.setBgColor(bgColor);
        graphView.setPreviewBgColor(previewBgColor);
        graphView.setXIndent(xIndent);
        graphView.setYIndent(yIndent);
        setLayout(new BorderLayout());
        add(graphView, BorderLayout.CENTER);
        graphView.requestFocusInWindow();
    }

    @Override
    public boolean requestFocusInWindow() {
        return graphView.requestFocusInWindow();
    }

    public void addGraphPanel(int weight, boolean isXCentered) {
        graphView.addGraphPanel(weight, isXCentered);
        graphController.addGraphCluster();
    }

    public void addPreviewPanel(int weight, boolean isXCentered) {
        graphView.addPreviewPanel(weight, isXCentered);
        graphController.addPreviewCluster();
    }


    public void addGraph(DataSet graph, int panelNumber) {
        graphController.addGraph(graph, panelNumber);
    }

    public void addPreview(DataSet preview, int panelNumber) {
        graphController.addPreview(preview, panelNumber);
    }

/*
* Add Graph to the last graph panel. If there is no graph panel create one
*/
    public void addGraph(DataSet graph) {
        int panelNumber = graphView.getNumberOfGraphPanels() - 1;
        if(panelNumber < 0) {
            addGraphPanel(DEFAULT_GRAPH_PANEL_WEIGHT, IS_GRAPH_X_CENTERED_DEFAULT);
            panelNumber = 0;
        }
        addGraph(graph, panelNumber);
    }

/*
* Add Preview to the last preview panel. If there is no preview panel create one
*/
    public void addPreview(DataSet preview) {
        int panelNumber = graphView.getNumberOfPreviewPanels() - 1;
        if(panelNumber < 0) {
            addPreviewPanel(DEFAULT_PREVIEW_PANEL_WEIGHT, IS_PREVIEW_X_CENTERED_DEFAULT);
            panelNumber = 0;
        }
        addPreview(preview, panelNumber);
    }

    public void setGraphFrequency(double graphFrequency) {
        graphController.setGraphFrequency(graphFrequency);
    }

    public void setPreviewFrequency(double previewFrequency) {
        graphController.setPreviewFrequency(previewFrequency);
    }

    public void setCompression(int compression) {
        graphController.setCompression(compression);
    }

    public void autoScroll() {
        graphController.autoScroll();
    }
}
