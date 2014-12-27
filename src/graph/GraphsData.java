package graph;

import data.DataSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

class GraphsData {
    private static final Log log = LogFactory.getLog(GraphsData.class);

    // bigger GAP - less precision need slot to start autoscroll
    private static final int AUTO_SCROLL_GAP = 10;

    static final int X_INDENT = 50;
    static final int Y_INDENT = 20;
    private  int compression = 750;
    private double timeFrequency;
    private long startTime;
    private int startIndex;
    private int scrollPosition;
    private int canvasWidth;
    // graphs list for every panel
    private List<List<DataSet>> listOfGraphLists = new ArrayList<List<DataSet>>();
    // preview list for every preview panel
    private List<List<DataSet>> listOfPreviewLists = new ArrayList<List<DataSet>>();

    void addGraphList() {
        listOfGraphLists.add(new ArrayList<DataSet>());
    }

    void addGraphs(DataSet... graphs) {
        List<DataSet> lastGraphList = listOfGraphLists.get(listOfGraphLists.size() - 1);
        for(DataSet graph : graphs) {
            lastGraphList.add(graph);
        }
    }

    void addPreviewList() {
        listOfPreviewLists.add(new ArrayList<DataSet>());
    }

    void addPreviews(DataSet... previews) {
        List<DataSet> lastPreviewList = listOfPreviewLists.get(listOfPreviewLists.size() - 1);
        for(DataSet preview : previews) {
            lastPreviewList.add(preview);
        }
    }

    int getSlotPosition() {
        int slotIndex = startIndex / compression;
        int slotPosition = slotIndex - scrollPosition;
        return slotPosition;
    }

    int getDrawingAreaWidth() {
        return (canvasWidth - X_INDENT);
    }

    int getGraphsSize() {
        int graphsSize = 0;
        for (List<DataSet> graphsList : listOfGraphLists) {
            for (DataSet graph : graphsList) {
                int graphSize = graph.size();
                if(timeFrequency > 0 && graph.getFrequency() > 0) {
                     graphSize *= (int)(timeFrequency / graph.getFrequency());
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
                    previewSize *= (int)(frequency / preview.getFrequency());
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
        if(slotPosition > getSlotMaxPosition()) {
            slotPosition = getSlotMaxPosition();
        }
        startIndex = (slotPosition + scrollPosition) * compression;
    }


    int getSlotWidth() {
        if(compression <= 1) {
            return 0;
        }
        return  Math.max(1, (getDrawingAreaWidth() / compression));
    }

    int getSlotMaxPosition() {
        int maxPosition = getPreviewsSize() - scrollPosition - getSlotWidth();
        if(maxPosition < 0) {
            maxPosition = 0;
        }
        maxPosition = Math.min(maxPosition, getDrawingAreaWidth() - getSlotWidth());
        return maxPosition;
    }

    void setScrollPosition(int scrollPosition) {
        this.scrollPosition = scrollPosition;
        if(getSlotPosition() < 0){
            //adjust slotPosition to 0
            startIndex = scrollPosition * compression;
        }
        if(getSlotPosition() > getSlotMaxPosition()){
            //adjust slotPosition to slotMaxPosition
            startIndex = (scrollPosition + getSlotMaxPosition())* compression;
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
            scrollPosition = startIndex / compression;
        }
        if(getSlotPosition() > getSlotMaxPosition()){
            //adjust slotPosition to slotMaxPosition
            scrollPosition = startIndex / compression - getSlotMaxPosition();
        }
    }

    void setCanvasWidth(int canvasWidth) {
        this.canvasWidth = canvasWidth;
        if(getSlotPosition() > getSlotMaxPosition()){
            //adjust slotPosition to slotMaxPosition
            startIndex = (scrollPosition + getSlotMaxPosition())* compression;
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
        if(newStartIndex > getMaxStartIndex()) {
            newStartIndex = getMaxStartIndex();
        }
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
            if(newStartIndex < 0) {
                newStartIndex = 0;
            }
            setStartIndex(newStartIndex);
        }
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
