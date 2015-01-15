package filters;

import data.Converter;
import data.DataList;
import data.DataSet;

/**
 *
 */
public abstract class FilterBuffered extends Converter {
    DataList filteredList = new DataList();


    public FilterBuffered(DataSet inputData) {
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

