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
    private int DEFAULT_GRAPH_PANEL_WEIGHT = 4;
    private int DEFAULT_PREVIEW_PANEL_WEIGHT = 2;

    private boolean IS_GRAPH_X_CENTERED_DEFAULT = true;
    private boolean IS_PREVIEW_X_CENTERED_DEFAULT = false;

    private final Color BG_COLOR = Color.BLACK;

    private GraphsData graphsData;

    private ArrayList<GraphPanel> graphPanelList = new ArrayList<GraphPanel>();
    private ArrayList<GraphPanel> previewPanelList = new ArrayList<GraphPanel>();

    private JPanel paintingPanel = new JPanel();
    private JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);

    public GraphsView() {
        setLayout(new BorderLayout());
        paintingPanel.setBackground(BG_COLOR);
        paintingPanel.setLayout(new BoxLayout(paintingPanel, BoxLayout.Y_AXIS));
        add(paintingPanel, BorderLayout.CENTER);
        graphsData = new GraphsData();
        add(scrollBar, BorderLayout.SOUTH);
        setFocusable(true); //only that way KeyListeners work
        requestFocusInWindow();

        // Key Listener to move Slot
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_RIGHT) {
                    graphsData.moveForward();
                    update();
                }

                if (key == KeyEvent.VK_LEFT) {
                    graphsData.moveBackward();
                    update();
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                graphsData.setCanvasWidth(getWidth());
                setPanelsSizes();
                update();
            }
        });

        scrollBar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                graphsData.setScrollPosition(e.getValue());
                update();
            }
        });
    }

    private void updateScrollModel() {
        int newScrollValue = graphsData.getScrollPosition();
        int newScrollMaximum = graphsData.getPreviewFullSize();
        int newScrollExtent = graphsData.getCanvasWidth();
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
        for (GraphPanel graphPanel : graphPanelList) {
            graphPanel.setStartIndex(graphsData.getStartIndex());
        }
        for (GraphPanel previewPanel : previewPanelList) {
            previewPanel.setStartIndex(graphsData.getScrollPosition());
            previewPanel.setSlotPosition(graphsData.getSlotPosition());
            previewPanel.setSlotWidth(graphsData.getSlotWidth());
        }
        updateScrollModel();
        repaint();
    }

    public void synchronize() {
        if (graphsData != null) {
            graphsData.autoScroll();
        }
        update();
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
    }


    public int getStartIndex() {
        return graphsData.getStartIndex();
    }

    public void setTimeFrequency(double timeFrequency) {
        graphsData.setTimeFrequency(timeFrequency);
        setPanelsGraphs();
        setPanelsPreviews();
    }


    public void setCompression(int compression) {
        graphsData.setCompression(compression);
        setPanelsPreviews();
    }

    public void setPreviewTimeFrequency(double previewTimeFrequency) {
        graphsData.setPreviewTimeFrequency(previewTimeFrequency);
        setPanelsPreviews();
    }

    public int getCompression() {
        return graphsData.getCompression();
    }

    public void addGraphPanel(int weight, boolean isXCentered) {
        graphsData.addGraphList();
        GraphPanel panel = new GraphPanel(weight, isXCentered);
        panel.setIndentX(graphsData.X_INDENT);
        panel.setIndentY(graphsData.Y_INDENT);
        graphPanelList.add(panel);
        paintingPanel.add(panel);
        setPanelsSizes();
    }

    public void addPreviewPanel(int weight, boolean isXCentered) {
        graphsData.addPreviewList();
        GraphPanel panel = new GraphPanel(weight, isXCentered);
        panel.setIndentX(graphsData.X_INDENT);
        panel.setIndentY(graphsData.Y_INDENT);
        panel.addSlotListener(this);
        previewPanelList.add(panel);
        paintingPanel.add(panel);
        setPanelsSizes();
    }

    private void setPanelsGraphs() {
        double timeFrequency = graphsData.getTimeFrequency();
        for(int i = 0; i < graphPanelList.size(); i++){
            java.util.List<DataSet> inputGraphs = graphsData.getGraphList(i);
            java.util.List<DataSet> resultingGraphs = new ArrayList<DataSet>();
            for(DataSet graph : inputGraphs) {
                resultingGraphs.add(new FrequencyConverter(graph, timeFrequency, CompressionType.AVERAGE));
            }
            graphPanelList.get(i).setGraphs(resultingGraphs);
        }
    }

    private void setPanelsPreviews() {
        double previewTimeFrequency = graphsData.getPreviewFrequency();
        for(int i = 0; i < previewPanelList.size(); i++){
            java.util.List<DataSet> inputPreviews = graphsData.getPreviewList(i);
            java.util.List<DataSet> resultingPreviews = new ArrayList<DataSet>();
            for(DataSet preview : inputPreviews) {
                FrequencyConverter frequencyConverter = new FrequencyConverter(preview, previewTimeFrequency, CompressionType.AVERAGE);
                resultingPreviews.add(new BufferedConverter(frequencyConverter));
            }
            previewPanelList.get(i).setGraphs(resultingPreviews);
        }

    }

    /*
    * Add Graphs to the last graph panel. If there is no graph panel create one
    */
    public void addGraphs(DataSet... graphs) {
        graphsData.addGraphs(graphs);
        if (graphPanelList.size() == 0) {
            addGraphPanel(DEFAULT_GRAPH_PANEL_WEIGHT, IS_GRAPH_X_CENTERED_DEFAULT);
        }
        setPanelsGraphs();
    }

    /*
     * Add Previews to the last preview panel. If there is no preview panel create one
     */
    public void addPreviews(DataSet... previews) {
        addPreviews(CompressionType.AVERAGE, previews);
    }

    public void addPreviews(CompressionType compressionType, DataSet... previews) {
        graphsData.addPreviews(previews);
        if (previewPanelList.size() == 0) {
            addPreviewPanel(DEFAULT_PREVIEW_PANEL_WEIGHT, IS_PREVIEW_X_CENTERED_DEFAULT);
        }
        setPanelsPreviews();
    }


    private void setPanelsSizes() {
        int width = paintingPanel.getWidth();
        int height = paintingPanel.getHeight();
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
        paintingPanel.revalidate();
    }

    @Override
    public void moveSlot(int newSlotIndex) {
        graphsData.moveSlot(newSlotIndex);
        update();
    }
}
