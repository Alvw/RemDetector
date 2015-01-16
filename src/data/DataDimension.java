package data;

public class DataDimension {
    private int digitalMin = Integer.MIN_VALUE;
    private int digitalMax = Integer.MAX_VALUE;
    private double physicalMin = Integer.MIN_VALUE;
    private double physicalMax = Integer.MAX_VALUE;
    private String physicalDimension = "";

    public int getDigitalMin() {
        return digitalMin;
    }

    public int getDigitalMax() {
        return digitalMax;
    }

    public double getPhysicalMin() {
        return physicalMin;
    }

    public double getPhysicalMax() {
        return physicalMax;
    }

    public String getPhysicalDimension() {
        return physicalDimension;
    }

    public void setDigitalMin(int digitalMin) {
        this.digitalMin = digitalMin;
    }

    public void setDigitalMax(int digitalMax) {
        this.digitalMax = digitalMax;
    }

    public void setPhysicalMin(double physicalMin) {
        this.physicalMin = physicalMin;
    }

    public void setPhysicalMax(double physicalMax) {
        this.physicalMax = physicalMax;
    }

    public void setPhysicalDimension(String physicalDimension) {
        this.physicalDimension = physicalDimension;
    }

}
