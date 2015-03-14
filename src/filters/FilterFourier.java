package filters;

import data.DataSet;

public class FilterFourier extends Filter {

    public FilterFourier(DataSet inputData) {
        super(inputData);
    }

    @Override
    public int get(int index) {
        double frequencyStep = 1 / getFrequency();
        double frequency = index * frequencyStep;
        double delta = frequency * 0.25;
        int numberOfPoints = (int)(delta / frequencyStep);
        int result = 0;
        for( int i = Math.max(0, index - numberOfPoints); i <= Math.min(inputData.size(), index + numberOfPoints); i++ ) {
            result = result + inputData.get(i);
        }
        return result;
    }
}
