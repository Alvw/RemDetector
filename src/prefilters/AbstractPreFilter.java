package prefilters;

import data.DataList;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPreFilter implements PreFilter {
    protected int divider = 1;
    protected PreFilter output;
    protected List<PreFilter> listenersList = new ArrayList<PreFilter>();

    protected AbstractPreFilter() {

    }

    public AbstractPreFilter(PreFilter output) {
        if(output !=  null) {
            this.output = output;
            listenersList.add(output);
        }
    }

    @Override
    public void addListener(PreFilter listener) {
        if(output != null) {
            output.addListener(listener);
        }
        else{
            listenersList.add(listener);
        }
    }

    @Override
    public int getDivider() {
        if(output != null) {
            return divider* output.getDivider();
        }
        return divider;
    }

    protected void notifyListeners(int filteredValue) {
        for(PreFilter listener : listenersList) {
            listener.add(filteredValue);
        }
    }
}