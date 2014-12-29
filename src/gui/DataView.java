package gui;

import dreamrec.DataStoreListener;
import graph.GraphsView;

/**
 * Created by mac on 06/12/14.
 */
public class DataView extends GraphsView implements DataStoreListener {

    @Override
    public void onDataUpdate() {
        repaint();
    }

    @Override
    public void onStart(long startTime) {
        setStart(startTime);
    }
}
