package graph;

import data.BufferedConverter;
import data.DataSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 *
 */
public class GraphsView extends JPanel implements SlotListener {
    private static final Log log = LogFactory.getLog(GraphsView.class);
    private final int DEFAULT_GRAPH_PANEL_WEIGHT = 4;
    private final int DEFAULT_PREVIEW_PANEL_WEIGHT = 2;

    private final boolean IS_GRAPH_X_CENTERED_DEFAULT = true;
    private final boolean IS_PREVIEW_X_CENTERED_DEFAULT = false;

    private final int X_INDENT = 50;
    private final int Y_INDENT = 0;
    private final Color BG_COLOR = Color.BLACK;
    private final Color PREVIEW_BG_COLOR = new Color(10,0,10);

    private GraphsData graphsData;

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
        graphsData = new GraphsData();

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
                graphsData.setDrawingAreaWidth(getWidth() - X_INDENT);
                setPanelsSizes();
                update();
            }
        });

        scrollBar.setModel(new DefaultBoundedRangeModel() {
            @Override
            public int getMinimum() {
                return 0;
            }

            @Override
            public int getMaximum() {
                return graphsData.getPreviewsSize();
            }

            @Override
            public int getValue() {
                return graphsData.getScrollPosition();
            }

            @Override
            public void setValue(int newValue) {
                // super.setValue(newValue);
                graphsData.setScrollPosition(newValue);
                update();
            }

            @Override
            public int getExtent() {
                return graphsData.getDrawingAreaWidth();
            }
        });
    }

    private int getTimePanelHeight(Font font) {
        FontMetrics fm = getFontMetrics(font);
        return fm.getHeight() + 4;
    }

    private void addGraphTimePanel() {
        graphsTimePanel.setPreferredSize(new Dimension(getWidth(), getTimePanelHeight(graphsTimePanel.getFont())));
        graphsTimePanel.setIndentX(X_INDENT);
        graphsTimePanel.setBackground(BG_COLOR);
        graphsMainPanel.add(graphsTimePanel, BorderLayout.NORTH);
    }

    private void addPreviewTimePanel() {
        previewsTimePanel.setPreferredSize(new Dimension(getWidth(), getTimePanelHeight(previewsTimePanel.getFont())));
        previewsTimePanel.setIndentX(X_INDENT);
        previewsTimePanel.setBackground(PREVIEW_BG_COLOR);
        previewsMainPanel.add(previewsTimePanel, BorderLayout.SOUTH);
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
        graphsTimePanel.setStartIndex(graphsData.getStartIndex());
        graphsTimePanel.setFrequency(graphsData.getTimeFrequency());
        graphsTimePanel.setStartTime(graphsData.getStartTime());
        previewsTimePanel.setStartIndex(graphsData.getScrollPosition());
        previewsTimePanel.setFrequency(graphsData.getPreviewFrequency());
        previewsTimePanel.setStartTime(graphsData.getStartTime());
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
        if (graphsTimePanel != null) {
            graphsTimePanel.repaint();
        }
        if (previewsTimePanel != null) {
            previewsTimePanel.repaint();
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

    public void setPreviewTimeFrequency(double previewTimeFrequency) {
        graphsData.setPreviewTimeFrequency(previewTimeFrequency);
        setPanelsPreviews();
    }

    public int getCompression() {
        return graphsData.getCompression();
    }

    public void setCompression(int compression) {
        graphsData.setCompression(compression);
        setPanelsPreviews();
    }

    public void addGraphPanel(int weight, boolean isXCentered) {
        graphsData.addGraphList();
        GraphPanel panel = new GraphPanel(weight, isXCentered);
        panel.setIndentX(X_INDENT);
        panel.setIndentY(Y_INDENT);
        panel.setBackground(BG_COLOR);
        if (graphPanelList.size() == 0) {
            addGraphTimePanel();
        }
        graphPanelList.add(panel);
        graphsPaintingPanel.add(panel);
        setPanelsSizes();

    }

    public void addPreviewPanel(int weight, boolean isXCentered) {
        graphsData.addPreviewList();
        GraphPanel panel = new GraphPanel(weight, isXCentered);
        panel.setIndentX(X_INDENT);
        panel.setIndentY(Y_INDENT);
        panel.setBackground(PREVIEW_BG_COLOR);
        panel.addSlotListener(this);
        if (previewPanelList.size() == 0) {
            addPreviewTimePanel();
        }
        previewPanelList.add(panel);
        previewsPaintingPanel.add(panel);
        setPanelsSizes();
    }

    private void setPanelsGraphs() {
        double timeFrequency = graphsData.getTimeFrequency();
        for (int i = 0; i < graphPanelList.size(); i++) {
            java.util.List<DataSet> inputGraphs = graphsData.getGraphList(i);
            java.util.List<DataSet> resultingGraphs = new ArrayList<DataSet>();
            for (DataSet graph : inputGraphs) {
                resultingGraphs.add(new FrequencyConverter(graph, timeFrequency, CompressionType.AVERAGE));
            }
            graphPanelList.get(i).setGraphs(resultingGraphs);
        }
    }

    private void setPanelsPreviews() {
        double previewTimeFrequency = graphsData.getPreviewFrequency();
        for (int i = 0; i < previewPanelList.size(); i++) {
            java.util.List<DataSet> inputPreviews = graphsData.getPreviewList(i);
            java.util.List<DataSet> resultingPreviews = new ArrayList<DataSet>();
            for (DataSet preview : inputPreviews) {
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

    @Override
    public void moveSlot(int newSlotIndex) {
        graphsData.moveSlot(newSlotIndex);
        update();
    }
}
