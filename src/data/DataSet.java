package data;

/**
 * Created with IntelliJ IDEA.
 * User: GENA
 * Date: 16.05.14
 * Time: 3:24
 * To change this template use File | Settings | File Templates.
 */

public interface DataSet {
    public int size();
    public int get(int index);

    public double getFrequency();
    public long getStartTime();
    public DataDimension getDataDimension();
}
