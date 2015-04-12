package dreamrec;

import data.CompressionType;
import data.DataSeries;
import filters.*;
import graph.GraphType;
import graph.GraphViewer;
import gui.MainWindow;
import rem.SaccadeGroupDetector;

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
       //workRem(remDataStore);
        galaRem(remDataStore);
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

    private void workRem(RemDataStore remDataStore) {
        DataSeries eog = remDataStore.getEogData();
        DataSeries eogFull = remDataStore.getEogFullData();
        DataSeries accMovement = remDataStore.getAccMovementData();
        DataSeries isSleep = remDataStore.isSleep();

        double accMovementLimit = remDataStore.getAccMovementLimit();
        double eogDerivativeLimit = remDataStore.getEogRemDerivativeMax();

        DataSeries eogDerivativeRem =  new FilterDerivativeRem(eog);
        DataSeries eogDerivativeRemAbs =  new FilterAbs(eogDerivativeRem);

        graphViewer.addGraphPanel(2, true);
        graphViewer.addGraph(eog);
        graphViewer.addGraphPanel(2, false);
        graphViewer.addGraph(eogDerivativeRemAbs);
        graphViewer.addGraph(new FilterConstant(eog, eogDerivativeLimit));
        graphViewer.addGraphPanel(2, false);
        graphViewer.addGraph(accMovement);
        graphViewer.addGraph(new FilterConstant(accMovement, accMovementLimit));
        graphViewer.addGraphPanel(2, true);
        graphViewer.addGraph(new FilterHiPass(new FilterBandPass_Alfa(eog), 2));

        graphViewer.addPreviewPanel(2, false);
        graphViewer.addPreview(eogDerivativeRemAbs, CompressionType.MAX);
        graphViewer.addPreview(isSleep, GraphType.BOOLEAN, CompressionType.BOOLEAN);

        graphViewer.addPreviewPanel(2, true);
        graphViewer.addPreview(eogFull, CompressionType.AVERAGE);

    }

    private void galaRem(RemDataStore remDataStore) {
        DataSeries eog = remDataStore.getEogData();
        DataSeries eogFull = remDataStore.getEogFullData();
        DataSeries accMovement = remDataStore.getAccMovementData();
        DataSeries isSleep = remDataStore.isSleep();

        double accMovementLimit = remDataStore.getAccMovementLimit();
        double eogDerivativeLimit = remDataStore.getEogRemDerivativeMax();

        DataSeries eogDerivativeRem =  new FilterDerivativeRem(eogFull);
        //DataSeries eogDerivativeRem =  new FilterLowPass(new FilterDerivativeRem(eogFull), 25.0);
        DataSeries eogDerivativeRemAbs =  new FilterAbs(eogDerivativeRem);

        SaccadeGroupDetector saccadesRem = new SaccadeGroupDetector(eogFull);

        graphViewer.addGraphPanel(2, true);
        graphViewer.addGraph(eog);
        graphViewer.addGraphPanel(2, true);
        graphViewer.addGraph(new FilterDerivative(eogFull));
        //graphViewer.addGraph(saccades);
       // graphViewer.addGraphPanel(2, true);
        //graphViewer.addGraph(new FilterDerivative_N(eogFull, 2));

        graphViewer.addGraphPanel(2, false);
        graphViewer.addGraph(accMovement);
        graphViewer.addGraph(new FilterConstant(accMovement, accMovementLimit));

        graphViewer.addGraphPanel(2, false);
        graphViewer.addGraph(saccadesRem);

        graphViewer.addGraphPanel(2, false);
        graphViewer.addGraph(eogDerivativeRemAbs);
       // graphViewer.addGraph(new NoiseSeries(eogDerivativeRem, 200));
       // graphViewer.addGraph(new NoiseSeries(new FilterDerivativeRem(eogDerivativeRem), 200));
        graphViewer.addGraph(saccadesRem.getThresholds());


        graphViewer.addPreviewPanel(2, false);
        graphViewer.addPreview(eogDerivativeRemAbs, CompressionType.MAX);
        graphViewer.addPreview(isSleep, GraphType.BOOLEAN, CompressionType.BOOLEAN);


       graphViewer.addPreviewPanel(2, false);
       graphViewer.addPreview(saccadesRem, CompressionType.MAX);
    }
}
