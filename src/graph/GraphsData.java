package graph;

import data.DataSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.util.ArrayList;
import java.util.List;

class GraphsData {
    private static final Log log = LogFactory.getLog(GraphsData.class);
    protected ChangeEvent changeEvent = null;
    protected EventListenerList listenerList = new EventListenerList();


    // bigger GAP - less precision need slot to start autoscroll
    private static final int AUTO_SCROLL_GAP = 2;

    static final int X_INDENT = 50;
    static final int Y_INDENT = 20;
    private  int compression = 1;
    private double timeFrequency;
    private long startTime;
    private int startIndex;
    private int scrollPosition;
    private int canvasWidth;

    // graphs list for every graph panel
    private List<List<DataSet>> listOfGraphLists = new ArrayList<List<DataSet>>();
    // previews list for every preview panel
    private List<List<DataSet>> listOfPreviewLists = new ArrayList<List<DataSet>>();

    BoundedRangeModel getScrollBoundedRangeModel() {
        return new ScrollModel(this);
    }

    void addGraphList() {
        listOfGraphLists.add(new ArrayList<DataSet>());
    }

    void addGraphs(DataSet... graphs) {
        if(listOfGraphLists.size() == 0) {
            addGraphList();
        }
        List<DataSet> lastGraphList = listOfGraphLists.get(listOfGraphLists.size() - 1);
        for(DataSet graph : graphs) {
            lastGraphList.add(new FrequencyConverterAvg(graph, timeFrequency));
            setTimeFrequency(Math.max(timeFrequency, graph.getFrequency()));
        }
    }

    void addPreviewList() {
        listOfPreviewLists.add(new ArrayList<DataSet>());
    }

    void addPreviewsAvg(DataSet... previews) {
        if(listOfPreviewLists.size() == 0) {
            addPreviewList();
        }
        List<DataSet> lastPreviewList = listOfPreviewLists.get(listOfPreviewLists.size() - 1);
        for(DataSet preview : previews) {
            lastPreviewList.add(new BufferedFrequencyConverter(new FrequencyConverterAvg(preview, timeFrequency/compression)));
        }
    }

    void addPreviewsMax(DataSet... previews) {
        if(listOfPreviewLists.size() == 0) {
            addPreviewList();
        }
        List<DataSet> lastPreviewList = listOfPreviewLists.get(listOfPreviewLists.size() - 1);
        for(DataSet preview : previews) {
            lastPreviewList.add(new BufferedFrequencyConverter(new FrequencyConverterMax(preview, timeFrequency/compression)));
        }
    }

    public void setCompression(int compression) {
        this.compression = compression;
        for(List<DataSet> listOfPreviews : listOfPreviewLists) {
            for(DataSet preview : listOfPreviews) {
                FrequencyConverter previewCasted = (FrequencyConverter) preview;
                previewCasted.setFrequency(timeFrequency/compression);
            }
        }
        fireStateChanged();
    }

    public void setTimeFrequency(double timeFrequency) {
        this.timeFrequency = timeFrequency;
        for(List<DataSet> listOfGraphs : listOfGraphLists) {
            for(DataSet graph : listOfGraphs) {
                FrequencyConverter graphCasted = (FrequencyConverter) graph;
                graphCasted.setFrequency(timeFrequency);
            }
        }
        for(List<DataSet> listOfPreviews : listOfPreviewLists) {
            for(DataSet preview : listOfPreviews) {
                FrequencyConverter previewCasted = (FrequencyConverter) preview;
                previewCasted.setFrequency(timeFrequency/compression);
            }
        }

        fireStateChanged();
    }

    List<DataSet> getGraphList(int listNumber) {
        return listOfGraphLists.get(listNumber);
    }

    List<DataSet> getPreviewList(int listNumber) {
        return listOfPreviewLists.get(listNumber);
    }

    int getSlotPosition() {
        int slotIndex = startIndex / compression;
        int slotPosition = slotIndex - scrollPosition;
        return slotPosition;
    }

    int getDrawingAreaWidth() {
        int width = canvasWidth - X_INDENT;
        if(width < 0) {
            width = 0;
        }
        return width;
    }

    int getGraphsSize() {
        int graphsSize = 0;
        for (List<DataSet> graphsList : listOfGraphLists) {
            for (DataSet graph : graphsList) {
                int graphSize = graph.size();
                if(timeFrequency > 0 && graph.getFrequency() > 0) {
                    graphSize = (int)(graphSize * timeFrequency / graph.getFrequency());
                }
                graphsSize = Math.max(graphsSize, graphSize);
            }
        }

        int previewsSize = 0;
        double frequency = timeFrequency/compression;
        for (List<DataSet> previewList : listOfPreviewLists) {
            for (DataSet preview : previewList) {
                int previewSize = preview.size();
                if(frequency > 0 && preview.getFrequency() > 0) {
                    previewSize = (int)(previewSize * frequency / preview.getFrequency());
                }
                previewsSize = Math.max(previewsSize, previewSize);
            }
        }
        graphsSize = Math.max(graphsSize, previewsSize * compression);
        return graphsSize;
    }

    int getPreviewsSize() {
        return getGraphsSize() / compression;
    }

    int getMaxStartIndex () {
        int maxStartIndex = getGraphsSize() - 1 - getDrawingAreaWidth();
        if (maxStartIndex < 0) {
            maxStartIndex = 0;
        }
        return maxStartIndex;
    }

    int getPreviewFullSize() {
        return X_INDENT + getPreviewsSize();
    }

    void moveSlot(int slotPosition) {
        if(slotPosition < 0) {
            slotPosition = 0;
        }
        if(slotPosition > getMaxSlotPosition()) {
            slotPosition = getMaxSlotPosition();
        }
        int newStartIndex = (slotPosition + scrollPosition) * compression;
        setStartIndex(newStartIndex);
        fireStateChanged();
    }


    int getSlotWidth() {
        if(compression > 1 && getDrawingAreaWidth() > 0) {
            return  Math.max(1, (getDrawingAreaWidth() / compression));
        }
        return 0;
    }

    int getMaxSlotPosition() {
        int maxPosition = getPreviewsSize() - scrollPosition - getSlotWidth();
        if(maxPosition < 0) {
            maxPosition = 0;
        }
        maxPosition = Math.min(maxPosition, getDrawingAreaWidth() - getSlotWidth());
        return maxPosition;
    }


    int getMaxScrollPosition() {
        return getPreviewFullSize() - canvasWidth;
    }

    void setScrollPosition(int scrollPosition) {
        if(scrollPosition < 0) {
            scrollPosition = 0;
        }
        if(scrollPosition > getMaxScrollPosition()) {
            scrollPosition = getMaxScrollPosition();
        }
        this.scrollPosition = scrollPosition;
        if(getSlotPosition() < 0){
            //adjust slotPosition to 0
            startIndex = scrollPosition * compression;
        }
        if(getSlotPosition() > getMaxSlotPosition()){
            //adjust slotPosition to slotMaxPosition
            startIndex = (scrollPosition + getMaxSlotPosition())* compression;
        }
        fireStateChanged();
    }

    void setStartIndex(int startIndex) {
        if(startIndex < 0) {
            startIndex = 0;
        }
        if(startIndex > getMaxStartIndex()) {
            startIndex = getMaxStartIndex();
        }
        this.startIndex = startIndex;
        if(getSlotPosition() < 0){
            //adjust slotPosition to 0
            setScrollPosition(startIndex / compression);
        }
        if(getSlotPosition() > getMaxSlotPosition()){
            //adjust slotPosition to slotMaxPosition
            setScrollPosition(startIndex / compression - getMaxSlotPosition());
        }
        fireStateChanged();
    }


    void setCanvasWidth(int canvasWidth) {
        this.canvasWidth = canvasWidth;
        if(getSlotPosition() > getMaxSlotPosition()){
            //adjust slotPosition to slotMaxPosition
            startIndex = (scrollPosition + getMaxSlotPosition())* compression;
        }
        fireStateChanged();
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    boolean isAutoScroll() {
        return (getMaxSlotPosition() <= (getSlotPosition() + AUTO_SCROLL_GAP));
    }

    void autoScroll() {
        if (isAutoScroll()) {
            setStartIndex(getMaxStartIndex());
        }
    }

    void moveForward() {
        int shift = (int)(getDrawingAreaWidth() * 0.8);
        int newStartIndex = getStartIndex() + shift;
        setStartIndex(newStartIndex);
        fireStateChanged();
    }

    void moveBackward() {
        if (isAutoScroll()) {
            int newSlotPosition = getSlotPosition() - AUTO_SCROLL_GAP - 1; //to stop autoScroll
            if(newSlotPosition < 0) {
                newSlotPosition = 0;
            }
            moveSlot(newSlotPosition);
        }
        else {
            int shift = (int)(getDrawingAreaWidth() * 0.8);
            int newStartIndex = getStartIndex() - shift;
            setStartIndex(newStartIndex);
        }
        fireStateChanged();
    }


    public int getScrollPosition() {
        return scrollPosition;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getCompression() {
        return compression;
    }

    public double getTimeFrequency() {
        return timeFrequency;
    }


    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
        fireStateChanged();
    }
    /*
     * The rest of this is event handling code copied from
     * DefaultBoundedRangeModel.
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }

    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }

    protected void fireStateChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -=2 ) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }
        }
    }
}
