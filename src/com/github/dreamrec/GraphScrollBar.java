package com.github.dreamrec;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class GraphScrollBar extends JScrollBar implements AdjustmentListener{

    private GraphScrollBarModel model;
    private boolean notifyListeners;
    private List<AdjustmentListener> listenerList = new ArrayList<AdjustmentListener>();

    public GraphScrollBar(GraphScrollBarModel model_) {
        super(JScrollBar.HORIZONTAL);
        model = model_;
        addAdjustmentListener(this);
    }

    public void synchronizeBoundedRangeModel() {
        if(model == null) return;
        BoundedRangeModel boundedRangeModel = getModel();
        notifyListeners = false;
        int value = model.graphSize() < model.screenSize() ? 0 : model.graphIndex();
        boundedRangeModel.setRangeProperties(value, model.screenSize(), 0, model.graphSize(), false);
    }

    /**
     * Add listeners that do not respond to synchronizeBoundedRangeModel() method invocation;
     */
    public void addScrollListener(AdjustmentListener adjustmentListener) {
        listenerList.add(adjustmentListener);
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {
        if (notifyListeners) {
            for (AdjustmentListener adjustmentListener : listenerList) {
                adjustmentListener.adjustmentValueChanged(e);
            }
        } else {
            notifyListeners = true;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        synchronizeBoundedRangeModel();
        super.paintComponent(g);
    }
}
