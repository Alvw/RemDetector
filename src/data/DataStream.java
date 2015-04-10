package data;

public interface DataStream {
    //Returns the number of elements that can be read  from this stream
    public int available();
    //Returns the next elements from this stream
    public int getNext();

    public double getFrequency();
    public long getStartTime();
    public DataDimension getDataDimension();
}
