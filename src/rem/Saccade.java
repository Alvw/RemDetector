package rem;

public class Saccade {
    private int beginIndex;
    private int endIndex;
    private int peakIndex;
    private int peakValue;
    private int threshold;
    private int energy;

    public Saccade(int index, int value, int threshold) {
        beginIndex = index;
        peakValue = value;
        peakIndex = index;
        endIndex = index;
        energy = value * value;
        this.threshold = threshold;
    }

    public void addPoint(int index, int value) {
        if(Math.abs(peakValue) < Math.abs(value)) {
            peakValue = value;
            peakIndex = index;
        }
        endIndex = index;
        energy = energy + value * value;
    }

    public int getWidth() {
        return endIndex - beginIndex + 1;
    }

    public double getPeakToThresholdRatio() {
        double peak = Math.abs(peakValue);
        if(threshold != 0) {
            return peak / threshold;
        }
        return 0;
    }

    public double getPeakToEnergyRatio() {
        double peak = peakValue*peakValue;
        return  peak / (energy/getWidth());
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
