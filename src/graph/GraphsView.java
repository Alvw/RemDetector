package graph;

import data.BufferedConverter;
import data.DataSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 *
 */
public class GraphsView extends JPanel implements SlotListener {
    private static final Log log = LogFactory.getLog(GraphsView.class);
    private static final int DEFAULT_GRAPH_PANEL_WEIGHT = 1;
    private static final int DEFAULT_PREVIEW_PANEL_WEIGHT = 1;

    private static final boolean IS_GRAPH_X_CENTERED_DEFAULT = true;
    private static final boolean IS_PREVIEW_X_CENTERED_DEFAULT = true;

    private static final int X_INDENT = 50;
    private static final int Y_INDENT = 2;
    private static final Color BG_COLOR = Color.BLACK;
    private static final Color PREVIEW_BG_COLOR = new Color(25,25,25);

    private GraphsModel graphsModel;

    private ArrayList<GraphPanel> graphPanelList = new ArrayList<GraphPanel>();
    private ArrayList<GraphPanel> previewPanelList = new ArrayList<GraphPanel>();

    private JPanel mainPanel = new JPanel();
    private JPanel graphsMainPanel = new JPanel();
    private JPanel previewsMainPanel = new JPanel();
    private JPanel graphsPaintingPanel = new JPanel();
    private JPanel previewsPaintingPanel = new JPanel();
    private TimePanel graphsTimePanel = new TimePanel();
    private TimePanel previewsTimePanel = new TimePanel();
    private JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);

    public GraphsView() {
        graphsModel = new GraphsModel();

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        add(scrollBar, BorderLayout.SOUTH);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(graphsMainPanel, BorderLayout.CENTER);
        mainPanel.add(previewsMainPanel, BorderLayout.SOUTH);
        graphsMainPanel.setLayout(new BorderLayout());
        previewsMainPanel.setLayout(new BorderLayout());
        graphsMainPanel.add(graphsPaintingPanel, BorderLayout.CENTER);
        previewsMainPanel.add(previewsPaintingPanel, BorderLayout.CENTER);
        graphsPaintingPanel.setLayout(new BoxLayout(graphsPaintingPanel, BoxLayout.Y_AXIS));
        previewsPaintingPanel.setLayout(new BoxLayout(previewsPaintingPanel, BoxLayout.Y_AXIS));
        graphsPaintingPanel.setBackground(BG_COLOR);
        previewsPaintingPanel.setBackground(PREVIEW_BG_COLOR);

        setFocusable(true); //only that way KeyListeners work
        requestFocusInWindow();

        // Key Listener to move Slot
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_RIGHT) {
                    moveSlotForward();
                }

                if (key == KeyEvent.VK_LEFT) {
                    moveSlotBackward();
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                setDrawingAreaWidth(getWidth() - X_INDENT);
            }
        });

        scrollBar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                moveScroll(e.getValue());
            }
        });
    }

    public void synchronize() {
        if (graphsModel != null) {
            graphsModel.autoScroll();
        }
        update();
    }

    public int getStartIndex() {
        return graphsModel.getStartIndex();
    }

    public void setTimeFrequency(double timeFrequency) {
        graphsModel.setTimeFrequency(timeFrequency);
        updatePanelsGraphs();
    }

    public void setPreviewTimeFrequency(double previewTimeFrequency) {
        graphsModel.setPreviewTimeFrequency(previewTimeFrequency);
        updatePanelsGraphs();
    }


    public void setCompression(int compression) {
        graphsModel.setCompression(compression);
        updatePanelsGraphs();
    }

    public void addGraphPanel(int weight, boolean isXCentered) {
        graphsModel.addGraphCluster();
        GraphPanel panel = new GraphPanel(weight, isXCentered);
        panel.setIndentX(X_INDENT);
        panel.setIndentY(Y_INDENT);
        panel.setBackground(BG_COLOR);
        TimeAxisPainter timeAxisPainter = new TimeAxisPainter();
        timeAxisPainter.isValuesPaint(false);
        panel.setTimeAxisPainter(timeAxisPainter);
        if (graphPanelList.size() == 0) {
            addGraphTimePanel();
        }
        graphPanelList.add(panel);
        graphsPaintingPanel.add(panel);
        setPanelsSizes();
    }

    public void addPreviewPanel(int weight, boolean isXCentered) {
        graphsModel.addPreviewCluster();
        GraphPanel panel = new GraphPanel(weight, isXCentered);
        panel.setIndentX(X_INDENT);
        panel.setIndentY(Y_INDENT);
        panel.setBackground(PREVIEW_BG_COLOR);
        panel.addSlotListener(this);
        TimeAxisPainter timeAxisPainter = new TimeAxisPainter();
        timeAxisPainter.isValuesPaint(false);
        panel.setTimeAxisPainter(timeAxisPainter);
        if (previewPanelList.size() == 0) {
            addPreviewTimePanel();
        }
        previewPanelList.add(panel);
        previewsPaintingPanel.add(panel);
        setPanelsSizes();
    }

    /*
    * Add Graph to the last graph panel. If there is no graph panel create one
    */
    public void addGraph(DataSet graph) {
        graphsModel.addGraph(graph);
        if (graphPanelList.size() == 0) {
            addGraphPanel(DEFAULT_GRAPH_PANEL_WEIGHT, IS_GRAPH_X_CENTERED_DEFAULT);
        }
        updatePanelsGraphs();
    }

    /*
     * Add Previews to the last preview panel. If there is no preview panel create one
     */
    public void addPreview(DataSet preview) {
        addPreview(CompressionType.AVERAGE, preview);
    }

    public void addPreview(CompressionType compressionType, DataSet preview) {
        graphsModel.addPreview(preview);
        if (previewPanelList.size() == 0) {
            addPreviewPanel(DEFAULT_PREVIEW_PANEL_WEIGHT, IS_PREVIEW_X_CENTERED_DEFAULT);
        }
        updatePanelsGraphs();
    }


    @Override
    public void repaint() {
        super.repaint();
        if (scrollBar != null) {
            scrollBar.revalidate();
            scrollBar.repaint();
        }
        if (graphPanelList != null) {
            for (GraphPanel panel : graphPanelList) {
                panel.repaint();
            }
        }
        if (previewPanelList != null) {
            for (GraphPanel panel : previewPanelList) {
                panel.repaint();
            }
        }
        if (graphsTimePanel != null) {
            graphsTimePanel.repaint();
        }
        if (previewsTimePanel != null) {
            previewsTimePanel.repaint();
        }
    }

    private void updateScroll() {
        int newScrollValue = graphsModel.getScrollPosition();
        int newScrollMaximum = graphsModel.getPreviewsSize();
        int newScrollExtent = graphsModel.getDrawingAreaWidth();
        BoundedRangeModel scrollModel = scrollBar.getModel();
        if(scrollModel.getExtent() != newScrollExtent) {
            scrollModel.setExtent(newScrollExtent);
        }
        if(scrollModel.getMaximum() != newScrollMaximum) {
            scrollModel.setMaximum(newScrollMaximum);
        }
        if(scrollModel.getValue() != newScrollValue) {
            scrollModel.setValue(newScrollValue);
        }
    }

    private void update() {
        updateScroll();
        for (GraphPanel graphPanel : graphPanelList) {
            graphPanel.setStartIndex(graphsModel.getStartIndex());
        }
        for (GraphPanel previewPanel : previewPanelList) {
            previewPanel.setStartIndex(graphsModel.getScrollPosition());
            previewPanel.setSlotPosition(graphsModel.getSlotPosition());
            previewPanel.setSlotWidth(graphsModel.getSlotWidth());
        }
        graphsTimePanel.setStartIndex(graphsModel.getStartIndex());
        graphsTimePanel.setFrequency(graphsModel.getTimeFrequency());
        graphsTimePanel.setStartTime(graphsModel.getStartTime());
        previewsTimePanel.setStartIndex(graphsModel.getScrollPosition());
        previewsTimePanel.setFrequency(graphsModel.getPreviewTimeFrequency());
        previewsTimePanel.setStartTime(graphsModel.getStartTime());
        repaint();
    }

    private void updatePanelsGraphs() {
        double timeFrequency = graphsModel.getTimeFrequency();
        for (int i = 0; i < graphPanelList.size(); i++) {
            java.util.List<DataSet> inputGraphs = graphsModel.getGraphList(i);
            java.util.List<DataSet> resultingGraphs = new ArrayList<DataSet>();
            for (DataSet graph : inputGraphs) {
                resultingGraphs.add(new FrequencyConverter(graph, timeFrequency, CompressionType.AVERAGE));
            }
            graphPanelList.get(i).setGraphs(resultingGraphs);
        }
        double previewTimeFrequency = graphsModel.getPreviewTimeFrequency();
        for (int i = 0; i < previewPanelList.size(); i++) {
            java.util.List<DataSet> inputPreviews = graphsModel.getPreviewList(i);
            java.util.List<DataSet> resultingPreviews = new ArrayList<DataSet>();
            for (DataSet preview : inputPreviews) {
                FrequencyConverter frequencyConverter = new FrequencyConverter(preview, previewTimeFrequency, CompressionType.MAX);
                resultingPreviews.add(new BufferedConverter(frequencyConverter));
            }
            previewPanelList.get(i).setGraphs(resultingPreviews);
        }
        update();
    }

    private void setPanelsSizes() {
        int width = graphsPaintingPanel.getWidth();
        int height = graphsPaintingPanel.getHeight() + previewsPaintingPanel.getHeight();
        int sumWeight = 0;
        for (GraphPanel panel : graphPanelList) {
            sumWeight += panel.getWeight();
        }
        for (GraphPanel panel : previewPanelList) {
            sumWeight += panel.getWeight();
        }
        for (GraphPanel panel : graphPanelList) {
            panel.setPreferredSize(new Dimension(width, height * panel.getWeight() / sumWeight));
        }
        for (GraphPanel panel : previewPanelList) {
            panel.setPreferredSize(new Dimension(width, height * panel.getWeight() / sumWeight));
        }
        graphsPaintingPanel.revalidate();
        previewsPaintingPanel.revalidate();
    }

    private void addGraphTimePanel() {
        int panelHeight = getFontMetrics(graphsTimePanel.getFont()).getHeight() + 4;
        graphsTimePanel.setPreferredSize(new Dimension(getWidth(), panelHeight));
        graphsTimePanel.setIndentX(X_INDENT);
        graphsTimePanel.setBackground(BG_COLOR);
        graphsMainPanel.add(graphsTimePanel, BorderLayout.NORTH);
    }

    private void addPreviewTimePanel() {
        int panelHeight = getFontMetrics(previewsTimePanel.getFont()).getHeight() + 4;
        previewsTimePanel.setPreferredSize(new Dimension(getWidth(), panelHeight));
        previewsTimePanel.setIndentX(X_INDENT);
        previewsTimePanel.setBackground(PREVIEW_BG_COLOR);
        previewsMainPanel.add(previewsTimePanel, BorderLayout.SOUTH);
    }

    private void moveSlotForward() {
        graphsModel.moveSlotForward();
        update();
    }
    private void moveSlotBackward() {
        graphsModel.moveSlotBackward();
        update();
    }
    private void setDrawingAreaWidth(int drawingAreaWidth) {
        graphsModel.setDrawingAreaWidth(drawingAreaWidth);
        setPanelsSizes();
        update();
    }

    private void moveScroll(int scrollPosition) {
        graphsModel.setScrollPosition(scrollPosition);
        update();
    }

    @Override
    public void moveSlot(int slotPosition) {
        graphsModel.moveSlot(slotPosition);
        update();
    }
}
