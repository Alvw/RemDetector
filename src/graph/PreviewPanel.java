package graph;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    PreviewPanel(int weight, boolean isXCentered, GraphsData graphsData) {
        super(weight, isXCentered, graphsData);
        graphs = graphsData.getLastPreviewList();
        //MouseListener to move Slot
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int slotPosition = e.getX() - GraphsDataOld.X_INDENT;
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
    protected void paintAxisX(Graphics g) {
        int MINUTE = 60 * 1000;//milliseconds
        int MINUTES_2 = 2 * MINUTE;
        int MINUTES_10 = 10 * MINUTE;
        int MINUTES_30 = 30 * MINUTE;//milliseconds
        long startTime = graphsData.getStartTime();
        double timeFrequency = getTimeFrequency();
        int point_distance_msec = (int) (1000/timeFrequency);
        g.setColor(axisColor);
        Graphics2D g2d = (Graphics2D) g;
        g2d.transform(AffineTransform.getScaleInstance(1.0, -1.0)); // flip transformation

        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        if(startTime == 0 ) {
            startTime = System.currentTimeMillis();
        }

        long newStartTime = (startTime/ point_distance_msec)* point_distance_msec + point_distance_msec;
        for (int i = 0; i  < getWorkspaceWidth(); i++) {

            long iTime = newStartTime + (long)((getStartIndex() + i) * point_distance_msec);
            if((iTime % MINUTES_10) == 0){
                // Paint Triangle
                g.fillPolygon(new int[]{i - 3, i + 3, i}, new int[]{0, 0, 6}, 3);
            }
            else if((iTime % MINUTES_2) == 0){
                //paint T
                g.drawLine(i - 1, 0, i + 1, 0);
                g.drawLine(i, 0, i, 5);
            }
            else if((iTime % MINUTE) == 0){
                // Paint Point
                g.drawLine(i, 0, i, 0);
            }

            if(((iTime % MINUTES_30) == 0)){
                String timeStamp = dateFormat.format(new Date(iTime)) ;
                // Paint Time Stamp
                g.drawString(timeStamp, i - 15, +18);
            }
        }
        g2d.transform(AffineTransform.getScaleInstance(1.0, -1.0)); // flip transformation
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);    //To change body of overridden methods use File | Settings | File Templates.
        transformCoordinate(g);
        paintSlot(g);
        restoreCoordinate(g);
    }
}
