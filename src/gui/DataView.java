package gui;

import dreamrec.DataStoreListener;
import graph.GraphViewer;

/**
 * Created by mac on 06/12/14.
 */
public class DataView extends GraphViewer implements DataStoreListener {

    @Override
    public void onDataUpdate() {
        autoScroll();
    }

}
