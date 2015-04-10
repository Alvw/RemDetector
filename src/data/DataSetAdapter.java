package data;

/**
 * Created by mac on 27/02/15.
 */
public class DataSetAdapter {

    public static DataSeries wrap(int[] dataArray) {
        return new DataSeriesIntArrayAdapter(dataArray);
    }

    public static DataSeries wrap(double[] dataArray) {
        return new DataSeriesDoubleArrayAdapter(dataArray);
    }

}
