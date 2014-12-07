package gui;

import dreamrec.DataStoreListener;
import graph.GraphsView;

import java.awt.*;

/**
 * Created by mac on 06/12/14.
 */
public class DataView extends GraphsView implements DataStoreListener {

    @Override
    public void onDataUpdate() {
        syncView();
    }

    @Override
    public void onStart(long startTime) {
        setStart(startTime);
    }
}
