package graph;

import data.DataSet;
import fft.Fourie;
import fft.FourieView;

/**
 * Created by mac on 04/03/15.
 */
public class FourierHandler implements FourierListener {

    @Override
    public void doFourier(DataSet graph, int startIndex) {
        int time = 90; // sec
        if(graph.getFrequency() >= 500) {
            time = 9;
        }
        new FourieView(Fourie.fft(graph, startIndex, time));
    }
}
