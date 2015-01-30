package graph;

import data.DataSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

class GraphsData {
    private static final Log log = LogFactory.getLog(GraphsData.class);

    // bigger GAP - less precision need slot to start autoscroll
    private static final int AUTO_SCROLL_GAP = 2;

    static final int DEFAULT_COMPRESSION = 1;
    private  int compression = DEFAULT_COMPRESSION;
    private double timeFrequency;
    private int startIndex;
    private int scrollPosition;
    private int drawingAreaWidth;

    // graphs list for every graph panel
    private List<List<DataSet>> listOfGraphLists = new ArrayList<List<DataSet>>();
    // previews list for every preview panel
    private List<List<DataSet>> listOfPreviewLists = new ArrayList<List<DataSet>>();

    long getStartTime() {
        long startTime = 0;
        if(listOfGraphLists.size() > 0) {
            if(listOfGraphLists.get(0).size() > 0) {
                startTime = listOfGraphLists.get(0).get(0).getStartTime();
            }
        }
        else if(listOfPreviewLists.size() > 0) {
            if(listOfPreviewLists.get(0).size() > 0) {
                startTime = listOfPreviewLists.get(0).get(0).getStartTime();
            }
        }
        return startTime;
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
            setTimeFrequency(Math.max(timeFrequency, graph.getFrequency()));
        }
    }

    void addPreviewList() {
        listOfPreviewLists.add(new ArrayList<DataSet>());
    }

    double getPreviewFrequency() {
        return timeFrequency / compression;
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

    public void setPreviewTimeFrequency (double previewTimeFrequency) {
        if(getTimeFrequency() == 0) {
            setTimeFrequency(previewTimeFrequency);
        }
        int compression = (int)(getTimeFrequency() / previewTimeFrequency);
        setCompression(compression);
    }

    public void setCompression(int compression) {
        if(compression <= 0) {
            compression = DEFAULT_COMPRESSION;
        }
        this.compression = compression;
    }

    public void setTimeFrequency(double timeFrequency) {
        double previewTimeFrequency = this.timeFrequency / compression;
        this.timeFrequency = timeFrequency;
        if(compression != 1 && previewTimeFrequency != 0) {
            int compressionNew = (int)(timeFrequency / previewTimeFrequency); // to save the same previewTimeFrequency
            setCompression(compressionNew);
        }
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
        return drawingAreaWidth;
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


    void moveSlot(int slotPosition) {
        if(slotPosition < 0) {
            slotPosition = 0;
        }
        if(slotPosition > getMaxSlotPosition()) {
            slotPosition = getMaxSlotPosition();
        }
        int newStartIndex = (slotPosition + scrollPosition) * compression;
        setStartIndex(newStartIndex);
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
        int maxScrollPosition = getPreviewsSize() - getDrawingAreaWidth();
        if(maxScrollPosition < 0) {
            maxScrollPosition = 0;
        }
        return maxScrollPosition;
    }

    void setScrollPosition(int scrollPosition) {
        if(scrollPosition > getMaxScrollPosition()) {
            scrollPosition = getMaxScrollPosition();
        }
        if(scrollPosition < 0) {
            scrollPosition = 0;
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
    }


    void setDrawingAreaWidth(int drawingAreaWidth) {
        this.drawingAreaWidth = drawingAreaWidth;
        if(startIndex > getMaxStartIndex()) {
            startIndex = getMaxStartIndex();
        }
        if(scrollPosition > getMaxScrollPosition()) {
            scrollPosition = getMaxScrollPosition();
        }
        if(getSlotPosition() > getMaxSlotPosition()){
            //adjust slotPosition to slotMaxPosition
            startIndex = (scrollPosition + getMaxSlotPosition())* compression;
        }
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
        int shift = (int)(getDrawingAreaWidth() * 0.25);  //прокрутка
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
            int shift = (int)(getDrawingAreaWidth() * 0.25);
            int newStartIndex = getStartIndex() - shift;
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

    public double getTimeFrequency() {
        return timeFrequency;
    }
}
