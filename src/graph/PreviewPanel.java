package graph;

import data.DataSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: GENA
 * Date: 10.05.14
 * Time: 21:32
 * To change this template use File | Settings | File Templates.
 */
class PreviewPanel extends GraphPanel {
    private static final Log log = LogFactory.getLog(PreviewPanel.class);
    protected Color SLOT_COLOR = Color.MAGENTA;
    private List<SlotListener> slotListeners = new ArrayList<SlotListener>();

    PreviewPanel(int weight, boolean isXCentered, int panelNumber, GraphsData graphsData) {
        super(weight, isXCentered, panelNumber, graphsData);
        //MouseListener to move Slot
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int slotPosition = e.getX() - GraphsData.X_INDENT;
                notifySlotListeners(slotPosition);
            }
        });
    }

    public void addSlotListener(SlotListener slotListener) {
        slotListeners.add(slotListener);
    }
    private void notifySlotListeners(int newSlotPosition) {
        for (SlotListener listener: slotListeners) {
            listener.moveSlot(newSlotPosition);
        }
    }

    @Override
    protected List<? extends DataSet> getGraphs() {
        return graphsData.getPreviewList(panelNumber);
    }

    @Override
    protected int getStartIndex() {
        return graphsData.getScrollPosition();
    }

    @Override
    protected double getTimeFrequency() {
        return graphsData.getTimeFrequency() / graphsData.getCompression();
    }

    private void paintSlot(Graphics g) {
        int slotWidth = graphsData.getSlotWidth();
        int slotPosition = graphsData.getSlotPosition();
        if(slotWidth > 0) {
            g.setColor(SLOT_COLOR);
            g.drawRect(slotPosition, 0, slotWidth, getMaxY());
            g.drawLine(slotPosition-1, 0, slotPosition-1, getMaxY());
            g.drawLine(slotPosition-2, 0, slotPosition-2, getMaxY());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        transformCoordinate(g);
        paintSlot(g);
        restoreCoordinate(g);
    }
}
