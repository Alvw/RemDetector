package dreamrec;

import data.DataStream;
import filters.*;
import graph.GraphsViewer;

import javax.swing.*;

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
        graphsViewer.addCompressedGraphPanel(1, false);
    }

    @Override
    protected void addGraphs() {
        DataStream channel_1 = model.getSignalData(0);
        DataStream channel_2 = model.getSignalData(1);

        //  graphsViewer.addGraph(0, new FilterHiPass(channel_1, 100));
        graphsViewer.addGraph(0, new FilterOffset_1(channel_1, graphsViewer));
        graphsViewer.addGraph(1, new FilterDerivative(channel_1));

        DataStream velocityRem =  new FilterAbs(new FilterDerivativeRem(channel_1));
        DataStream compressedVelocityRem =  new CompressorMaximizing(velocityRem, graphsViewer.getCompression());
        graphsViewer.addCompressedGraph(0, compressedVelocityRem);
    }
}
