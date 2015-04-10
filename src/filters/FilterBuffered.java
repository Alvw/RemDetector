package filters;

import data.DataList;
import data.DataSeries;
import functions.Function;

/**
 *
 */
public abstract class FilterBuffered extends Function {
    DataList filteredList = new DataList();


    public FilterBuffered(DataSeries inputData) {
        super(inputData);
    }

    @Override
    public int get(int index) {
        if(filteredList.size() > size()) {
           // filteredList.clear();
        }
        if (filteredList.size() <= index) {
            for (int i = filteredList.size(); i <= index; i++) {
                filteredList.add(get(i));
            }
        }
        return filteredList.get(index);
    }

}

