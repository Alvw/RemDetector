package bdf;

/**
 * (physValue - physMin) / (digValue - digMin)  = constant [Gain] = (physMax - physMin) / (digMax - digMin)
 *  Thus:
 *  physValue = physMin + (digValue - digMin) * (physMax - physMin) / (digMax - digMin); or
 *
 *  physValue = digValue * gain + offset;
 *
 *  where:
 *  gain = (physMax - physMin) / (digMax - digMin)
 *  offset = (physMin - digMin * gain) = (digMax * physMin - digMin * physMax) / (digMax - digMin)
 */

public class Calibration {
    private int digitalMin = Integer.MIN_VALUE;
    private int digitalMax = Integer.MAX_VALUE;
    private double physicalMin = Integer.MIN_VALUE;
    private double physicalMax = Integer.MAX_VALUE;
    private String physicalDimension = "";  // uV or degreeC

    public double getGain() {
        return (physicalMax - physicalMin) / ((long)digitalMax - (long)digitalMin);
    }

    public double getOffset() {
        return (digitalMax * physicalMin - digitalMin * physicalMax) / ((long)digitalMax - (long)digitalMin);
    }

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
