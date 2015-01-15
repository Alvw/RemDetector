package filters;

import data.Converter;
import data.DataSet;

/**
 *
 */
public class FilterThreshold_mod extends Converter {
    private int bufferSize = 4;
    private int shift = 0; // points
  
    public FilterThreshold_mod(DataSet inputData) {
        super(inputData);
    }

    @Override
    public int get(int index) {
        int sum = Integer.MAX_VALUE;
        int indexShifted = index - shift;

        if (indexShifted <= 20*bufferSize) {
            for (int i = 0; i <= index; i++) {
                sum += Math.abs(inputData.get(i));
            }
            return sum/(index+1);
        }

        for (int j = 0; j < 4; j++) {
            int sum_j = 0;
            indexShifted = indexShifted - 1;
            for (int i = (indexShifted - bufferSize); i < indexShifted; i++) {
                sum_j += Math.abs(inputData.get(i));
            }
            sum = Math.min(sum, sum_j);
        }
        return 10*sum/bufferSize;
    }
}
