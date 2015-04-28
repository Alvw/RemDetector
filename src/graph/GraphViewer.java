package graph;

import data.CompressionType;
import data.DataSeries;
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

    private static final int X_INDENT_DEFAULT =50;
    private static final int Y_INDENT_DEFAULT = 4;
    private Color bgColor = Color.BLACK;
    private Color previewBgColor = new Color(25, 25, 25);

    private GraphModel graphModel;
    private GraphView graphView;
    private GraphController graphController;
    private GraphPresenter graphPresenter;

    public GraphViewer() {
        this(true, true);
    }

    public GraphViewer(boolean showScalesSeparate, boolean isFourierActive) {
        graphModel = new GraphModel();
        graphController = new GraphController(graphModel);
        if(isFourierActive) {
            FourierHandler fourierHandler = new FourierHandler(graphModel);
            graphController.addListener(fourierHandler);
            graphView = new GraphView(graphController,fourierHandler, showScalesSeparate);
            graphView.requestFocusInWindow();
        }
        else {
            graphView = new GraphView(graphController, showScalesSeparate);
        }

        graphPresenter = new GraphPresenter(graphModel, graphView);
        graphController.addListener(graphPresenter);
        graphView.setBgColor(bgColor);
        graphView.setPreviewBgColor(previewBgColor);
        graphView.setXIndent(X_INDENT_DEFAULT);
        graphView.setYIndent(Y_INDENT_DEFAULT);
        setLayout(new BorderLayout());
        add(graphView, BorderLayout.CENTER);
    }

    public void setXIndent(int xIndent) {
        graphView.setXIndent(xIndent);

    }

    public void setYIndent(int yIndent) {
        graphView.setYIndent(yIndent);
    }

    @Override
    public boolean requestFocusInWindow() {
        return graphView.requestFocusInWindow();
    }

    public void addGraphPanel(final int weight, final boolean isXCentered) {
        if(SwingUtilities.isEventDispatchThread()) {
            graphView.addGraphPanel(weight, isXCentered);
            graphController.addGraphCluster();
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    graphView.addGraphPanel(weight, isXCentered);
                    graphController.addGraphCluster();
                }
            });
        }
    }

    public void addPreviewPanel(final int weight, final boolean isXCentered) {
        if(SwingUtilities.isEventDispatchThread()) {
            graphView.addPreviewPanel(weight, isXCentered);
            graphController.addPreviewCluster();
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    graphView.addPreviewPanel(weight, isXCentered);
                    graphController.addPreviewCluster();

                }
            });
        }
    }


    public void addGraph(final DataSeries graph, final GraphType graphType, final CompressionType compressionType, final int panelNumber) {
        if(SwingUtilities.isEventDispatchThread()) {
            graphController.addGraph(graph, graphType, compressionType, panelNumber);
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    graphController.addGraph(graph, graphType, compressionType, panelNumber);
                }
            });
        }
    }

    public void removeGraphs(final int panelNumber) {
        if(SwingUtilities.isEventDispatchThread()) {
            graphController.removeGraphs(panelNumber);
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    graphController.removeGraphs(panelNumber);
                }
            });
        }
    }

    public void addPreview(final DataSeries preview, final GraphType graphType, final CompressionType compressionType, final int panelNumber) {
        if(SwingUtilities.isEventDispatchThread()) {
            graphController.addPreview(preview, graphType, compressionType, panelNumber);
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    graphController.addPreview(preview, graphType, compressionType, panelNumber);
                }
            });
        }
    }

/*
* Add Graph to the last graph panel. If there is no graph panel create one
*/
    public void addGraph(final DataSeries graph, final GraphType graphType, final CompressionType compressionType) {
        int panelNumber = graphView.getNumberOfGraphPanels() - 1;
        if(panelNumber < 0) {
            addGraphPanel(DEFAULT_GRAPH_PANEL_WEIGHT, IS_GRAPH_X_CENTERED_DEFAULT);
            panelNumber = 0;
        }
        addGraph(graph, graphType, compressionType, panelNumber);
    }

/*
* Add Preview to the last preview panel. If there is no preview panel create one
*/
    public void addPreview(final DataSeries preview, final GraphType graphType, final CompressionType compressionType) {
        int panelNumber = graphView.getNumberOfPreviewPanels() - 1;
        if(panelNumber < 0) {
            addPreviewPanel(DEFAULT_PREVIEW_PANEL_WEIGHT, IS_PREVIEW_X_CENTERED_DEFAULT);
            panelNumber = 0;
        }
        addPreview(preview, graphType, compressionType, panelNumber);
    }


    public void addGraph(final DataSeries graph) {
        addGraph(graph, GraphType.PAPA, CompressionType.AVERAGE);
    }

    public void addPreview(final DataSeries preview,  final CompressionType compressionType) {
        addPreview(preview, GraphType.PAPA, compressionType);
    }

    public void setGraphSamplingRate(final double samplingRate) {
        if(SwingUtilities.isEventDispatchThread()) {
            graphController.setGraphFrequency(samplingRate);
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    graphController.setGraphFrequency(samplingRate);
                }
            });
        }
    }

    public void setPreviewFrequency(final double samplingRate) {
        if(SwingUtilities.isEventDispatchThread()) {
            graphController.setPreviewFrequency(samplingRate);
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    graphController.setPreviewFrequency(samplingRate);
                }
            });
        }
    }

    public void setCompression(final int compression) {
        if(SwingUtilities.isEventDispatchThread()) {
            graphController.setCompression(compression);
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    graphController.setCompression(compression);
                }
            });
        }
    }

    public void autoScroll() {
        if(SwingUtilities.isEventDispatchThread()) {
            graphController.autoScroll();
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    graphController.autoScroll();
                }
            });
        }
    }
}
