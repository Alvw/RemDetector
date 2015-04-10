package rem;

import data.DataDimension;
import data.DataList;
import data.DataSet;

public class NoiseDetector implements DataSet {
    private DataSet inputData;
    private int numberOfPoints;
    private long sumValue;
    private int currentIndex = -1;
    private DataList noiseList = new DataList();

    public NoiseDetector  (DataSet inputData, int periodMsec) {
        this.inputData = inputData;
        numberOfPoints = Math.round((float)(periodMsec * inputData.getFrequency() / 1000));
    }

    public int get(int index) {
        if(noiseList.size() <= index){
            for(int i = noiseList.size(); i <= index; i++) {
                int noise = calculate1(i);
                noiseList.add(noise * 4);
            }
        }
        return noiseList.get(index);
    }

    public int get_(int index) {
        int noise = 0;
        if(currentIndex >= index) {
           // currentIndex = - 1;
        }
        if(currentIndex < index){
            for(int i = currentIndex + 1; i <= index; i++) {
               noise = calculate1(i);
            }
        }
        return noise;
    }

    private int calculate(int index) {
        currentIndex = index;
        if(index < numberOfPoints) {
            sumValue = sumValue + Math.abs(inputData.get(index));
            return (int)(sumValue / (index + 1));
        }
        else {
            sumValue = sumValue + Math.abs(inputData.get(index)) - Math.abs(inputData.get(index - numberOfPoints));
            return (int)(sumValue / numberOfPoints);
        }
    }

    private int calculate1(int index) {
        currentIndex = index;
        if(index < numberOfPoints) {
            sumValue = sumValue + inputData.get(index) * inputData.get(index);
            return (int)(sumValue / (index + 1));
        }
        else {
            sumValue = sumValue + inputData.get(index) * inputData.get(index) - inputData.get(index - numberOfPoints) * inputData.get(index - numberOfPoints);
            return (int)Math.sqrt(sumValue / numberOfPoints);
        }
    }

    @Override
    public int size() {
        return inputData.size();
    }

    @Override
    public double getFrequency() {
        return inputData.getFrequency();
    }

    @Override
    public long getStartTime() {
        return inputData.getStartTime();
    }

    @Override
    public DataDimension getDataDimension() {
        return inputData.getDataDimension();
    }
}
