package graph;

import data.DataSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

class GraphModel {
    private static final Log log = LogFactory.getLog(GraphModel.class);

    // bigger GAP - less precision need slot to start autoScroll
    private static final int AUTO_SCROLL_GAP = 2;

    private double compression = 1;
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
        for (List<DataSet> graphList : graphClusterList) {
            for (DataSet graph : graphList) {
                startTime = graph.getStartTime();
            }
        }
        for (List<DataSet> previewList : previewClusterList) {
            for (DataSet preview : previewList) {
                startTime = preview.getStartTime();
            }
        }
        return startTime;
    }

    private void setGraphsFrequency(double frequency) {
        for (List<DataSet> graphsList : graphClusterList) {
            for (DataSet graph : graphsList) {
                FrequencyConverter frequencyConverter = (FrequencyConverter) graph;
                frequencyConverter.setFrequency(frequency);
            }
        }
    }

    private void setPreviewsFrequency(double frequency) {
        for (List<DataSet> previewList : previewClusterList) {
            for (DataSet preview : previewList) {
                FrequencyConverter frequencyConverter = (FrequencyConverter) preview;
                frequencyConverter.setFrequency(frequency);
            }
        }
    }

    private int getNumberOfGraphs() {
        int graphsNumber = 0;
        for (List<DataSet> graphsList : graphClusterList) {
            for (DataSet graph : graphsList) {
                graphsNumber++;
            }
        }
        return graphsNumber;
    }

    private int getNumberOfPreviews() {
        int previewsNumber = 0;
        for (List<DataSet> previewList : previewClusterList) {
            for (DataSet preview : previewList) {
                previewsNumber++;
            }
        }
        return previewsNumber;
    }

    private double getCompression() {
        if (getNumberOfGraphs() == 0 || getNumberOfPreviews() == 0) {
            return 1;
        }
        return compression;
    }

    public void addGraphCluster() {
        graphClusterList.add(new ArrayList<DataSet>());
    }

    public void addPreviewCluster() {
        previewClusterList.add(new ArrayList<DataSet>());
    }

    public void addGraph(DataSet graph, int graphClusterNumber) {
        if (graphClusterNumber < graphClusterList.size()) {
            FrequencyConverter resultingGraph = new FrequencyConverterRuntime(graph, CompressionType.AVERAGE);
            if(graph.getFrequency() != 0) {
                double previewFrequency = timeFrequency / compression;
                timeFrequency = Math.max(timeFrequency, graph.getFrequency());
                setGraphsFrequency(timeFrequency);
                resultingGraph.setFrequency(timeFrequency);
                if(previewFrequency != 0) {
                    compression = timeFrequency / previewFrequency;
                }
            }
            graphClusterList.get(graphClusterNumber).add(resultingGraph);
        }
    }


    public void addPreview(DataSet preview, int previewClusterNumber, CompressionType compressionType) {
        if (previewClusterNumber < previewClusterList.size()) {
            FrequencyConverter resultingPreview = new FrequencyConverterBuffered(new FrequencyConverterRuntime(preview, compressionType));
            if (preview.getFrequency() == 0) {
                resultingPreview.setCompression(compression);
            } else {
                if(timeFrequency == 0) {
                    timeFrequency = preview.getFrequency();
                }
                double previewFrequency = timeFrequency / compression;
                resultingPreview.setFrequency(previewFrequency);
                setPreviewsFrequency(previewFrequency);
            }
            previewClusterList.get(previewClusterNumber).add(resultingPreview);
        }
    }

    public void setCompression(int compression) {
        if (compression <= 0) {
            compression = 1;
        }
        this.compression = compression;
    }

    public void setGraphFrequency(double graphFrequency) {
        if(timeFrequency != 0) {
            compression = graphFrequency * compression / timeFrequency;
        }
        timeFrequency = graphFrequency;
        setGraphsFrequency(graphFrequency);
    }

    public void setPreviewFrequency(double previewFrequency) {
        if(timeFrequency != 0) {
            compression = timeFrequency / previewFrequency;
        }
        else {
            timeFrequency = previewFrequency * compression;
        }
        setPreviewsFrequency(previewFrequency);
    }

    public List<DataSet> getGraphCluster(int listNumber) {
        return graphClusterList.get(listNumber);
    }

    public List<DataSet> getPreviewCluster(int listNumber) {
        return previewClusterList.get(listNumber);
    }

    public int getSlotPosition() {
        int slotIndex = Math.round((float)(startIndex / getCompression()));
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
                graphsSize = Math.max(graphsSize, graph.size());
            }
        }

        int previewsSize = 0;
        for (List<DataSet> previewList : previewClusterList) {
            for (DataSet preview : previewList) {
                previewsSize = Math.max(previewsSize, preview.size());
            }
        }
        return (int)Math.max(graphsSize, previewsSize * getCompression());
    }

    public int getPreviewsSize() {
        return (int)(getGraphsSize() / getCompression());
    }


    public void moveSlot(int slotPosition) {
        if (slotPosition < 0) {
            slotPosition = 0;
        }
        if (slotPosition > getMaxSlotPosition()) {
            slotPosition = getMaxSlotPosition();
        }
        int newStartIndex = (int)((slotPosition + scrollPosition) * getCompression());
        setStartIndex(newStartIndex);
    }


    public int getSlotWidth() {
        if (getCompression() > 1 && getDrawingAreaWidth() > 0 && getGraphsSize() > 0) {
            return Math.max(1, (int)(getDrawingAreaWidth() / getCompression()));
        }
        return 0;
    }

    private int getMaxStartIndex() {
        int maxStartIndex = getGraphsSize() - 1 - getDrawingAreaWidth();
        if (maxStartIndex < 0) {
            maxStartIndex = 0;
        }
        return maxStartIndex;
    }

    private int getMaxSlotPosition() {
        int maxPosition = getPreviewsSize() - scrollPosition - getSlotWidth();
        if (maxPosition < 0) {
            maxPosition = 0;
        }
        maxPosition = Math.min(maxPosition, getDrawingAreaWidth() - getSlotWidth());
        return maxPosition;
    }


    private int getMaxScrollPosition() {
        int maxScrollPosition = getPreviewsSize() - getDrawingAreaWidth();
        if (maxScrollPosition < 0) {
            maxScrollPosition = 0;
        }
        return maxScrollPosition;
    }

    public void setScrollPosition(int scrollPosition) {
        if (scrollPosition > getMaxScrollPosition()) {
            scrollPosition = getMaxScrollPosition();
        }
        if (scrollPosition < 0) {
            scrollPosition = 0;
        }
        this.scrollPosition = scrollPosition;
        if (getSlotPosition() < 0) {
            //adjust slotPosition to 0
            startIndex = (int)(scrollPosition * getCompression());
        }
        if (getSlotPosition() > getMaxSlotPosition()) {
            //adjust slotPosition to slotMaxPosition
            startIndex = (int)((scrollPosition + getMaxSlotPosition()) * getCompression());
        }
    }

    public void setStartIndex(int startIndex) {
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (startIndex > getMaxStartIndex()) {
            startIndex = getMaxStartIndex();
        }
        this.startIndex = startIndex;
        if (getSlotPosition() < 0) {
            //adjust slotPosition to 0
            setScrollPosition((int)(startIndex / getCompression()));
        }
        if (getSlotPosition() > getMaxSlotPosition()) {
            //adjust slotPosition to slotMaxPosition
            setScrollPosition((int)(startIndex / getCompression()) - getMaxSlotPosition());
        }
    }


    public void setDrawingAreaWidth(int drawingAreaWidth) {
        this.drawingAreaWidth = drawingAreaWidth;
        if (startIndex > getMaxStartIndex()) {
            startIndex = getMaxStartIndex();
        }
        if (scrollPosition > getMaxScrollPosition()) {
            scrollPosition = getMaxScrollPosition();
        }
        if (getSlotPosition() > getMaxSlotPosition()) {
            //adjust slotPosition to slotMaxPosition
            startIndex = (int)((scrollPosition + getMaxSlotPosition()) * getCompression());
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

    public void moveForward() {
        int shift = (int) (getDrawingAreaWidth() * 0.25);  //прокрутка
        int newStartIndex = getStartIndex() + shift;
        setStartIndex(newStartIndex);
    }

    public void moveBackward() {
        if (isAutoScroll()) {
            int newSlotPosition = getSlotPosition() - AUTO_SCROLL_GAP - 1; //to stop autoScroll
            if (newSlotPosition < 0) {
                newSlotPosition = 0;
            }
            moveSlot(newSlotPosition);
        } else {
            int shift = (int) (getDrawingAreaWidth() * 0.25);
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

    public double getGraphFrequency() {
        return timeFrequency;
    }

    public double getPreviewFrequency() {
        return timeFrequency / compression;
    }
}
