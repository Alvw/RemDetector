package dreamrec;

import data.DataSet;
import filters.*;
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
            //view.addGraph(new FilterOffset_1(channel_1, view));
            view.addGraph(channel_1);


          //  view.addPreviewPanel(1, false);
            DataSet velocityRem =  new FilterAbs(new FilterDerivativeRem(channel_1));
            //DataSet compressedVelocityRem =  new CompressorMaximizing(velocityRem, view.getCompression());
           // view.addPreview(velocityRem);
        }

    }

    public static void configureRem(DataView view, RemDataStore dataStore) {
        DataSet channel_1 = dataStore.getEogData();
        DataSet channel_2 = dataStore.getAccelerometerXData();

        view.addGraphPanel(2, true);
        //view.addGraph(new FilterOffset_1(channel_1, view));
        view.addGraph(channel_1);
        view.addGraphPanel(1, false);
        view.addGraph(new FilterAbs(new FilterDerivativeRem(channel_1)));
        view.addGraph(new FilterConstant(channel_1, 400));
        view.addGraphPanel(1, false);
        view.addGraph(dataStore.getAccMovement());
        view.addGraph(dataStore.getAccLimit());

        DataSet accLimit = new FilterMovementTreshhold(dataStore.getAccelerometerXData(),dataStore.getAccelerometerYData(), dataStore.getAccelerometerZData(), 0.15);

        view.addPreviewPanel(1, false);
        DataSet velocityRem =  new FilterAbs(new FilterDerivativeRem(channel_1));
       // view.addPreview(velocityRem, CompressionType.MAX);
        DataSet limit = new FilterLimit(new FilterDerivativeRemTreshhold(channel_1, 400), accLimit);
        DataSet velocityClean = new Multiplexer(velocityRem, limit);
        view.addPreview(velocityClean, CompressionType.MAX);
    }
}
