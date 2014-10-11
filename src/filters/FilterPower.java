package filters;

import data.DataStream;

/**
 *
 */

public class FilterPower extends Filter {

    private int  distance = 1;

    public FilterPower(DataStream inputData, int distance) {
        super(inputData);
        this.distance = distance;
    }

    @Override
    protected int getData(int index) {
        if(index < distance || index >= (size()-distance)) {
            return 0;
        }
        int y = inputData.get(index);
        int y_before = inputData.get(index - distance);
        int y_after = inputData.get(index + distance);


       // return (int) Math.sqrt(y*y - y_before * y_after) ;
        return y*y - y_before * y_after;
    }
}

