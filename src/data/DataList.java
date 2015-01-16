package data;


import gnu.trove.list.array.TIntArrayList;
import prefilters.AbstractPreFilter;

import java.util.ArrayList;


/**

 */
public class DataList extends AbstractPreFilter implements DataSet  {
    private TIntArrayList intArrayList;
    private ArrayList<Integer> arrayList;
    private double frequency = 0;
    private int digitalMin = Integer.MIN_VALUE;
    private int digitalMax = Integer.MAX_VALUE;
    private double physicalMin = Integer.MIN_VALUE;
    private double physicalMax = Integer.MAX_VALUE;
    private String physicalDimension = "";

    private DataList(TIntArrayList intArrayList) {
        this.intArrayList = intArrayList;
    }
    private DataList(int[] array) {
        intArrayList = TIntArrayList.wrap(array);
    }
    private DataList(ArrayList<Integer> arrayList) {
        this.arrayList = arrayList;
    }


    public DataList() {
        intArrayList = new TIntArrayList();
    }

    public static DataSet wrap(TIntArrayList intArrayList) {
        return new DataList(intArrayList);
    }

    public static DataSet wrap(ArrayList<Integer> arrayList) {
        return new DataList(arrayList);
    }

    public static DataSet wrap(int[] array) {
        return new DataList(array);
    }

    public void add(int value) {
        if(intArrayList != null) {
            intArrayList.add(value);
        }
        else {
            arrayList.add(value);
        }
        notifyListeners(value);
    }

    public void set(int index, int value) {
        if(intArrayList != null) {
            intArrayList.set(index, value);
        }
        else {
            arrayList.set(index, value);
        }
    }


    public void clear() {
        if(intArrayList != null) {
            intArrayList.clear();
        }
        else{
            arrayList.clear();
        }

    }


    @Override
    public int getDigitalMin() {
        return digitalMin;
    }

    @Override
    public int getDigitalMax() {
        return digitalMax;
    }

    @Override
    public double getPhysicalMin() {
        return physicalMin;
    }

    @Override
    public double getPhysicalMax() {
        return physicalMax;
    }


    @Override
    public String getPhysicalDimension() {
        return physicalDimension;
    }

    public void setDigitalMin(int digitalMin) {
        this.digitalMin = digitalMin;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
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

    @Override
    public int size() {
        if(intArrayList != null) {
            return intArrayList.size();
        }
        return arrayList.size();
    }

    @Override
    public int get(int index) {
        if(intArrayList != null) {
            return intArrayList.get(index);
        }
        return arrayList.get(index);
    }

    @Override
    public double getFrequency() {
        return frequency;
    }

}
