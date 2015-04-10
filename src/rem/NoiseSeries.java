package rem;

import data.BufferedData;
import data.DataDimension;
import data.DataSeries;

public class NoiseSeries implements DataSeries {
    private DataSeries noiseList;

    public NoiseSeries(DataSeries inputData, int periodMsec) {
       NoiseDetector noiseDetector = new NoiseDetector(inputData, periodMsec);
       noiseList = new BufferedData(noiseDetector);
    }

    public int get(int index) {
        return noiseList.get(index) * 4;
    }


    @Override
    public int size() {
        return noiseList.size();
    }

    @Override
    public double getFrequency() {
        return noiseList.getFrequency();
    }

    @Override
    public long getStartTime() {
        return noiseList.getStartTime();
    }

    @Override
    public DataDimension getDataDimension() {
        return noiseList.getDataDimension();
    }
}
