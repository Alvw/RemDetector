package graph;

import data.CompressionType;
import data.DataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер (controller) — это объект, не относящийся к интерфейсу пользователя и
 * отвечающий за обработку системных событий (обытие высокого уровня, генерируемое внешним исполнителем)
 * Контроллер определяет методы для выполнения системных операций.
 * <p/>
 * Поэтому !!! контроллер принципиально не может иметь зависимость от View !!!
 * как это имеет место в патерне MVC
 */
public class GraphController implements GraphEventHandler {
    private GraphModel graphModel;
    private List<GraphControllerListener> listenerList = new ArrayList<GraphControllerListener>();

    public GraphController(GraphModel graphModel) {
        this.graphModel = graphModel;
    }

    public void addListener(GraphControllerListener listener) {
        listenerList.add(listener);
    }

    public void removeListener(GraphControllerListener listener) {
        listenerList.remove(listener);
    }

    private void fireDataUpdated() {
        for (GraphControllerListener listener : listenerList) {
            listener.dataUpdated();
        }
    }


    @Override
    public void moveSlotForward() {
        graphModel.moveForward();
        fireDataUpdated();
    }

    @Override
    public void moveSlotBackward() {
        graphModel.moveBackward();
        fireDataUpdated();
    }

    @Override
    public void setDrawingAreaWidth(int drawingAreaWidth) {
        graphModel.setDrawingAreaWidth(drawingAreaWidth);
        fireDataUpdated();
    }

    @Override
    public void moveScroll(int scrollPosition) {
        if(scrollPosition != graphModel.getScrollPosition()) {
            graphModel.setScrollPosition(scrollPosition);
            fireDataUpdated();
        }
    }

    @Override
    public void moveSlot(int newSlotPosition) {
        graphModel.moveSlot(newSlotPosition);
        fireDataUpdated();
    }


    public void addGraphCluster() {
        graphModel.addGraphCluster();
        fireDataUpdated();
    }


    public void addPreviewCluster() {
        graphModel.addPreviewCluster();
        fireDataUpdated();
    }


    public void addGraph(DataSet graph, int graphClusterNumber) {
        graphModel.addGraph(graph, graphClusterNumber);
        fireDataUpdated();
    }

    public void removeGraphs(int graphClusterNumber) {
        graphModel.removeGraphs(graphClusterNumber);
        fireDataUpdated();
    }


    public void addPreview(DataSet preview, int previewClusterNumber, CompressionType compressionType) {
        graphModel.addPreview(preview, previewClusterNumber, compressionType);
        fireDataUpdated();
    }


    public void setGraphFrequency(double graphFrequency) {
        graphModel.setGraphFrequency(graphFrequency);
        fireDataUpdated();
    }

    public void setPreviewFrequency(double previewFrequency) {
        graphModel.setPreviewFrequency(previewFrequency);
        fireDataUpdated();
    }


    public void setCompression(int compression) {
        graphModel.setCompression(compression);
        fireDataUpdated();
    }

    public void autoScroll() {
        graphModel.autoScroll();
        fireDataUpdated();
    }
}
