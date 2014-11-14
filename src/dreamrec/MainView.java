package dreamrec;

import data.DataSet;
import filters.*;

/**
 * Main Window of our program...
 */
public class MainView extends View {

    public MainView(Controller controller) {
        super(controller);
    }

    @Override
    protected void addPanels() {
        graphsViewer.addGraphPanel(1, true);
        graphsViewer.addGraphPanel(1, true);
        graphsViewer.addGraphPanel(1, true);
        graphsViewer.addPreviewPanel(1, false);
    }

    @Override
    protected void addGraphs() {
        DataSet channel_1 = model.getSignalData(0);
        DataSet channel_2 = model.getSignalData(2);

        //  graphsViewer.addGraph(0, new FilterHiPass(channel_1, 100));
        graphsViewer.addGraphs(0, new FilterOffset_1(channel_1, graphsViewer));
        graphsViewer.addGraphs(1, new FilterDerivative(channel_1));
        graphsViewer.addGraphs(2, new FilterDerivative(channel_2));

        DataSet velocityRem =  new FilterAbs(new FilterDerivativeRem(channel_1));
        DataSet compressedVelocityRem =  new CompressorMaximizing(velocityRem, graphsViewer.getCompression());
        graphsViewer.addPreviews(0, compressedVelocityRem);
    }
}
