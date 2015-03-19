package graph;

import graph.painters.XAxisPainter;
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


    private boolean isTimeAxis;

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
    private ScalePanel graphScalePanel;
    private ScalePanel previewScalePanel;
    private JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
    private boolean showScalesSeparate = true;

    private GraphEventHandler eventHandler;
    private FourierListener fourierHandler;

    public GraphView(GraphEventHandler eventHandler, boolean isTimeAxis, boolean showScalesSeparate) {
        this(eventHandler, null, isTimeAxis, showScalesSeparate);
    }

    public GraphView(GraphEventHandler eventHandler, FourierListener fourierHandler, boolean isTimeAxis, boolean showScalesSeparate) {
        this.eventHandler = eventHandler;
        this.fourierHandler = fourierHandler;
        this.isTimeAxis = isTimeAxis;
        this.showScalesSeparate = showScalesSeparate;
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

        createGraphScalePanel();
        createPreviewScalePanel();

        setFocusable(true); //only that way KeyListeners work
        requestFocusInWindow();

        // Key Listener to move Slot
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_RIGHT) {
                    GraphView.this.eventHandler.moveSlotForward();
                }

                if (key == KeyEvent.VK_LEFT) {
                    GraphView.this.eventHandler.moveSlotBackward();
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                setPanelsSizes();
                GraphView.this.eventHandler.setDrawingAreaWidth(getWidth() - xIndent);
            }
        });

        scrollBar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                GraphView.this.eventHandler.moveScroll(e.getValue());
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
        if (graphScalePanel != null) {
            graphScalePanel.repaint();
        }
        if (previewScalePanel != null) {
            previewScalePanel.repaint();
        }
    }

    public void setScrollData(int scrollMaximum, int scrollExtent, int scrollValue) {
        BoundedRangeModel scrollModel = scrollBar.getModel();
        if(scrollModel.getMaximum() != scrollMaximum || scrollModel.getExtent() != scrollExtent || scrollModel.getValue() != scrollValue) {
            scrollModel.setRangeProperties(scrollValue, scrollExtent, 0, scrollMaximum, true);
        }
    }

    public void setXIndent(int xIndent) {
        this.xIndent = xIndent;
        graphScalePanel.setIndentX(xIndent);
        previewScalePanel.setIndentX(xIndent);
        for(GraphPanel panel : graphPanelList) {
            panel.setIndentX(xIndent);
        }
        for(GraphPanel panel : previewPanelList) {
            panel.setIndentX(xIndent);
        }
    }

    public void setYIndent(int yIndent) {
        this.yIndent = yIndent;
        for(GraphPanel panel : graphPanelList) {
            panel.setIndentY(yIndent);
        }
        for(GraphPanel panel : previewPanelList) {
            panel.setIndentY(yIndent);
        }
    }

    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
        graphScalePanel.setBackground(bgColor);
        for(GraphPanel panel : graphPanelList) {
            panel.setBackground(bgColor);
        }
    }

    public void setPreviewBgColor(Color previewBgColor) {
        this.previewBgColor = previewBgColor;
        previewScalePanel.setBackground(previewBgColor);
        for(GraphPanel panel : previewPanelList) {
            panel.setBackground(previewBgColor);
        }
    }

    public void setGraphStartIndex(int graphStartIndex) {
        for (GraphPanel graphPanel : graphPanelList) {
            graphPanel.setStartIndex(graphStartIndex);
        }
        graphScalePanel.setStartIndex(graphStartIndex);
    }

    public void setPreviewStartIndex(int previewStartIndex) {
        for (GraphPanel previewPanel : previewPanelList) {
            previewPanel.setStartIndex(previewStartIndex);
        }
        previewScalePanel.setStartIndex(previewStartIndex);
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
        graphScalePanel.setStartTime(startTime);
        previewScalePanel.setStartTime(startTime);
    }

    public void setGraphTimeFrequency(double graphTimeFrequency) {
        graphScalePanel.setFrequency(graphTimeFrequency);
    }

    public void setPreviewTimeFrequency(double previewTimeFrequency) {
        previewScalePanel.setFrequency(previewTimeFrequency);
    }

    public void addGraphPanel(int weight, boolean isXCentered) {
        GraphPanel panel = new GraphPanel(weight, isXCentered);
        panel.setIndentX(xIndent);
        panel.setIndentY(yIndent);
        panel.setBackground(bgColor);
        if(fourierHandler != null) {
            panel.addFourierListener(fourierHandler);
        }
        XAxisPainter xAxisPainter = new XAxisPainter(isTimeAxis);
        xAxisPainter.isValuesPaint(!showScalesSeparate);
        panel.setxAxisPainter(xAxisPainter);
        if (graphPanelList.size() == 0) {
            graphsMainPanel.add(graphScalePanel, BorderLayout.NORTH);
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
        panel.addSlotListener(eventHandler);
        XAxisPainter xAxisPainter = new XAxisPainter(isTimeAxis);
        xAxisPainter.isValuesPaint(!showScalesSeparate);
        panel.setxAxisPainter(xAxisPainter);
        if (previewPanelList.size() == 0) {
            previewsMainPanel.add(previewScalePanel, BorderLayout.SOUTH);
        }
        previewPanelList.add(panel);
        previewsPaintingPanel.add(panel);
        setPanelsSizes();
    }

    public void setPanelGraphs(java.util.List<Graph> graphList, int panelNumber) {
        if(panelNumber < graphPanelList.size()) {
            graphPanelList.get(panelNumber).setGraphs(graphList);
        }
    }

    public void setPanelPreviews(java.util.List<Graph> previewList, int panelNumber) {
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

    private void createGraphScalePanel() {
        XAxisPainter scalePainter= new XAxisPainter(isTimeAxis);
        scalePainter.isAxisPaint(false);
        scalePainter.isGridPaint(false);
        graphScalePanel = new ScalePanel(scalePainter);
        graphScalePanel.setIndentX(xIndent);
        graphScalePanel.setBackground(bgColor);
        graphScalePanel.addMinusButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                double frequencyNew = graphScalePanel.getFrequency() / 2;
                eventHandler.setGraphFrequency(frequencyNew);
            }
        });
        graphScalePanel.addPlusButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                double frequencyNew = graphScalePanel.getFrequency() * 2;
                eventHandler.setGraphFrequency(frequencyNew);
            }
        });

        if( ! isTimeAxis) {
            graphScalePanel.setButtonsVisible(false);
        }
        graphScalePanel.setVisible(showScalesSeparate);
    }

    private void createPreviewScalePanel() {
        XAxisPainter scalePainter= new XAxisPainter(isTimeAxis);
        scalePainter.isAxisPaint(false);
        scalePainter.isGridPaint(false);
        previewScalePanel = new ScalePanel(scalePainter);
        previewScalePanel.setIndentX(xIndent);
        previewScalePanel.setBackground(previewBgColor);
        previewScalePanel.addMinusButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                double frequencyNew = previewScalePanel.getFrequency() / 2;
                eventHandler.setPreviewFrequency(frequencyNew);
            }
        });
        previewScalePanel.addPlusButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                double frequencyNew = previewScalePanel.getFrequency() * 2;
                eventHandler.setPreviewFrequency(frequencyNew);
            }
        });

        if( ! isTimeAxis) {
            previewScalePanel.setButtonsVisible(false);
        }
        previewScalePanel.setVisible(showScalesSeparate);
    }
}
