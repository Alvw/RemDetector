package filters;

import data.DataSeries;
import functions.Function;

public class FilterPowerIntegral extends Function{

    private int numberOfPoints;

    public FilterPowerIntegral(DataSeries inputData, int numberOfPoints) {
        super(inputData);
        this.numberOfPoints = numberOfPoints;
    }

    @Override
    public int get(int index) {
        if(index < numberOfPoints * 2) {
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
            y_before += inputData.get(i);
        }
        int y_after = 0;
        for (int i = endIndex + 1; i <= endIndex + numberOfPoints; i++) {
            y_after += inputData.get(i);
        }

        return (int)Math.sqrt(Math.abs(y*y - y_before * y_after))/numberOfPoints;
    }

    @Override
    public int size() {
        return inputData.size() - numberOfPoints * 2;
    }
}

