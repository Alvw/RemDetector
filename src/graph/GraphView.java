package graph;

import data.DataSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Представление (View) - это интерфейс, который отображает данные (Модель)
 * и отправляет пользовательские команды (или события) Контроллеру,
 * который их выполняет, действовуя над этими данными.
 */

public class GraphView extends JPanel {
    private static final Log log = LogFactory.getLog(GraphView.class);

    private int xIndent;
    private int yIndent;
    private Color bgColor = Color.BLACK;
    private Color previewBgColor = Color.BLACK;

    private java.util.List<GraphPanel> graphPanelList = new ArrayList<GraphPanel>();
    private java.util.List<GraphPanel> previewPanelList = new ArrayList<GraphPanel>();

    private JPanel mainPanel = new JPanel();
    private JPanel graphsMainPanel = new JPanel();
    private JPanel previewsMainPanel = new JPanel();
    private JPanel graphsPaintingPanel = new JPanel();
    private JPanel previewsPaintingPanel = new JPanel();
    private TimePanel graphTimePanel = new TimePanel();
    private TimePanel previewTimePanel = new TimePanel();
    private JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);

    private ViewEventHandler viewEventHandler;

    public GraphView(ViewEventHandler viewEventHandler) {
        this.viewEventHandler = viewEventHandler;
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
        graphsPaintingPanel.setBackground(bgColor);
        previewsPaintingPanel.setBackground(previewBgColor);

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
                setDrawingAreaWidth(getWidth() - xIndent);
            }
        });

        scrollBar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                moveScroll(e.getValue());
            }
        });
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
        if (graphTimePanel != null) {
            graphTimePanel.repaint();
        }
        if (previewTimePanel != null) {
            previewTimePanel.repaint();
        }
    }

    public void setScrollData(int scrollMaximum, int scrollExtent, int scrollValue) {
        BoundedRangeModel scrollModel = scrollBar.getModel();
        if(scrollModel.getExtent() != scrollExtent) {
            scrollModel.setExtent(scrollExtent);
        }
        if(scrollModel.getMaximum() != scrollMaximum) {
            scrollModel.setMaximum(scrollMaximum);
        }
        if(scrollModel.getValue() != scrollValue) {
            scrollModel.setValue(scrollValue);
        }
    }

    public void setXIndent(int xIndent) {
        this.xIndent = xIndent;
    }

    public void setYIndent(int yIndent) {
        this.yIndent = yIndent;
    }

    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
    }

    public void setPreviewBgColor(Color previewBgColor) {
        this.previewBgColor = previewBgColor;
    }

    public void setGraphStartIndex(int graphStartIndex) {
        for (GraphPanel graphPanel : graphPanelList) {
            graphPanel.setStartIndex(graphStartIndex);
        }
        graphTimePanel.setStartIndex(graphStartIndex);
    }

    public void setPreviewStartIndex(int previewStartIndex) {
        for (GraphPanel previewPanel : previewPanelList) {
            previewPanel.setStartIndex(previewStartIndex);
        }
        previewTimePanel.setStartIndex(previewStartIndex);
    }

    public void setSlotPosition(int slotPosition) {
        for (GraphPanel previewPanel : previewPanelList) {
            previewPanel.setSlotPosition(slotPosition);
        }
    }

    public void setSlotWidth(int slotWidth) {
        for (GraphPanel previewPanel : previewPanelList) {
            previewPanel.setSlotWidth(slotWidth);
        }
    }

    public void setStartTime(long startTime) {
        graphTimePanel.setStartTime(startTime);
        previewTimePanel.setStartTime(startTime);
    }

    public void setGraphTimeFrequency(double graphTimeFrequency) {
        graphTimePanel.setFrequency(graphTimeFrequency);
    }

    public void setPreviewTimeFrequency(double previewTimeFrequency) {
        previewTimePanel.setFrequency(previewTimeFrequency);
    }

    public void addGraphPanel(int weight, boolean isXCentered) {
        GraphPanel panel = new GraphPanel(weight, isXCentered);
        panel.setIndentX(xIndent);
        panel.setIndentY(yIndent);
        panel.setBackground(bgColor);
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
        GraphPanel panel = new GraphPanel(weight, isXCentered);
        panel.setIndentX(xIndent);
        panel.setIndentY(yIndent);
        panel.setBackground(previewBgColor);
        panel.addSlotListener(viewEventHandler);
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

    public void setPanelGraphs(java.util.List<DataSet> graphList, int panelNumber) {
        if(panelNumber < graphPanelList.size()) {
            graphPanelList.get(panelNumber).setGraphs(graphList);
        }
    }

    public void setPanelPreviews(java.util.List<DataSet> previewList, int panelNumber) {
        if(panelNumber < previewPanelList.size()) {
            previewPanelList.get(panelNumber).setGraphs(previewList);
        }
    }

    public int getNumberOfGraphPanels() {
        return graphPanelList.size();
    }

    public int getNumberOfPreviewPanels() {
        return previewPanelList.size();
    }

    private void moveSlotForward() {
        viewEventHandler.moveSlotForward();
    }
    private void moveSlotBackward() {
        viewEventHandler.moveSlotBackward();
    }
    private void setDrawingAreaWidth(int drawingAreaWidth) {
        viewEventHandler.setDrawingAreaWidth(drawingAreaWidth);
        setPanelsSizes();
    }

    private void moveScroll(int scrollPosition) {
        viewEventHandler.moveScroll(scrollPosition);
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

    private int getTimePanelHeight(Font font) {
        FontMetrics fm = getFontMetrics(font);
        return fm.getHeight() + 4;
    }

    private void addGraphTimePanel() {
        graphTimePanel.setPreferredSize(new Dimension(getWidth(), getTimePanelHeight(graphTimePanel.getFont())));
        graphTimePanel.setIndentX(xIndent);
        graphTimePanel.setBackground(bgColor);
        graphsMainPanel.add(graphTimePanel, BorderLayout.NORTH);
    }

    private void addPreviewTimePanel() {
        previewTimePanel.setPreferredSize(new Dimension(getWidth(), getTimePanelHeight(previewTimePanel.getFont())));
        previewTimePanel.setIndentX(xIndent);
        previewTimePanel.setBackground(previewBgColor);
        previewsMainPanel.add(previewTimePanel, BorderLayout.SOUTH);
    }

}
