package dreamrec;

import data.DataSet;
import filters.*;
import data.CompressionType;
import graph.GraphType;
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
       //workRem(remDataStore);
        galaRem(remDataStore);
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

    private void workRem(RemDataStore remDataStore) {
        DataSet eog = remDataStore.getEogData();
        DataSet eogFull = remDataStore.getEogFullData();
        DataSet accMovement = remDataStore.getAccMovementData();
        DataSet isSleep = remDataStore.isSleep();

        double accMovementLimit = remDataStore.getAccMovementLimit();
        double eogDerivativeLimit = remDataStore.getEogRemDerivativeMax();

        DataSet eogDerivativeRem =  new FilterDerivativeRem(eog);
        DataSet eogDerivativeRemAbs =  new FilterAbs(eogDerivativeRem);

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
        DataSet eog = remDataStore.getEogData();
        DataSet eogFull = remDataStore.getEogFullData();
        DataSet accMovement = remDataStore.getAccMovementData();
        DataSet isSleep = remDataStore.isSleep();

        double accMovementLimit = remDataStore.getAccMovementLimit();
        double eogDerivativeLimit = remDataStore.getEogRemDerivativeMax();

       // DataSet eogDerivativeRem =  new FilterDerivativeRem(eogFull);
        DataSet eogDerivativeRem =  new FilterLowPass(new FilterDerivativeRem(eogFull), 25.0);
        DataSet eogDerivativeRemAbs =  new FilterAbs(eogDerivativeRem);

        SaccadeDetector saccadesRem = new SaccadeDetector(eogDerivativeRem);

        graphViewer.addGraphPanel(2, true);
        graphViewer.addGraph(eog);
        graphViewer.addGraphPanel(2, true);
        graphViewer.addGraph(new FilterDerivative(eogFull));
        //graphViewer.addGraph(saccades);
       // graphViewer.addGraphPanel(2, true);
        //graphViewer.addGraph(new FilterDerivative_N(eogFull, 2));

        graphViewer.addGraphPanel(1, false);
        graphViewer.addGraph(saccadesRem);

        graphViewer.addGraphPanel(2, false);
        graphViewer.addGraph(eogDerivativeRemAbs);
        graphViewer.addGraph(saccadesRem.getThresholds());
        //graphViewer.addGraph(new FilterConstant(eog, eogDerivativeLimit));
        //graphViewer.addGraphPanel(2, true);
        //graphViewer.addGraph(new FilterDerivative(eogDerivativeRem));
       // graphViewer.addGraph(new FilterDerivativeRem(eogDerivativeRem));



        graphViewer.addPreviewPanel(2, false);
        graphViewer.addPreview(eogDerivativeRemAbs, CompressionType.MAX);
        graphViewer.addPreview(isSleep, GraphType.BOOLEAN, CompressionType.BOOLEAN);

        graphViewer.addPreviewPanel(2, false);
        graphViewer.addPreview(saccadesRem, CompressionType.MAX);
    }
}
