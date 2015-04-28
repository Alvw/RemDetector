package data;

public class ScalingImpl implements Scaling{
    private double dataGain = 1;
    private double dataOffset = 0;
    private String dataDimension = "";
    private double samplingInterval = 1;
    private double start = 0;
    private boolean isTimeSeries = false;

    public ScalingImpl() {
    }

    public ScalingImpl(Scaling scaling) {
        if(scaling != null) {
            dataDimension = scaling.getDataDimension();
            dataGain = scaling.getDataGain();
            dataOffset = scaling.getDataOffset();
            samplingInterval = scaling.getSamplingInterval();
            start = scaling.getStart();
            isTimeSeries = scaling.isTimeSeries();
        }
    }

    @Override
    public double getDataGain() {
        return dataGain;
    }

    @Override
    public double getDataOffset() {
        return dataOffset;
    }

    @Override
    public String getDataDimension() {
        return dataDimension;
    }

    @Override
    public double getStart() {
        return start;
    }

    @Override
    public double getSamplingInterval() {
        return samplingInterval;
    }

    @Override
    public boolean isTimeSeries() {
        return isTimeSeries;
    }

    public void setDataGain(double dataGain) {
        this.dataGain = dataGain;
    }

    public void setDataOffset(double dataOffset) {
        this.dataOffset = dataOffset;
    }

    public void setDataDimension(String dataDimension) {
        this.dataDimension = dataDimension;
    }

    public void setTimeSeries(boolean isTimeSeries) {
        this.isTimeSeries = isTimeSeries;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public void setSamplingInterval(double samplingInterval) {
        this.samplingInterval = samplingInterval;
    }

    @Override
    public String toString() {
        return "gain: "+dataGain + "  offset: "+dataOffset+"  dimension: "+dataDimension+"\n"
                +"sampling interval: "+samplingInterval+"  start: "+start +"  isTimeSeries: "+isTimeSeries;
    }

    public static boolean isCompatible(Scaling scaling1,  Scaling scaling2) {
        if(scaling1 != null && scaling2 == null) {
            return false;
        }
        if(scaling1 == null && scaling2 != null) {
            return false;
        }
        if(scaling1 != null && scaling2 != null) {
            if(scaling1.getDataGain() != scaling2.getDataGain())  return false;
            if(scaling1.getDataOffset() != scaling2.getDataOffset())  return false;
            if(scaling1.getDataDimension() != null ? !scaling1.getDataDimension().equals(scaling2.getDataDimension()) : scaling2.getDataDimension() != null)  return false;
            if(scaling1.getSamplingInterval() != scaling2.getSamplingInterval())  return false;
            if(scaling1.getStart() != scaling2.getStart())  return false;
            if(scaling1.isTimeSeries() != scaling2.isTimeSeries())  return false;
        }
        return true;
    }

}
