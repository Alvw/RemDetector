package gui;

import data.DataSet;
import dreamrec.Controller;
import filters.*;

/**
 * Main Window of our program...
 */
public class MainView extends View {
    public MainView(Controller controller) {
        super(controller);
    }

    @Override
    protected void addGraphs() {
        DataSet channel_1 = model.getChannelData(0);
        DataSet channel_2 = model.getChannelData(2);

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
