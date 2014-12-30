package graph;

import javax.swing.*;
import javax.swing.event.ChangeListener;

public class ScrollModel implements BoundedRangeModel{
    protected GraphsData graphsData;

    public ScrollModel(GraphsData graphsData) {
        this.graphsData = graphsData;
    }

    @Override
    public int getMinimum() {
        return 0;
    }

    @Override
    public void setMinimum(int newMinimum) {

    }

    @Override
    public int getMaximum() {
        return graphsData.getPreviewFullSize();
    }

    @Override
    public void setMaximum(int newMaximum) {

    }

    @Override
    public int getValue() {
        return graphsData.getScrollPosition();
    }

    @Override
    public void setValue(int newValue) {
          graphsData.setScrollPosition(newValue);
    }

    @Override
    public void setValueIsAdjusting(boolean b) {

    }

    @Override
    public boolean getValueIsAdjusting() {
        return false;
    }

    @Override
    public int getExtent() {
        return graphsData.getCanvasWidth();
    }

    @Override
    public void setExtent(int newExtent) {

    }

    @Override
    public void setRangeProperties(int value, int extent, int min, int max, boolean adjusting) {
        setValue(value);
    }

    public void addChangeListener(ChangeListener l) {
        graphsData.addChangeListener(l);
    }

    public void removeChangeListener(ChangeListener l) {
        graphsData.removeChangeListener(l);
    }
}
