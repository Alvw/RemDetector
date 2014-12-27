package graph;

public class GraphsSettings {
    protected static final int X_INDENT = 50;
    protected static final int Y_INDENT = 20;
    private  int compression = 750;
    private double timeFrequency;
    private long startTime;
    private int startIndex;

    public int getCompression() {
        return compression;
    }

    public void setCompression(int compression) {
        this.compression = compression;
    }

    public double getTimeFrequency() {
        return timeFrequency;
    }

    public void setTimeFrequency(double timeFrequency) {
        this.timeFrequency = timeFrequency;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
