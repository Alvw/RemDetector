package dreamrec;

import data.DataSet;
import filters.*;
import gui.View;

/**
 * Created by mac on 04/12/14.
 */
public class GraphsConfigurator {

    protected static void configurate(View view, DataStore dataStore) {
        view.clear();
        baseConfiguration(view, dataStore);
    }

    private static void baseConfiguration(View view, DataStore dataStore) {
        DataSet channel_1 = dataStore.getChannelData(0);
        DataSet channel_2 = dataStore.getChannelData(2);

        //  graphsViewer.addGraph(0, new FilterHiPass(channel_1, 100));
        view.addGraphPanel(1, true);
        view.addGraphs(new FilterOffset_1(channel_1, view.getGraphsView()));
        view.addGraphPanel(1, true);
        view.addGraphs(new FilterDerivative(channel_1));
        view.addGraphPanel(1, true);
        view.addGraphs(new FilterDerivative(channel_2));

        view.addPreviewPanel(1, false);
        DataSet velocityRem =  new FilterAbs(new FilterDerivativeRem(channel_1));
        DataSet compressedVelocityRem =  new CompressorMaximizing(velocityRem, view.getGraphsView().getCompression());
        view.addPreviews(compressedVelocityRem);
    }

    private static void GalaConfiguration(View view, DataStore dataStore) {

    }

    private static void GenaConfiguration(View view, DataStore dataStore) {

    }
}
