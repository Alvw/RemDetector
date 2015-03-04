package data;


import fft.prinston.Complex;
import gnu.trove.list.array.TIntArrayList;
import prefilters.AbstractPreFilter;

import java.util.ArrayList;


/**

 */
public class DataList  implements DataSet {
    private TIntArrayList intArrayList;
    private double frequency = 0;
    private long startTime = 0;
    private DataDimension dataDimension = new DataDimension();


    public DataList() {
        intArrayList = new TIntArrayList();
    }
    public DataList(int[] array) {
        intArrayList = TIntArrayList.wrap(array);
    }

    public void add(int value) {
        intArrayList.add(value);
    }

    public void set(int index, int value) {
        intArrayList.set(index, value);
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public void setDataDimension(DataDimension dataDimension) {
        this.dataDimension = dataDimension;
    }


    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public DataDimension getDataDimension() {
        return dataDimension;
    }

    @Override
    public int size() {
        return intArrayList.size();
    }

    @Override
    public int get(int index) {
        return intArrayList.get(index);
    }

    @Override
    public double getFrequency() {
        return frequency;
    }
}
