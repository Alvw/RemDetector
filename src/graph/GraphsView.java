package graph;

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
public class GraphsView extends JPanel implements SlotListener{
    private static final Log log = LogFactory.getLog(GraphsView.class);
    private int DEFAULT_GRAPH_PANEL_WEIGHT = 4;
    private int DEFAULT_PREVIEW_PANEL_WEIGHT = 2;

    private boolean IS_GRAPH_X_CENTERED_DEFAULT = true;
    private boolean IS_PREVIEW_X_CENTERED_DEFAULT = false;

    private final Color BG_COLOR = Color.BLACK;

    private GraphsData graphSettings = new GraphsData();

    private ArrayList<GraphPanel> graphPanelList = new ArrayList<GraphPanel>();
    private ArrayList<PreviewPanel> previewPanelList = new ArrayList<PreviewPanel>();

    private JPanel paintingPanel = new JPanel();
    private JPanel scrollablePanel = new JPanel();
    private JScrollPane scrollPanel = new JScrollPane(scrollablePanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    public GraphsView() {
        setLayout(new BorderLayout());
        paintingPanel.setBackground(BG_COLOR);
        paintingPanel.setLayout(new BoxLayout(paintingPanel, BoxLayout.Y_AXIS));
        add(paintingPanel, BorderLayout.CENTER);

        scrollPanel.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (e.getValueIsAdjusting()) {  // fire only when scroll was dragged by user
                    graphSettings.setScrollPosition(e.getValue());
                    repaint();
                }
            }
        });
        add(scrollPanel, BorderLayout.SOUTH);

        setFocusable(true); //only that way KeyListeners work

        // Key Listener to move Slot
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_RIGHT) {
                    graphSettings.moveForward();
                    syncView();
                }

                if (key == KeyEvent.VK_LEFT) {
                    graphSettings.moveBackward();
                    syncView();
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                graphSettings.setCanvasWidth(getWidth());
            }
        });
    }


    public void setStart(long startTime) {
        graphSettings.setStartTime(startTime);
    }

    public int getStartIndex() {
        return graphSettings.getStartIndex();
    }

    public void setTimeFrequency(double timeFrequency) {
        graphSettings.setTimeFrequency(timeFrequency);
    }


    public void setCompression(int compression) {
        graphSettings.setCompression(compression);
    }

    public int getCompression() {
        return graphSettings.getCompression();
    }

    public void addGraphPanel(int weight, boolean isXCentered) {
        GraphPanel panel = new GraphPanel(weight, isXCentered, graphSettings);
        graphPanelList.add(panel);
        paintingPanel.add(panel);
        setPanelsPreferredSizes();
        graphSettings.addGraphList();
    }

    public void addPreviewPanel(int weight, boolean isXCentered) {
        PreviewPanel panel = new PreviewPanel(weight, isXCentered, graphSettings);
        panel.addSlotListener(this);
        previewPanelList.add(panel);
        paintingPanel.add(panel);
        setPanelsPreferredSizes();
        graphSettings.addPreviewList();
    }


    /*
         * Add Graphs to the last graph panel. If there is no graph panel create one
         */
    public void addGraphs(DataSet... graphs) {
        if (graphPanelList.size() == 0) {
            addGraphPanel(DEFAULT_GRAPH_PANEL_WEIGHT, IS_GRAPH_X_CENTERED_DEFAULT);
            graphSettings.addGraphList();
        }
        graphSettings.addGraphs(graphs);
        GraphPanel panel = graphPanelList.get(graphPanelList.size() - 1);  // the last panel
        for (DataSet graphSet : graphs) {
            addGraph(panel, graphSet);
        }
    }

    /*
     * Add Previews to the last preview panel. If there is no preview panel create one
     */
    public void addPreviews(DataSet... previews) {
        if (previewPanelList.size() == 0) {
            addGraphPanel(DEFAULT_PREVIEW_PANEL_WEIGHT, IS_PREVIEW_X_CENTERED_DEFAULT);
            graphSettings.addPreviews();
        }
        graphSettings.addPreviews(previews);
        PreviewPanel panel = previewPanelList.get(previewPanelList.size() - 1);  // the last panel
        for (DataSet previewSet : previews) {
            addPreview(panel, previewSet);
        }
    }

    @Override
    public void setPreferredSize(Dimension d) {
        super.setPreferredSize(d);
        setPanelsPreferredSizes();
    }



    public void syncView() {
        syncScroll();
        repaint();
    }

    @Override
    public void repaint() {
        super.repaint();
        if(graphPanelList != null) {
            for (GraphPanel panel : graphPanelList) {
                panel.repaint();
            }
        }
        if(previewPanelList != null) {
            for (GraphPanel panel : previewPanelList) {
                panel.repaint();
            }
        }
    }

    private void syncScroll() {
        if (previewPanelList != null) {
            if (previewPanelList.size() > 0) {
                scrollablePanel.setPreferredSize(new Dimension(graphSettings.getPreviewFullSize(), 0));
                scrollPanel.getViewport().setViewPosition(new Point(graphSettings.getScrollPosition(), 0));
                scrollablePanel.revalidate(); // we always have to call component.revalidate() after changing it "directly"(outside the GUI)
                scrollPanel.repaint();
            }
        }
    }

    private void addGraph(GraphPanel graphPanel, DataSet graphSet) {
        graphPanel.addGraph(graphSet);
        graphSettings.setTimeFrequency(Math.max(graphSettings.getTimeFrequency(), graphSet.getFrequency()));
    }

    private void addPreview(PreviewPanel previewPanel, DataSet previewSet) {
        previewPanel.addGraph(previewSet);
    }

    private void setPanelsPreferredSizes() {
        Dimension d = getPreferredSize();
        int width = d.width;
        int height = d.height - scrollPanel.getPreferredSize().height;
        int sumWeight = 0;
        for (GraphPanel panel : graphPanelList) {
            sumWeight += panel.getWeight();
        }
        for (PreviewPanel panel : previewPanelList) {
            sumWeight += panel.getWeight();
        }

        for (GraphPanel panel : graphPanelList) {
            panel.setPreferredSize(new Dimension(width, height * panel.getWeight() / sumWeight));
        }
        for (PreviewPanel panel : previewPanelList) {
            panel.setPreferredSize(new Dimension(width, height * panel.getWeight() / sumWeight));
        }
    }

    @Override
    public void moveSlot(int newSlotIndex) {
        graphSettings.moveSlot(newSlotIndex);
        syncView();
    }
}
