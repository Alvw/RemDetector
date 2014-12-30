package graph;

import data.DataSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;

class GraphsData  {
    private static final Log log = LogFactory.getLog(GraphsDataOld.class);

    // bigger GAP - less precision need slot to start autoscroll
    private static final int AUTO_SCROLL_GAP = 10;

    static final int X_INDENT = 50;
    static final int Y_INDENT = 20;
    private  int compression = 750;
    private double timeFrequency;
    private long startTime;
    private int startIndex;
    //private int scrollPosition;
    private int canvasWidth;
    private BoundedRangeModel scrollModel;
    // graphs list for every panel
    private List<List<DataSet>> listOfGraphLists = new ArrayList<List<DataSet>>();
    // preview list for every preview panel
    private List<List<DataSet>> listOfPreviewLists = new ArrayList<List<DataSet>>();

    GraphsData(BoundedRangeModel scrollModel) {
        this.scrollModel = scrollModel;
        scrollModel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                 adjustStartIndex();
            }
        });
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
            lastGraphList.add(graph);
        }
    }

    void addPreviewList() {
        listOfPreviewLists.add(new ArrayList<DataSet>());
    }

    void addPreviews(DataSet... previews) {
        if(listOfPreviewLists.size() == 0) {
            addPreviewList();
        }
        List<DataSet> lastPreviewList = listOfPreviewLists.get(listOfPreviewLists.size() - 1);
        for(DataSet preview : previews) {
            lastPreviewList.add(preview);
        }
    }
    List<DataSet> getLastGraphList() {
        return listOfGraphLists.get(listOfGraphLists.size() - 1);
    }

    List<DataSet> getLastPreviewList() {
        return listOfPreviewLists.get(listOfPreviewLists.size() - 1);
    }

    int getSlotPosition() {
        int slotIndex = startIndex / compression;
        int slotPosition = slotIndex - getScrollPosition();
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
        int newStartIndex = (slotPosition + getScrollPosition()) * compression;
        setStartIndex(newStartIndex);
    }


    int getSlotWidth() {
        if(compression > 1 && getDrawingAreaWidth() > 0) {
            return  Math.max(1, (getDrawingAreaWidth() / compression));
        }
        return 0;
    }

    int getSlotMaxPosition() {
        int maxPosition = getPreviewsSize() - getScrollPosition() - getSlotWidth();
        if(maxPosition < 0) {
            maxPosition = 0;
        }
        maxPosition = Math.min(maxPosition, getDrawingAreaWidth() - getSlotWidth());
        return maxPosition;
    }

    void setScrollPosition(int scrollPosition) {
        scrollModel.setValue(scrollPosition);
   /*     if(getSlotPosition() < 0){
            //adjust slotPosition to 0
            startIndex = scrollPosition * compression;
        }
        if(getSlotPosition() > getSlotMaxPosition()){
            //adjust slotPosition to slotMaxPosition
            startIndex = (scrollPosition + getSlotMaxPosition())* compression;
        }   */
    }

    void adjustStartIndex() {
        if(getSlotPosition() < 0){
            //adjust slotPosition to 0
            startIndex = getScrollPosition() * compression;
        }
        if(getSlotPosition() > getSlotMaxPosition()){
            //adjust slotPosition to slotMaxPosition
            startIndex = (getScrollPosition() + getSlotMaxPosition())* compression;
        }
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
        if(getSlotPosition() > getSlotMaxPosition()){
            //adjust slotPosition to slotMaxPosition
            setScrollPosition(startIndex / compression - getSlotMaxPosition());
        }
    }

    void setCanvasWidth(int canvasWidth) {
        this.canvasWidth = canvasWidth;
        if(getSlotPosition() > getSlotMaxPosition()){
            //adjust slotPosition to slotMaxPosition
            startIndex = (getScrollPosition() + getSlotMaxPosition())* compression;
        }
    }

    boolean isAutoScroll() {
        return (getSlotMaxPosition() <= (getSlotPosition() + AUTO_SCROLL_GAP));
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
    }


    public int getScrollPosition() {
        return scrollModel.getValue();
    }

    public int getStartIndex() {
        return startIndex;
    }


    public int getCompression() {
        return compression;
    }

    public void setCompression(int compression) {
        this.compression = compression;
    }

    public double getTimeFrequency() {
        return timeFrequency;
    }

    public void setTimeFrequency(double timeFrequency) {
        this.timeFrequency = timeFrequency;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
