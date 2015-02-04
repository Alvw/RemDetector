package graph;

import data.DataSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

class GraphsModel {
    private static final Log log = LogFactory.getLog(GraphsModel.class);

    // bigger GAP - less precision need slot to start autoscroll
    private static final int AUTO_SCROLL_GAP = 2;
    private static final int DEFAULT_COMPRESSION = 1;

    private  int compression = DEFAULT_COMPRESSION;
    private double timeFrequency;
    private int startIndex;
    private int scrollPosition;
    private int drawingAreaWidth;

    // graph clusters corresponds to graph panels
    private List<List<DataSet>> graphClusterList = new ArrayList<List<DataSet>>();
    // preview clusters correspond to preview panels
    private List<List<DataSet>> previewClusterList = new ArrayList<List<DataSet>>();


    public long getStartTime() {
        long startTime = 0;
        for(List<DataSet> graphList : graphClusterList) {
            for(DataSet graph : graphList) {
                startTime = graph.getStartTime();
            }
        }
        for(List<DataSet> previewList : previewClusterList) {
            for(DataSet preview : previewList) {
                startTime = preview.getStartTime();
            }
        }
        return startTime;
    }

    public void addGraphCluster() {
        graphClusterList.add(new ArrayList<DataSet>());
    }

    public void addPreviewCluster() {
        previewClusterList.add(new ArrayList<DataSet>());
    }

    public void addGraph(DataSet graph) {
        if(graphClusterList.size() == 0) {
            addGraphCluster();
        }
        graphClusterList.get(graphClusterList.size() - 1).add(graph);
        setTimeFrequency(Math.max(timeFrequency, graph.getFrequency()));
    }

    public void addPreview(DataSet preview) {
        if(previewClusterList.size() == 0) {
            addPreviewCluster();
        }
        previewClusterList.get(previewClusterList.size() - 1).add(preview);
    }

    public double getPreviewTimeFrequency() {
        return timeFrequency / getCompression();
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
        double previewTimeFrequency = this.timeFrequency / getCompression();
        this.timeFrequency = timeFrequency;
        if(getCompression() != 1 && previewTimeFrequency != 0) {
            int compressionNew = (int)(timeFrequency / previewTimeFrequency); // to save the same previewTimeFrequency
            setCompression(compressionNew);
        }
    }

    public List<DataSet> getGraphList(int listNumber) {
        return graphClusterList.get(listNumber);
    }

    public List<DataSet> getPreviewList(int listNumber) {
        return previewClusterList.get(listNumber);
    }

    public int getSlotPosition() {
        int slotIndex = startIndex / getCompression();
        int slotPosition = slotIndex - scrollPosition;
        return slotPosition;
    }

    public int getDrawingAreaWidth() {
        return drawingAreaWidth;
    }


    public int getGraphsSize() {
        int graphsSize = 0;
        for (List<DataSet> graphsList : graphClusterList) {
            for (DataSet graph : graphsList) {
                int size = graph.size();
                if(timeFrequency > 0 && graph.getFrequency() > 0) {
                    size = (int)(size * timeFrequency / graph.getFrequency());
                }
                graphsSize = Math.max(graphsSize, size);
            }
        }

        int previewsSize = 0;
        double frequency = timeFrequency/getCompression();
        for (List<DataSet> previewList : previewClusterList) {
            for (DataSet preview : previewList) {
                int size = preview.size();
                if(frequency > 0 && preview.getFrequency() > 0) {
                    size = (int)(size * frequency / preview.getFrequency());
                }
                previewsSize = Math.max(previewsSize, size);
            }
        }
        return  Math.max(graphsSize, previewsSize * getCompression());
    }

    public int getPreviewsSize() {
        return  getGraphsSize() / getCompression();
    }


    public void moveSlot(int slotPosition) {
        if(slotPosition < 0) {
            slotPosition = 0;
        }
        if(slotPosition > getMaxSlotPosition()) {
            slotPosition = getMaxSlotPosition();
        }
        int newStartIndex = (slotPosition + scrollPosition) * getCompression();
        setStartIndex(newStartIndex);
    }


    public int getSlotWidth() {
        if(getCompression() > 1 && getDrawingAreaWidth() > 0 && getGraphsSize() > 0) {
            return  Math.max(1, (getDrawingAreaWidth() / getGraphsSize()));
        }
        return 0;
    }

    private int getMaxStartIndex () {
        int maxStartIndex = getGraphsSize() - 1 - getDrawingAreaWidth();
        if (maxStartIndex < 0) {
            maxStartIndex = 0;
        }
        return maxStartIndex;
    }

    private int getMaxSlotPosition() {
        int maxPosition = getPreviewsSize() - scrollPosition - getSlotWidth();
        if(maxPosition < 0) {
            maxPosition = 0;
        }
        maxPosition = Math.min(maxPosition, getDrawingAreaWidth() - getSlotWidth());
        return maxPosition;
    }


    private int getMaxScrollPosition() {
        int maxScrollPosition = getPreviewsSize() - getDrawingAreaWidth();
        if(maxScrollPosition < 0) {
            maxScrollPosition = 0;
        }
        return maxScrollPosition;
    }

    public void setScrollPosition(int scrollPosition) {
        if(scrollPosition > getMaxScrollPosition()) {
            scrollPosition = getMaxScrollPosition();
        }
        if(scrollPosition < 0) {
            scrollPosition = 0;
        }
        this.scrollPosition = scrollPosition;
        if(getSlotPosition() < 0){
            //adjust slotPosition to 0
            startIndex = scrollPosition * getCompression();
        }
        if(getSlotPosition() > getMaxSlotPosition()){
            //adjust slotPosition to slotMaxPosition
            startIndex = (scrollPosition + getMaxSlotPosition())* getCompression();
        }
    }

    public void setStartIndex(int startIndex) {
        if(startIndex < 0) {
            startIndex = 0;
        }
        if(startIndex > getMaxStartIndex()) {
            startIndex = getMaxStartIndex();
        }
        this.startIndex = startIndex;
        if(getSlotPosition() < 0){
            //adjust slotPosition to 0
            setScrollPosition(startIndex / getCompression());
        }
        if(getSlotPosition() > getMaxSlotPosition()){
            //adjust slotPosition to slotMaxPosition
            setScrollPosition(startIndex / getCompression() - getMaxSlotPosition());
        }
    }


    public void setDrawingAreaWidth(int drawingAreaWidth) {
        this.drawingAreaWidth = drawingAreaWidth;
        if(startIndex > getMaxStartIndex()) {
            startIndex = getMaxStartIndex();
        }
        if(scrollPosition > getMaxScrollPosition()) {
            scrollPosition = getMaxScrollPosition();
        }
        if(getSlotPosition() > getMaxSlotPosition()){
            //adjust slotPosition to slotMaxPosition
            startIndex = (scrollPosition + getMaxSlotPosition())* getCompression();
        }
    }


    private boolean isAutoScroll() {
        return (getMaxSlotPosition() <= (getSlotPosition() + AUTO_SCROLL_GAP));
    }

    public void autoScroll() {
        if (isAutoScroll()) {
            setStartIndex(getMaxStartIndex());
        }
    }

    public void moveSlotForward() {
        int shift = (int)(getDrawingAreaWidth() * 0.25);  //прокрутка
        int newStartIndex = getStartIndex() + shift;
        setStartIndex(newStartIndex);
    }

   public  void moveSlotBackward() {
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
        int graphsNumber = 0;
        int previewsNumber = 0;
        for (List<DataSet> graphsList : graphClusterList) {
            for (DataSet graph : graphsList) {
                graphsNumber++;
            }
        }
        for (List<DataSet> previewList : previewClusterList) {
            for (DataSet preview : previewList) {
                previewsNumber++;
            }
        }

        if(graphsNumber == 0 || previewsNumber ==0 ) {
            return DEFAULT_COMPRESSION;
        }
        return compression;
    }

    public double getTimeFrequency() {
        return timeFrequency;
    }
}
