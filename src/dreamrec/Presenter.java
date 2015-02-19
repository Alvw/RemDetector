package dreamrec;

import data.DataSet;
import filters.*;
import graph.CompressionType;
import graph.GraphViewer;

import gui.MainWindow;

/**
 * Created by mac on 19/02/15.
 */
public class Presenter implements  ControllerListener {
    private final double PREVIEW_TIME_FREQUENCY = 50.0 / 750;

    MainWindow mainWindow;
    GraphViewer graphViewer;

    public Presenter(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    @Override
    public void dataStoreUpdated(Object dataStore) {
        graphViewer = new GraphViewer();
        graphViewer.setPreviewFrequency(PREVIEW_TIME_FREQUENCY);
        mainWindow.setGraphViewer(graphViewer);

        if(dataStore instanceof DataStore) {
            DataStore dataStore1 = (DataStore) dataStore;
            configureGraphViewer(dataStore1);
            dataStore1.addListener(new DataStoreListener() {
                @Override
                public void onDataUpdate() {
                    graphViewer.autoScroll();
                }
            });
        }

        if(dataStore instanceof RemDataStore) {
            RemDataStore remDataStore = (RemDataStore) dataStore;
            configureRemGraphViewer(remDataStore);
            remDataStore.addListener(new DataStoreListener() {
                @Override
                public void onDataUpdate() {
                    graphViewer.autoScroll();
                }
            });
        }
    }


    private void configureRemGraphViewer(RemDataStore remDataStore) {
        DataSet channel_1 = remDataStore.getEogData();
        DataSet channel_origin = remDataStore.getChannelData(0);


        graphViewer.addGraphPanel(2, true);
        graphViewer.addGraph(channel_1);
        graphViewer.addGraphPanel(1, false);
        graphViewer.addGraph(new FilterAbs(new FilterDerivativeRem(channel_1)));
        graphViewer.addGraph(new FilterConstant(channel_1, 400));
        graphViewer.addGraphPanel(1, false);
        graphViewer.addGraph(remDataStore.getAccMovement());
        graphViewer.addGraph(remDataStore.getAccLimit());


        DataSet accLimit = new FilterMovementTreshhold(remDataStore.getAccelerometerXData(),remDataStore.getAccelerometerYData(), remDataStore.getAccelerometerZData(), 0.15);

        graphViewer.addPreviewPanel(1, false);
        DataSet velocityRem =  new FilterAbs(new FilterDerivativeRem(channel_1));
        // graphViewer.addPreview(velocityRem, CompressionType.MAX);
        DataSet limit = new FilterLimit(new FilterDerivativeRemTreshhold(channel_1, 400), accLimit);
        DataSet velocityClean = new Multiplexer(velocityRem, limit);
        graphViewer.addPreview(velocityClean, CompressionType.MAX);

        graphViewer.addPreviewPanel(1, true);
        graphViewer.addPreview(channel_origin, CompressionType.AVERAGE);

    }

    private void configureGraphViewer(DataStore dataStore) {
        if(dataStore.getNumberOfChannels() > 0) {
            DataSet channel_1 = dataStore.getChannelData(0);
            graphViewer.addGraphPanel(1, true);
            graphViewer.addGraph(channel_1);


            graphViewer.addPreviewPanel(1, false);
            DataSet velocityRem =  new FilterAbs(new FilterDerivativeRem(channel_1));
            graphViewer.addPreview(velocityRem, CompressionType.MAX);
        }

    }
}
