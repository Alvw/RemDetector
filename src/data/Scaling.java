package data;

public interface Scaling {
    // AxisY
    public double getDataGain();
    public double getDataOffset();
    public String getDataDimension();  // uV or degreeC
    // AxisX
    public double getStart();
    public double getSamplingInterval();
    public boolean isTimeSeries();
}
