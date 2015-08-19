package graph;

import data.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

class GraphModel {
    private static final Log log = LogFactory.getLog(GraphModel.class);

    // bigger GAP - less precision need slot to start autoScroll
    private static final int AUTO_SCROLL_GAP = 2;

    private double compression = 1;
    private double graphsSamplingRate;
    private int startIndex;
    private int scrollPosition;
    private int drawingAreaWidth;

    // graph clusters corresponds to graph panels
    private List<List<Graph>> graphClusterList = new ArrayList<List<Graph>>();
    // preview clusters correspond to preview panels
    private List<List<Graph>> previewClusterList = new ArrayList<List<Graph>>();


    private int getNumberOfGraphs() {
        int graphsNumber = 0;
        for (List<Graph> graphsList : graphClusterList) {
            graphsNumber += graphsList.size();
        }
        return graphsNumber;
    }

    private int getNumberOfPreviews() {
        int previewsNumber = 0;
        for (List<Graph> previewList : previewClusterList) {
            previewsNumber += previewList.size();
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
        graphClusterList.add(new ArrayList<Graph>());
    }

    public void addPreviewCluster() {
        previewClusterList.add(new ArrayList<Graph>());
    }

    public void addGraph(Graph graph, CompressionType compressionType, int graphClusterNumber) {
        if (graphClusterNumber < graphClusterList.size()) {
            DataSeries graphData = graph.getGraphData();
            DataCompressor resultingGraphData = new DataCompressor(graphData, compressionType);
            if(graphData.getScaling() != null) {
                double previewFrequency = graphsSamplingRate / compression;
                double graphFrequency = 1 / graphData.getScaling().getSamplingInterval();
                graphsSamplingRate = Math.max(graphsSamplingRate, graphFrequency);
                updateGraphsSamplingRate(graphsSamplingRate);
                resultingGraphData.setSamplingRate(graphsSamplingRate);
                if(previewFrequency != 0) {
                    compression = graphsSamplingRate / previewFrequency;
                }
            }
            graph.setGraphData(resultingGraphData);
            graphClusterList.get(graphClusterNumber).add(graph);
        }
    }

    public void removeGraphs(int graphClusterNumber) {
        if (graphClusterNumber < graphClusterList.size()) {
            graphClusterList.get(graphClusterNumber).clear();
        }
    }


    public void addPreview(DataSeries previewData, GraphType graphType, CompressionType compressionType, int previewClusterNumber) {
        if (previewClusterNumber < previewClusterList.size()) {
            DataCompressorCollecting resultingPreviewData = new DataCompressorCollecting(previewData, compressionType);
            if (previewData.getScaling() != null) {
                resultingPreviewData.setCompression(compression);
            } else {
                if(graphsSamplingRate == 0) {
                    double previewFrequency = 1 / previewData.getScaling().getSamplingInterval();
                    graphsSamplingRate = previewFrequency;
                }
                double previewFrequency = graphsSamplingRate / compression;
                resultingPreviewData.setSamplingRate(previewFrequency);
                updatePreviewsSamplingRate(previewFrequency);
            }
            previewClusterList.get(previewClusterNumber).add(new Graph(resultingPreviewData, graphType));
        }
    }

    public void setCompression(int compression) {
        if (compression <= 0) {
            compression = 1;
        }
        this.compression = compression;
    }


    private void updateGraphsSamplingRate(double samplingRate) {
        for (List<Graph> graphsList : graphClusterList) {
            for (Graph graph : graphsList) {
                DataCompressor dataCompressor = (DataCompressor) graph.getGraphData();
                dataCompressor.setSamplingRate(samplingRate);
            }
        }
    }

    private void updatePreviewsSamplingRate(double samplingRate) {
        for (List<Graph> previewList : previewClusterList) {
            for (Graph preview : previewList) {
                DataCompressorCollecting dataCompressor = (DataCompressorCollecting) preview.getGraphData();
                dataCompressor.setSamplingRate(samplingRate);
            }
        }
    }
    
    public void setGraphsSamplingRate(double samplingRate) {
        int slotPosition = getSlotPosition();
        if(graphsSamplingRate != 0) {
            compression = samplingRate * compression / graphsSamplingRate;
        }
        graphsSamplingRate = samplingRate;
        updateGraphsSamplingRate(samplingRate);
        moveSlot(slotPosition);
    }

    public void setPreviewsSamplingRate(double samplingRate) {
        int startIndex = getStartIndex();
        if(graphsSamplingRate != 0) {
            compression = graphsSamplingRate / samplingRate;
        }
        else {
            graphsSamplingRate = samplingRate * compression;
        }
        updatePreviewsSamplingRate(samplingRate);
        scrollPosition = 0;
        setStartIndex(startIndex);
    }

    public List<Graph> getGraphCluster(int listNumber) {
        return graphClusterList.get(listNumber);
    }

    public List<Graph> getPreviewCluster(int listNumber) {
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
        for (List<Graph> graphsList : graphClusterList) {
            for (Graph graph : graphsList) {
                graphsSize = Math.max(graphsSize, graph.getGraphData().size());
            }
        }

        int previewsSize = 0;
        for (List<Graph> previewList : previewClusterList) {
            for (Graph preview : previewList) {
                previewsSize = Math.max(previewsSize, preview.getGraphData().size());
            }
        }
        return (int)Math.max(graphsSize, previewsSize * getCompression());
    }

    public int getPreviewsSize() {
        return (int)(getGraphsSize() / getCompression());
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

    public void setScrollPosition(int scrollPosition) {
        if (scrollPosition > getMaxScrollPosition()) {
            scrollPosition = getMaxScrollPosition();
        }
        if (scrollPosition < 0) {
            scrollPosition = 0;
        }
        this.scrollPosition = scrollPosition;
        if(getCompression() == 1) {
            startIndex = scrollPosition;
        }
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
            scrollPosition = ((int)(startIndex / getCompression()));
        }
        if (getSlotPosition() > getMaxSlotPosition()) {
            //adjust slotPosition to slotMaxPosition
            scrollPosition = ((int)(startIndex / getCompression()) - getMaxSlotPosition());
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

    public Scaling getGraphsScaling() {
        Scaling scaling = null;
        for (List<Graph> graphList : graphClusterList) {
            for (Graph graph : graphList) {
                scaling = graph.getGraphData().getScaling();
            }
        }
        return scaling;
    }

    public Scaling getPreviewsScaling() {
        Scaling scaling = null;
        for (List<Graph> previewList : previewClusterList) {
            for (Graph preview : previewList) {
                scaling = preview.getGraphData().getScaling();
            }
        }

        return scaling;
    }
}
