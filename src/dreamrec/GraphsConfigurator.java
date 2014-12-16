package dreamrec;

import data.DataSet;
import filters.CompressorMaximizing;
import filters.FilterAbs;
import filters.FilterDerivative;
import filters.FilterDerivativeRem;
import gui.DataView;

/**
 * Created by mac on 04/12/14.
 */
public class GraphsConfigurator {

    protected static void configurate(DataView view, DataStore dataStore) {
        galaConfiguration(view, dataStore);
    }

    private static void baseConfiguration(DataView view, DataStore dataStore) {
        DataSet channel_1 = dataStore.getChannelData(0);
        DataSet channel_2 = dataStore.getChannelData(2);

        view.addGraphPanel(1, true);
        //view.addGraphs(new FilterOffset_1(channel_1, view));
        view.addGraphs(channel_1);
        view.addGraphPanel(1, true);
        view.addGraphs(new FilterDerivative(channel_1));
        view.addGraphPanel(1, true);
        view.addGraphs(new FilterDerivative(channel_2));

        view.addPreviewPanel(1, false);
        DataSet velocityRem =  new FilterAbs(new FilterDerivativeRem(channel_1));
        DataSet compressedVelocityRem =  new CompressorMaximizing(velocityRem, view.getCompression());
        view.addPreviews(compressedVelocityRem);
    }

    private static void galaConfiguration(DataView view, DataStore dataStore) {
        DataSet channel_1 = dataStore.getChannelData(0);


        view.addGraphPanel(1, true);
        //view.addGraphs(new FilterOffset_1(channel_1, view));
        view.addGraphs(channel_1);
        view.addGraphPanel(1, true);
        view.addGraphs(new FilterDerivative(channel_1));

        view.addPreviewPanel(1, false);
        DataSet velocityRem =  new FilterAbs(new FilterDerivativeRem(channel_1));
        DataSet compressedVelocityRem =  new CompressorMaximizing(velocityRem, view.getCompression());
        view.addPreviews(compressedVelocityRem);
    }
}
