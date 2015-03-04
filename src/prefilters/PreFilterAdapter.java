package prefilters;

import data.DataList;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by mac on 02/03/15.
 */
public class PreFilterAdapter implements PreFilter {
    DataList dataList;
    protected List<PreFilter> listenersList = new ArrayList<PreFilter>();

    public PreFilterAdapter(DataList dataList) {
        this.dataList = dataList;
    }

    @Override
    public void add(int value) {
        dataList.add(value);
        notifyListeners(value);

    }

    @Override
    public int getDivider() {
        return 1;
    }

    @Override
    public void addListener(PreFilter listener) {
        listenersList.add(listener);
    }

    protected void notifyListeners(int filteredValue) {
        for(PreFilter listener : listenersList) {
            listener.add(filteredValue);
        }
    }
}
