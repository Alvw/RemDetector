package graph;

/**
 *  Presenter действует над Моделью и Представлением.
 *  Класс-прослойка, позволяющая удалить зависимость View от Model
 *
 *  Позаимствовано из шаблона MVP. Но в MVP презентер также выполняет функции контроллера,
 *  Мы же используем презентер в чистом виде. Единственное что он делает это
 *  извлекает данные из Модели и форматирует их для отображения в Представлении.
 *
 *  В отличие от практически неизменяемого и независимого Контроллера Презентер является
 *  максимально зависящим от деталей реализации и изменчивым классом.
 *  По сути при любых изменениях он просто переписывается заново.
 *  Именно поэтому его и нужно отделять от Контроллера
 *
 *  Связь Презентера с Контроллером осуществляется через шаблон "Observer".
 *  Контроллер оповещает всех своих слушателей когда в системе (Модели) были сделаны изменения
 *  Тем самым Презентер знает, когда нужно обновлять Представление
 */

public class GraphPresenter implements GraphControllerListener{
    private GraphModel graphModel;
    private GraphView graphView;

    public GraphPresenter(GraphModel graphModel, GraphView graphView) {
        this.graphModel = graphModel;
        this.graphView = graphView;
    }

    @Override
    public void dataUpdated() {
        int newScrollMaximum = graphModel.getPreviewsSize();
        int newScrollExtent = graphModel.getDrawingAreaWidth();
        int newScrollValue = graphModel.getScrollPosition();
        graphView.setScrollData(newScrollMaximum, newScrollExtent, newScrollValue);
        graphView.setGraphStartIndex(graphModel.getStartIndex());
        graphView.setPreviewStartIndex(graphModel.getScrollPosition());
        graphView.setSlotPosition(graphModel.getSlotPosition());
        graphView.setSlotWidth(graphModel.getSlotWidth());
        graphView.setStartTime(graphModel.getStartTime());

        graphView.setGraphTimeFrequency(graphModel.getGraphFrequency());
        graphView.setPreviewTimeFrequency(graphModel.getPreviewFrequency());

        for (int i = 0; i < graphView.getNumberOfGraphPanels(); i++) {
            graphView.setPanelGraphs(graphModel.getGraphCluster(i), i);
        }
        for (int i = 0; i < graphView.getNumberOfPreviewPanels(); i++) {
            graphView.setPanelPreviews(graphModel.getPreviewCluster(i), i);
        }

        graphView.repaint();
    }
}
