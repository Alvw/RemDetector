package functions;

import data.DataSeries;

public class Trigger extends Function{
    int digitalLimit;


    public Trigger(DataSeries inputData, double physLimit) {
        super(inputData);
        double gain = 1;
        if(inputData.getScaling() != null) {
            gain = inputData.getScaling().getDataGain();
        }
        digitalLimit = (int)(physLimit / gain);
    }

    @Override
    public int get(int index) {
        if(Math.abs(inputData.get(index)) < digitalLimit) {
            return 1;
        }
        return 0;
    }

}
