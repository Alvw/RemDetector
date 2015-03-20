package filters;

import data.DataSet;
import functions.Function;

public class FilterFourier extends Function {

    public FilterFourier(DataSet inputData) {
        super(inputData);
    }

    @Override
    public int get(int index) {
        double frequencyStep = 1 / getFrequency();
        double frequency = index * frequencyStep;
        if(frequency < 0.1 || frequency > 40)  {
            return 0;
        }
        double delta = frequency * 0.05;
        int numberOfPoints = (int)(delta / frequencyStep);
        int result = 0;
        for( int i = Math.max(0, index - numberOfPoints); i <= Math.min(inputData.size(), index + numberOfPoints); i++ ) {
            result = result + inputData.get(i);
        }
        return result;
    }
}
