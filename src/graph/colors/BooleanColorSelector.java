package graph.colors;

import data.DataSeries;

import java.awt.*;

/**
 * Created by mac on 19/08/15.
 */
public class BooleanColorSelector implements  ColorSelector {
    private DataSeries inputData;
    int digitalLimit;

    public BooleanColorSelector(DataSeries inputData, double physLimit) {
        this.inputData = inputData;
        double gain = 1;
        if(inputData.getScaling() != null) {
            gain = inputData.getScaling().getDataGain();
        }
        digitalLimit = (int)(physLimit / gain);
    }

    @Override
    public Color getColor(int index) {
        if(index < inputData.size()) {
            int difference = inputData.get(index) - digitalLimit + 10;
            int red = 255 * difference/ digitalLimit * 10;
            if(red < 0) {red = 0; }
            if(red > 255) {red = 255;}
            return new Color(red, 0, 0, 100);

        }
        return null;
    }
}
