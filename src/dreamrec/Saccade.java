package dreamrec;

public class Saccade {
    private int beginIndex;
    private int peakIndex;
    private int endIndex;
    private int peakValue;

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
