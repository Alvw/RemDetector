package filters;

import data.DataSeries;
import data.Scaling;
import data.ScalingImpl;
import functions.Function;

public class FilterPowerStep extends Function {
    private int numberOfPoints;

    public FilterPowerStep(DataSeries inputData, int numberOfPoints) {
        super(inputData);
        this.numberOfPoints = numberOfPoints;
    }

    @Override
    public int get(int index) {
        if(index < 2 * numberOfPoints) {
            return 0;
        }
        int pointsBefore = (numberOfPoints - 1) - (numberOfPoints - 1) / 2;
        int pointsAfter = (numberOfPoints - 1) / 2;
        int startIndex = index - pointsBefore;
        int endIndex = index + pointsAfter;
        int y = 0;
        for (int i = startIndex; i <= endIndex; i++) {
            y += inputData.get(i);
        }
        int y_before = 0;
        for (int i = startIndex - numberOfPoints; i < startIndex; i++) {
            y_before += Math.abs(inputData.get(i));
        }
        int y_after = 0;
        for (int i = endIndex + 1; i <= endIndex + numberOfPoints; i++) {
            y_after += Math.abs(inputData.get(i));
        }

        return y*y / (y_before * y_after);
    }

    @Override
    public int size() {
        return inputData.size() - numberOfPoints * 2;
    }

    @Override
    public Scaling getScaling() {
        ScalingImpl scaling = new ScalingImpl(inputData.getScaling());
        scaling.setDataGain(1);
        scaling.setDataOffset(0);
        scaling.setDataDimension("");
        return scaling;
    }
}
