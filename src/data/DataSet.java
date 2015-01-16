package data;

/**
 * Created with IntelliJ IDEA.
 * User: GENA
 * Date: 16.05.14
 * Time: 3:24
 * To change this template use File | Settings | File Templates.
 */

public interface DataSet {

    public static final int UNDEFINED = Integer.MIN_VALUE;
    public static final int REM = Integer.MAX_VALUE - 400;
    public static final int SLOW = Integer.MAX_VALUE - 300;
    public static final int STAND = Integer.MAX_VALUE - 100;
    public static final int MOVE = Integer.MAX_VALUE - 200;
    public static final int WORKSPACE = 16777216; // 3 bytes
    
    public int size();
    public int get(int index);
    public double getFrequency();
    public DataDimension getDataDimension();
}
