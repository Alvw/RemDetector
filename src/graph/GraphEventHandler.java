package graph;

/**
 * Интерфейс определяющий методы для обработки входных системных событий
 * Системное событие (system event) — это событие высокого уровня,
 * генерируемое внешним исполнителем/пользователем (событие с внешним входом).
 *
 * Через этот интерфейс View связывается с конкретным обработчиком Controller
 */

public interface GraphEventHandler extends SlotListener {
    public void moveSlotForward();
    public void moveSlotBackward();
    public void setDrawingAreaWidth(int drawingAreaWidth);
    public void moveScroll(int scrollPosition);
    public void setGraphFrequency(double graphFrequency);
    public void setPreviewFrequency(double previewFrequency);
}
