package graph;

import data.DataSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 *
 */
public class GraphsView extends JPanel implements SlotListener, ChangeListener{
    private static final Log log = LogFactory.getLog(GraphsView.class);
    private int DEFAULT_GRAPH_PANEL_WEIGHT = 4;
    private int DEFAULT_PREVIEW_PANEL_WEIGHT = 2;

    private boolean IS_GRAPH_X_CENTERED_DEFAULT = true;
    private boolean IS_PREVIEW_X_CENTERED_DEFAULT = false;

    private final Color BG_COLOR = Color.BLACK;

    private GraphsData graphsData;

    private ArrayList<GraphPanel> graphPanelList = new ArrayList<GraphPanel>();
    private ArrayList<PreviewPanel> previewPanelList = new ArrayList<PreviewPanel>();

    private JPanel paintingPanel = new JPanel();
    private JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);

    public GraphsView() {
        setLayout(new BorderLayout());
        paintingPanel.setBackground(BG_COLOR);
        paintingPanel.setLayout(new BoxLayout(paintingPanel, BoxLayout.Y_AXIS));
        add(paintingPanel, BorderLayout.CENTER);
        graphsData = new GraphsData();
        graphsData.addChangeListener(this);
        scrollBar.setModel(graphsData.getScrollBoundedRangeModel());
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
                    repaint();
                }

                if (key == KeyEvent.VK_LEFT) {
                    graphsData.moveBackward();
                    repaint();
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                graphsData.setCanvasWidth(getWidth());
                setPanelsSizes();
            }
        });
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        repaint();
    }

    @Override
    public void repaint() {
        super.repaint();
        if(scrollBar !=null) {
            scrollBar.revalidate();
            scrollBar.repaint();
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
        graphsData.setStartTime(startTime);
    }

    public int getStartIndex() {
        return graphsData.getStartIndex();
    }

    public void setTimeFrequency(double timeFrequency) {
        graphsData.setTimeFrequency(timeFrequency);
    }


    public void setCompression(int compression) {
        graphsData.setCompression(compression);
    }

    public int getCompression() {
        return graphsData.getCompression();
    }

    public void addGraphPanel(int weight, boolean isXCentered) {
        graphsData.addGraphList();
        GraphPanel panel = new GraphPanel(weight, isXCentered, graphsData);
        graphPanelList.add(panel);
        paintingPanel.add(panel);
        setPanelsSizes();
        repaint();
    }

    public void addPreviewPanel(int weight, boolean isXCentered) {
        graphsData.addPreviewList();
        PreviewPanel panel = new PreviewPanel(weight, isXCentered, graphsData);
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
            graphsData.setTimeFrequency(Math.max(graphsData.getTimeFrequency(), g.getFrequency()));
        }
        graphsData.addGraphs(graphs);
        if (graphPanelList.size() == 0) {
            addGraphPanel(DEFAULT_GRAPH_PANEL_WEIGHT, IS_GRAPH_X_CENTERED_DEFAULT);
        }
        repaint();
    }

    /*
     * Add Previews to the last preview panel. If there is no preview panel create one
     */
    public void addPreviews(DataSet... previews) {
        graphsData.addPreviews(previews);
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
        graphsData.moveSlot(newSlotIndex);
        repaint();
    }
}
