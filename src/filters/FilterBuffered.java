package filters;

import data.DataList;
import data.DataSet;

/**
 *
 */
public abstract class FilterBuffered extends Filter {
    DataList filteredList = new DataList();


    public FilterBuffered(DataSet inputData) {
        super(inputData);
    }

    public int get(int index) {
        checkIndexBounds(index);
        if(filteredList.size() > size()) {
           // filteredList.clear();
        }
        if (filteredList.size() <= index) {
            for (int i = filteredList.size(); i <= index; i++) {
                filteredList.add(getData(i));
            }
        }
        return filteredList.get(index);
    }

    private void checkIndexBounds(int index) {
        if (index > size() || index < 0) {
            throw new IndexOutOfBoundsException("index:  " + index + ", available:  " + size());
        }
    }
}

