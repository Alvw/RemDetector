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

    private GraphsData graphSettings;

    private ArrayList<GraphPanel> graphPanelList = new ArrayList<GraphPanel>();
    private ArrayList<PreviewPanel> previewPanelList = new ArrayList<PreviewPanel>();

    private JPanel paintingPanel = new JPanel();
    private JPanel scrollablePanel = new JPanel();
    private JScrollPane scrollPanel = new JScrollPane(scrollablePanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    private final JScrollBar scrollBar;

    public GraphsView() {
        setLayout(new BorderLayout());
        paintingPanel.setBackground(BG_COLOR);
        paintingPanel.setLayout(new BoxLayout(paintingPanel, BoxLayout.Y_AXIS));
        add(paintingPanel, BorderLayout.CENTER);
        scrollBar =  scrollPanel.getHorizontalScrollBar();
        graphSettings = new GraphsData(scrollBar.getModel());
        scrollBar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                repaint();
            }
        });

        add(scrollPanel, BorderLayout.SOUTH);

        setFocusable(true); //only that way KeyListeners work
        requestFocusInWindow();

        // Key Listener to move Slot
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_RIGHT) {
                    graphSettings.moveForward();
                    repaint();
                }

                if (key == KeyEvent.VK_LEFT) {
                    graphSettings.moveBackward();
                    repaint();
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                graphSettings.setCanvasWidth(getWidth());
                setPanelsSizes();
            }
        });
    }


    @Override
    public void repaint() {
        super.repaint();
        if (previewPanelList != null) {
            if (previewPanelList.size() > 0) {
                scrollablePanel.setPreferredSize(new Dimension(graphSettings.getPreviewFullSize(), 0));
                // int modelPosition = graphSettings.getScrollPosition();
                //  scrollPanel.getViewport().setViewPosition(new Point(modelPosition, 0));
                scrollablePanel.revalidate(); // we always have to call component.revalidate() after changing it "directly"(outside the GUI)
                scrollPanel.repaint();
            }
        }
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
        graphSettings.addGraphList();
        GraphPanel panel = new GraphPanel(weight, isXCentered, graphSettings);
        graphPanelList.add(panel);
        paintingPanel.add(panel);
        setPanelsSizes();
        repaint();
    }

    public void addPreviewPanel(int weight, boolean isXCentered) {
        graphSettings.addPreviewList();
        PreviewPanel panel = new PreviewPanel(weight, isXCentered, graphSettings);
        panel.addSlotListener(this);
        previewPanelList.add(panel);
        paintingPanel.add(panel);
        setPanelsSizes();
        repaint();
    }


    /*
         * Add Graphs to the last graph panel. If there is no graph panel create one
         */
    public void addGraphs(DataSet... graphs) {
        for(DataSet g : graphs) {
            graphSettings.setTimeFrequency(Math.max(graphSettings.getTimeFrequency(), g.getFrequency()));
        }
        graphSettings.addGraphs(graphs);
        if (graphPanelList.size() == 0) {
            addGraphPanel(DEFAULT_GRAPH_PANEL_WEIGHT, IS_GRAPH_X_CENTERED_DEFAULT);
        }
        repaint();
    }

    /*
     * Add Previews to the last preview panel. If there is no preview panel create one
     */
    public void addPreviews(DataSet... previews) {
        graphSettings.addPreviews(previews);
        if (previewPanelList.size() == 0) {
            addGraphPanel(DEFAULT_PREVIEW_PANEL_WEIGHT, IS_PREVIEW_X_CENTERED_DEFAULT);
        }
        repaint();
    }


    private void setPanelsSizes() {
        int width = paintingPanel.getWidth();
        int height = paintingPanel.getHeight();
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
        paintingPanel.revalidate();
    }

    @Override
    public void moveSlot(int newSlotIndex) {
        graphSettings.moveSlot(newSlotIndex);
        repaint();
    }
}
