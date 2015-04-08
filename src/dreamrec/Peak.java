package dreamrec;

public class Peak {
    private int beginIndex;
    private int endIndex;
    private int peakIndex;
    private int peakValue;
    private double peakRatio;


    public double getPeakRatio() {
        return peakRatio;
    }

    public void setPeakRatio(double peakRatio) {
        this.peakRatio = peakRatio;
    }

    public int getPeakValue() {
        return peakValue;
    }

    public void setPeakValue(int peakValue) {
        this.peakValue = peakValue;
    }

    public int getBeginIndex() {
        return beginIndex;
    }

    public void setBeginIndex(int beginIndex) {
        this.beginIndex = beginIndex;
    }

    public int getPeakIndex() {
        return peakIndex;
    }

    public void setPeakIndex(int peakIndex) {
        this.peakIndex = peakIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }
}
