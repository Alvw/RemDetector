package data;

/**
 * Created by mac on 27/02/15.
 */
public class DataSetAdapter {

    public static DataSet wrap(int[] dataArray) {
        return new DataSetIntArrayWrapper(dataArray);
    }

    public static DataSet wrap(double[] dataArray) {
        return new DataSetDoubleArrayWrapper(dataArray);
    }

}
