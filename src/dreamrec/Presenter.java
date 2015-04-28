package dreamrec;

import data.CompressionType;
import data.DataSeries;
import filters.*;
import graph.GraphType;
import graph.GraphViewer;
import gui.MainWindow;
import rem.SaccadeBatchDetector;

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
        rem(remDataStore);
    }

    private void configureGraphViewer(DataStore dataStore) {
        for(int i = 0; i < dataStore.getNumberOfChannels(); i++) {
            DataSeries channel = dataStore.getChannelData(i);
            graphViewer.addGraphPanel(1, true);
            graphViewer.addGraph(channel);
        }

        if(dataStore.getNumberOfChannels() > 0) {
            DataSeries channel = dataStore.getChannelData(0);
            graphViewer.addPreviewPanel(1, false);
            DataSeries velocityRem =  new FilterAbs(new FilterDerivativeRem(channel));
            graphViewer.addPreview(velocityRem, CompressionType.AVERAGE);
        }

    }


    private void rem(RemDataStore remDataStore) {
        DataSeries eogFull = remDataStore.getEogData();
        DataSeries eog = new HiPassCollectingFilter(eogFull, 10);
        DataSeries accMovement = remDataStore.getAccMovementData();
        DataSeries isSleep = remDataStore.isSleep();

        double accMovementLimit = remDataStore.getAccMovementLimit();

        FilterDerivativeRem eogDerivativeRem =  new FilterDerivativeRem(eogFull);
        //DataSeries eogDerivativeRem =  new FilterLowPass(new FilterDerivativeRem(eogFull), 25.0);
        DataSeries eogDerivativeRemAbs =  new FilterAbs(eogDerivativeRem);

        SaccadeBatchDetector saccades = new SaccadeBatchDetector(eogFull);

        graphViewer.addGraphPanel(2, true);
        graphViewer.addGraph(eog);

        graphViewer.addGraphPanel(2, false);
        graphViewer.addGraph(eogDerivativeRemAbs);
        graphViewer.addGraph(new FilterConstant(eog, saccades.getSaccadeMaxValuePhysical()));
        graphViewer.addGraph(saccades.getThresholds());

        graphViewer.addGraphPanel(2, false);
        graphViewer.addGraph(saccades);

        graphViewer.addGraphPanel(2, true);
        graphViewer.addGraph(new FilterHiPass(new FilterBandPass_Alfa(eog), 2));

        graphViewer.addGraphPanel(1, false);
        graphViewer.addGraph(accMovement);
        graphViewer.addGraph(new FilterConstant(accMovement, accMovementLimit));

        graphViewer.addPreviewPanel(2, false);
        graphViewer.addPreview(eogDerivativeRemAbs, CompressionType.MAX);
        graphViewer.addPreview(isSleep, GraphType.BOOLEAN, CompressionType.BOOLEAN);


       graphViewer.addPreviewPanel(2, false);
       graphViewer.addPreview(saccades, CompressionType.MAX);
    }
}
