package com.dream.Filters;

import com.dream.Data.DataList;
import com.dream.Data.DataStream;

/**
 *
 */
public abstract class FilterBuffered extends Filter {
    DataList filteredList = new DataList();


    public FilterBuffered(DataStream inputData) {
        super(inputData);
    }

    public int get(int index) {
        checkIndexBounds(index);
        if(filteredList.size() > size()) {
            filteredList.clear();
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

