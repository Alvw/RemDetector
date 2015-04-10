package graph;

import data.CompressionType;
import data.DataSeries;
import data.FrequencyConverter;
import data.FrequencyConverterRuntime;
import dreamrec.FourierAnalizer;
import fft.Fourie;
import fft.FourierViewer;
import filters.FilterFourierIntegral;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 04/03/15.
 */
public class FourierHandler implements FourierListener, GraphControllerListener {
    private GraphModel graphModel;
    private List<DataSeries> graphList = new ArrayList<DataSeries>();
    private List<FourierViewer> fourierViewerList = new ArrayList<FourierViewer>();


    public FourierHandler(GraphModel graphModel) {
        this.graphModel = graphModel;
    }

    @Override
    public void doFourier(DataSeries graph, int startIndex) {
        graphList.add(graph);
        fourierViewerList.add(new FourierViewer(calculateFourier(graph)));
    }

    @Override
    public void dataUpdated() {
         for(int i = 0; i < fourierViewerList.size(); i++) {
             fourierViewerList.get(i).showGraph(calculateFourier(graphList.get(i)));
         }
    }

    private DataSeries calculateFourier(DataSeries graph) {
        double time = 6; // sec
        DataSeries fourier =  Fourie.fftForward(graph, graphModel.getStartIndex(), time);
        DataSeries fourierIntegral = new FilterFourierIntegral(fourier);


        System.out.println("has Alpha " +FourierAnalizer.hasAlfa(fourier));
        System.out.println("high " +FourierAnalizer.getHighFrequenciesSum(fourier));
        System.out.println("" );


        FrequencyConverter result = new FrequencyConverterRuntime(fourierIntegral, CompressionType.SUM);
        //result = new FrequencyConverterRuntime(fourier, CompressionType.SUM);
        result.setCompression(0.25);



        return result;
    }
}
