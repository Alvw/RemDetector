package graph;

import data.DataSet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: galafit
 * Date: 07/05/14
 * Time: 15:04
 * To change this template use File | Settings | File Templates.
 */
public class GraphsViewer extends JPanel {
    public  int compression = 750;
    private double timeFrequency = 0;
    private double previewTimeFrequency = 1.0/15;

    private ArrayList<GraphPanel> graphPanelList = new ArrayList<GraphPanel>();
    private ArrayList<PreviewPanel> previewPanelList = new ArrayList<PreviewPanel>();

    private JPanel PaintingPanel = new JPanel();
    private JPanel scrollablePanel = new JPanel();
    private JScrollPane scrollPanel = new JScrollPane(scrollablePanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    private ViewController viewController = new ViewController();

    public GraphsViewer() {
        setLayout(new BorderLayout());

        PaintingPanel.setLayout(new BoxLayout(PaintingPanel, BoxLayout.Y_AXIS));
        add(PaintingPanel, BorderLayout.CENTER);

        scrollPanel.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (e.getValueIsAdjusting()) {  // fire only when scroll was dragged by user
                    viewController.moveScroll(e.getValue());
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
                    viewController.moveSlotForward();
                }

                if (key == KeyEvent.VK_LEFT) {
                    viewController.moveSlotBackward();
                }
            }
        });
    }

    public int getStartIndex() {
        return graphPanelList.get(0).getStartPoint();
    }


    public void setStart(long startTime) {
        for (GraphPanel panel : graphPanelList) {
            panel.setStart(startTime, timeFrequency);
        }
        compression =  (int)(timeFrequency / previewTimeFrequency);
        for (PreviewPanel panel : previewPanelList) {
            panel.setStart(startTime, previewTimeFrequency);
            panel.setCompression(compression);
        }
    }

    public void setCompression(int compression) {
        this.compression = compression;
        for (PreviewPanel panel : previewPanelList) {
            panel.setCompression(compression);
        }
    }

    public int getCompression() {
         return compression;
    }

    public void addGraphPanel(int weight, boolean isXCentered) {
        GraphPanel panel = new GraphPanel(weight, isXCentered);
        graphPanelList.add(panel);
        PaintingPanel.add(panel);
        setPanelsPreferredSizes();
    }

    public void addCompressedGraphPanel(int weight, boolean isXCentered) {
        PreviewPanel panel = new PreviewPanel(weight, isXCentered);
        panel.addSlotListener(viewController);
        previewPanelList.add(panel);
        PaintingPanel.add(panel);
        setPanelsPreferredSizes();
    }

    public void addGraph(int panelNumber, DataSet graphData) {
        if (panelNumber < graphPanelList.size()) {
            graphPanelList.get(panelNumber).addGraph(graphData);
            timeFrequency = Math.max(timeFrequency, graphData.getFrequency());
        }
    }

    public void addCompressedGraph(int panelNumber, DataSet graphData) {
        if (panelNumber < previewPanelList.size()) {
            previewPanelList.get(panelNumber).addGraph(graphData);
        }
    }

    @Override
    public void setPreferredSize(Dimension d) {
        super.setPreferredSize(d);
        setPanelsPreferredSizes();
    }

    public void syncView() {
        viewController.autoScroll();
        for(GraphPanel panel : graphPanelList) {
            panel.repaint();
        }
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

    class ViewController implements SlotListener {
        private static final int AUTO_SCROLL_GAP = 10; // bigger GAP - less precision need slot to start autoscroll

        @Override
        public void moveSlot(int newSlotIndex) {
            int slotMaxIndex = getSlotMaxIndex();

            if (newSlotIndex < 0) {
                newSlotIndex = 0;
            }
            if (newSlotIndex > slotMaxIndex) {
                newSlotIndex = slotMaxIndex;
            }

            int compressedGraphsNewStartIndex = getCompressedGraphsStartIndex();

            if ((isSlotInWorkspace(newSlotIndex, compressedGraphsNewStartIndex) == -1)) {
                compressedGraphsNewStartIndex = newSlotIndex;
            }
            if ((isSlotInWorkspace(newSlotIndex, compressedGraphsNewStartIndex) == 1)) {
                compressedGraphsNewStartIndex = newSlotIndex + getSlotWidth() - getWorkspaceWidth();
            }

            for (PreviewPanel panel : previewPanelList) {
                panel.setSlotIndex(newSlotIndex);
                panel.setStartPoint(compressedGraphsNewStartIndex);
                panel.repaint();
            }

            int GraphsNewStartIndex = newSlotIndex * compression;
            for (GraphPanel panel : graphPanelList) {
                panel.setStartPoint(GraphsNewStartIndex);
                panel.repaint();
            }

            syncScroll();
        }


        int isSlotInWorkspace(int slotIndex, int startIndex) {
            int slotWorkspacePosition = slotIndex - startIndex;
            if (slotWorkspacePosition <= 0) {
                return -1;
            }
            if (slotWorkspacePosition >= (getWorkspaceWidth() - getSlotWidth())) {
                return 1;
            }

            return 0;
        }

        public void moveSlotForward() {
            moveSlot(getSlotIndex() + 1);
        }

        public void moveSlotBackward() {
            if (isAutoScroll(getSlotMaxIndex(), getSlotIndex())) {
                moveSlot(getSlotIndex() - AUTO_SCROLL_GAP - 1); //to stop autoScroll
            } else {
                moveSlot(getSlotIndex() - 1);
            }
        }


        private int getSlotMaxIndex() {
            int slotMaxIndex;
            slotMaxIndex = getCompressedGraphsSize() - getSlotWidth();
            return Math.max(0,slotMaxIndex);
        }

        private boolean isAutoScroll(int slotMaxIndex, int slotIndex) {
            return (slotMaxIndex <= (slotIndex + AUTO_SCROLL_GAP));
        }

        private void autoScroll() {
            int graphsMaxStartIndex = getGraphsSize() - getWorkspaceWidth();
            if (graphsMaxStartIndex < 0) {
                graphsMaxStartIndex = 0;
            }

            int slotMaxIndex;
            slotMaxIndex = graphsMaxStartIndex / compression;
            int slotIndex = getSlotIndex();

            if (isAutoScroll(slotMaxIndex, slotIndex)) {
                for (GraphPanel panel : graphPanelList) {
                    panel.setStartPoint(graphsMaxStartIndex);
                }
                if (slotMaxIndex > slotIndex) {
                    int compressedGraphsMaxStartIndex = slotMaxIndex + getSlotWidth() - getWorkspaceWidth();
                    if (compressedGraphsMaxStartIndex < 0) {
                        compressedGraphsMaxStartIndex = 0;
                    }
                    for (PreviewPanel panel : previewPanelList) {
                        panel.setSlotIndex(slotMaxIndex);
                        panel.setStartPoint(compressedGraphsMaxStartIndex);
                    }
                }
            }
            syncScroll();
            repaint();
        }


        private void syncScroll() {
            if (previewPanelList != null) {
                if (previewPanelList.size() > 0) {
                    PreviewPanel panel = previewPanelList.get(0);
                    scrollablePanel.setPreferredSize(new Dimension(panel.getFullWidth(), 0));
                    scrollPanel.getViewport().setViewPosition(new Point(panel.getStartPoint(), 0));
                    scrollablePanel.revalidate(); // we always have to call component.revalidate() after changing it "directly"(outside the GUI)
                    scrollPanel.repaint();
                }
            }
        }

        private void moveCompressedGraphs(int newStartIndex) {
            int newSlotIndex = getSlotIndex();
            if ((isSlotInWorkspace(newSlotIndex, newStartIndex) == -1)) {
                newSlotIndex = newStartIndex;
            }
            if ((isSlotInWorkspace(newSlotIndex, newStartIndex) == 1)) {
                newSlotIndex = newStartIndex + getWorkspaceWidth() - getSlotWidth();
            }

            for (PreviewPanel panel : previewPanelList) {
                panel.setSlotIndex(newSlotIndex);
                panel.setStartPoint(newStartIndex);
                panel.repaint();
            }

            int GraphsNewStartIndex = newSlotIndex * compression;
            for (GraphPanel panel : graphPanelList) {
                panel.setStartPoint(GraphsNewStartIndex);
                panel.repaint();
            }
        }

        private void moveScroll(int newScrollPosition) {
            moveCompressedGraphs(newScrollPosition);
        }


        private int getSlotWidth() {
            if (previewPanelList == null) {
                return 0;
            }
            if (previewPanelList.size() == 0) {
                return 0;
            }
            return previewPanelList.get(0).getSlotWidth();
        }


        private int getSlotIndex() {
            if (previewPanelList == null) {
                return 0;
            }
            if (previewPanelList.size() == 0) {
                return 0;
            }
            return previewPanelList.get(0).getSlotIndex();
        }

        private int getGraphsSize() {
            if (graphPanelList == null) {
                return 0;
            }
            if (graphPanelList.size() == 0) {
                return 0;
            }
            return graphPanelList.get(0).getGraphsSize();
        }

        private int getGraphsStartIndex() {
            if (graphPanelList == null) {
                return 0;
            }
            if (graphPanelList.size() == 0) {
                return 0;
            }
            return graphPanelList.get(0).getStartPoint();
        }

        private int getCompressedGraphsSize() {
            if (previewPanelList == null) {
                return 0;
            }
            if (previewPanelList.size() == 0) {
                return 0;
            }
            return previewPanelList.get(0).getGraphsSize();
        }


        private int getCompressedGraphsStartIndex() {
            if (previewPanelList == null) {
                return 0;
            }
            if (previewPanelList.size() == 0) {
                return 0;
            }
            return previewPanelList.get(0).getStartPoint();
        }


        protected int getWorkspaceWidth() {
            return (getSize().width - GraphPanel.X_INDENT);
        }

    }
}
