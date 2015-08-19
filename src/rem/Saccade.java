package rem;

public class Saccade {
    private int beginIndex;
    private int endIndex;
    private int peakIndex;
    private int peakValue;
    private int sumValue;


    public Saccade(int index, int value) {
        beginIndex = index;
        peakIndex = index;
        endIndex = index;
        sumValue = value;
        peakValue = value;
    }

    public void addPoint(int index, int value) {
        if(Math.abs(peakValue) < Math.abs(value)) {
            peakValue = value;
            peakIndex = index;
        }
        endIndex = index;
        sumValue += value;
    }

    public int getWidth() {
        return endIndex - beginIndex + 1;
    }

    public int getAverageValue() {
        return sumValue / getWidth();
    }

    public int getPeakValue() {
        return peakValue;
    }

    public int getBeginIndex() {
        return beginIndex;
    }

    public int getPeakIndex() {
        return peakIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }
}
