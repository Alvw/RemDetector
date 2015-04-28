package data;


import gnu.trove.list.array.TIntArrayList;


/**

 */
public class DataList  implements DataSeries {
    private TIntArrayList intArrayList;
    private Scaling scaling;


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

    public void setScaling(Scaling scaling) {
        this.scaling = scaling;
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
    public Scaling getScaling() {
        return scaling;
    }
}
