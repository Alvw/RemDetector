package graph;

import data.CompressionType;
import data.FrequencyConverter;
import data.FrequencyConverterRuntime;
import data.DataSet;
import fft.Fourie;
import fft.FourierViewer;
import filters.FilterFourier;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 04/03/15.
 */
public class FourierHandler implements FourierListener, GraphControllerListener {
    private GraphModel graphModel;
    private List<DataSet> graphList = new ArrayList<DataSet>();
    private List<FourierViewer> fourierViewerList = new ArrayList<FourierViewer>();


    public FourierHandler(GraphModel graphModel) {
        this.graphModel = graphModel;
    }

    @Override
    public void doFourier(DataSet graph, int startIndex) {
        graphList.add(graph);
        fourierViewerList.add(new FourierViewer(calculateFourier(graph)));
    }

    @Override
    public void dataUpdated() {
         for(int i = 0; i < fourierViewerList.size(); i++) {
             fourierViewerList.get(i).showGraph(calculateFourier(graphList.get(i)));
         }
    }

    private DataSet calculateFourier(DataSet graph) {
        int time = 10; // sec
        if(graph.getFrequency() >= 500) {
            time = 36;
        }
        DataSet fourier =  Fourie.fft(graph, graphModel.getStartIndex(), time);
        DataSet fourierPapa = new FilterFourier(fourier);
        FrequencyConverter result = new FrequencyConverterRuntime(fourierPapa, CompressionType.SUM);
        result.setCompression(0.25);
        return result;
    }
}
