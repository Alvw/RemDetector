package dreamrec;

import data.DataSet;
import filters.*;

/**
 * Main Window of our program...
 */
public class MainView extends View {
    private int compression = 750;

    public MainView(Controller controller) {
        super(controller);
        setCompression(compression);
    }

    @Override
    protected void addGraphs() {
        DataSet channel_1 = model.getSignalData(0);
        DataSet channel_2 = model.getSignalData(2);

        //  graphsViewer.addGraph(0, new FilterHiPass(channel_1, 100));
        graphsViewer.addGraphPanel(1, true);
        graphsViewer.addGraphs(new FilterOffset_1(channel_1, graphsViewer));
        graphsViewer.addGraphPanel(1, true);
        graphsViewer.addGraphs(new FilterDerivative(channel_1));
        graphsViewer.addGraphPanel(1, true);
        graphsViewer.addGraphs(new FilterDerivative(channel_2));

        graphsViewer.addPreviewPanel(1, false);
        DataSet velocityRem =  new FilterAbs(new FilterDerivativeRem(channel_1));
        DataSet compressedVelocityRem =  new CompressorMaximizing(velocityRem, graphsViewer.getCompression());
        graphsViewer.addPreviews(compressedVelocityRem);
    }
}
