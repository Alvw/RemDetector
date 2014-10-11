package filters;

import data.DataStream;

/**
 *
 */
public class FilterResonance_25Gz extends FilterBuffered {

    public FilterResonance_25Gz(DataStream inputData) {
        super(inputData);
    }

    @Override
    protected int getData(int index) {
        if (index < 5 || (index >= size()-5) ) {
            return 0;
        }

        int sum =
                + inputData.get(index-4)
                - inputData.get(index-3)*2
                + inputData.get(index-2)*3
                - inputData.get(index-1)*4
                + inputData.get(index  )*5
                - inputData.get(index+1)*4
                + inputData.get(index+2)*3
                - inputData.get(index-3)*2
                + inputData.get(index-4)
                ;
        return sum/(25);
    }
}