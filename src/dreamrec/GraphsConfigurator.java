package dreamrec;

import data.DataSet;
import filters.FilterAbs;
import filters.FilterDerivative;
import filters.FilterDerivativeRem;
import graph.CompressionType;
import gui.DataView;

/**
 * Created by mac on 04/12/14.
 */
public class GraphsConfigurator {

    public static void configure(DataView view, DataStore dataStore) {
        if(dataStore.getNumberOfChannels() > 0) {
            DataSet channel_1 = dataStore.getChannelData(0);
            view.addGraphPanel(1, true);
            //view.addGraphs(new FilterOffset_1(channel_1, view));
            view.addGraphs(channel_1);


          //  view.addPreviewPanel(1, false);
            DataSet velocityRem =  new FilterAbs(new FilterDerivativeRem(channel_1));
            //DataSet compressedVelocityRem =  new CompressorMaximizing(velocityRem, view.getCompression());
           // view.addPreviews(velocityRem);
        }

    }

    public static void configureRem(DataView view, RemDataStore dataStore) {
        DataSet channel_1 = dataStore.getEogData();
        DataSet channel_2 = dataStore.getAccelerometerXData();

        view.addGraphPanel(2, true);
        //view.addGraphs(new FilterOffset_1(channel_1, view));
        view.addGraphs(channel_1);
     //   view.addGraphPanel(1, true);
      //  view.addGraphs(new FilterDerivative(channel_1));
        view.addGraphPanel(2, true);
        view.addGraphs(new FilterDerivative(channel_2));


        view.addPreviewPanel(1, false);
        DataSet velocityRem =  new FilterAbs(new FilterDerivativeRem(channel_1));
       // DataSet compressedVelocityRem =  new CompressorMaximizing(velocityRem, view.getCompression());
        view.addPreviews(CompressionType.MAX, velocityRem);
    }
}
