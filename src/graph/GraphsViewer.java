package graph;

import data.DataStream;

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
    public static final int COMPRESSED_POINT_DISTANCE_MSEC = 15000;
    public  int compression = COMPRESSED_POINT_DISTANCE_MSEC / 20;

    private ArrayList<GraphPanel> graphPanels = new ArrayList<GraphPanel>();
    private ArrayList<CompressedGraphPanel> compressedGraphPanels = new ArrayList<CompressedGraphPanel>();

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
        return graphPanels.get(0).getStartIndex();
    }

    public void setStart(long startTime, double frequency ) {
        int point_distance_msec = (int) (1000/frequency);
        compression =  COMPRESSED_POINT_DISTANCE_MSEC / point_distance_msec;
        for (GraphPanel panel : graphPanels) {
            panel.setStart(startTime, point_distance_msec);
        }
        for (CompressedGraphPanel panel : compressedGraphPanels) {
            panel.setStart(startTime, point_distance_msec);
            panel.setCompression(compression);
        }
    }

    public void setCompression(int compression) {
        this.compression = compression;
        for (CompressedGraphPanel panel : compressedGraphPanels) {
            panel.setCompression(compression);
        }
    }

    public int getCompression() {
         return compression;
    }

    public void addGraphPanel(int weight, boolean isXCentered) {
        GraphPanel panel = new GraphPanel(weight, isXCentered);
        graphPanels.add(panel);
        PaintingPanel.add(panel);
        setPanelsPreferredSizes();
    }

    public void addCompressedGraphPanel(int weight, boolean isXCentered) {
        CompressedGraphPanel panel = new CompressedGraphPanel(weight, isXCentered);
        panel.addSlotListener(viewController);
        compressedGraphPanels.add(panel);
        PaintingPanel.add(panel);
        setPanelsPreferredSizes();
    }

    public void addGraph(int panelNumber, DataStream graphData) {
        if (panelNumber < graphPanels.size()) {
            graphPanels.get(panelNumber).addGraph(graphData);
        }
    }

    public void addCompressedGraph(int panelNumber, DataStream graphData) {
        if (panelNumber < compressedGraphPanels.size()) {
            compressedGraphPanels.get(panelNumber).addGraph(graphData);
        }
    }

    @Override
    public void setPreferredSize(Dimension d) {
        super.setPreferredSize(d);
        setPanelsPreferredSizes();
    }

    public void syncView() {
        viewController.autoScroll();
        for(GraphPanel panel : graphPanels ) {
            panel.repaint();
        }
    }

    private void setPanelsPreferredSizes() {
        Dimension d = getPreferredSize();
        int width = d.width;
        int height = d.height - scrollPanel.getPreferredSize().height;
        int sumWeight = 0;
        for (GraphPanel panel : graphPanels) {
            sumWeight += panel.getWeight();
        }
        for (CompressedGraphPanel panel : compressedGraphPanels) {
            sumWeight += panel.getWeight();
        }

        for (GraphPanel panel : graphPanels) {
            panel.setPreferredSize(new Dimension(width, height * panel.getWeight() / sumWeight));
        }
        for (CompressedGraphPanel panel : compressedGraphPanels) {
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

            for (CompressedGraphPanel panel : compressedGraphPanels) {
                panel.setSlotIndex(newSlotIndex);
                panel.setStartIndex(compressedGraphsNewStartIndex);
                panel.repaint();
            }

            int GraphsNewStartIndex = newSlotIndex * compression;
            for (GraphPanel panel : graphPanels) {
                panel.setStartIndex(GraphsNewStartIndex);
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
                for (GraphPanel panel : graphPanels) {
                    panel.setStartIndex(graphsMaxStartIndex);
                }
                if (slotMaxIndex > slotIndex) {
                    int compressedGraphsMaxStartIndex = slotMaxIndex + getSlotWidth() - getWorkspaceWidth();
                    if (compressedGraphsMaxStartIndex < 0) {
                        compressedGraphsMaxStartIndex = 0;
                    }
                    for (CompressedGraphPanel panel : compressedGraphPanels) {
                        panel.setSlotIndex(slotMaxIndex);
                        panel.setStartIndex(compressedGraphsMaxStartIndex);
                    }
                }
            }
            syncScroll();
            repaint();
        }


        private void syncScroll() {
            if (compressedGraphPanels != null) {
                if (compressedGraphPanels.size() > 0) {
                    CompressedGraphPanel panel = compressedGraphPanels.get(0);
                    scrollablePanel.setPreferredSize(new Dimension(panel.getFullWidth(), 0));
                    scrollPanel.getViewport().setViewPosition(new Point(panel.getStartIndex(), 0));
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

            for (CompressedGraphPanel panel : compressedGraphPanels) {
                panel.setSlotIndex(newSlotIndex);
                panel.setStartIndex(newStartIndex);
                panel.repaint();
            }

            int GraphsNewStartIndex = newSlotIndex * compression;
            for (GraphPanel panel : graphPanels) {
                panel.setStartIndex(GraphsNewStartIndex);
                panel.repaint();
            }
        }

        private void moveScroll(int newScrollPosition) {
            moveCompressedGraphs(newScrollPosition);
        }


        private int getSlotWidth() {
            if (compressedGraphPanels == null) {
                return 0;
            }
            if (compressedGraphPanels.size() == 0) {
                return 0;
            }
            return compressedGraphPanels.get(0).getSlotWidth();
        }


        private int getSlotIndex() {
            if (compressedGraphPanels == null) {
                return 0;
            }
            if (compressedGraphPanels.size() == 0) {
                return 0;
            }
            return compressedGraphPanels.get(0).getSlotIndex();
        }

        private int getGraphsSize() {
            if (graphPanels == null) {
                return 0;
            }
            if (graphPanels.size() == 0) {
                return 0;
            }
            return graphPanels.get(0).getGraphsSize();
        }

        private int getGraphsStartIndex() {
            if (graphPanels == null) {
                return 0;
            }
            if (graphPanels.size() == 0) {
                return 0;
            }
            return graphPanels.get(0).getStartIndex();
        }

        private int getCompressedGraphsSize() {
            if (compressedGraphPanels == null) {
                return 0;
            }
            if (compressedGraphPanels.size() == 0) {
                return 0;
            }
            return compressedGraphPanels.get(0).getGraphsSize();
        }


        private int getCompressedGraphsStartIndex() {
            if (compressedGraphPanels == null) {
                return 0;
            }
            if (compressedGraphPanels.size() == 0) {
                return 0;
            }
            return compressedGraphPanels.get(0).getStartIndex();
        }


        protected int getWorkspaceWidth() {
            return (getSize().width - GraphPanel.X_INDENT);
        }

    }
}
