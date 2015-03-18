package filters;

import data.DataSet;
import functions.Function;

/**
 * Created with IntelliJ IDEA.
 * User: GENA
 * Date: 24.07.14
 * Time: 0:12
 * To change this template use File | Settings | File Templates.
 */
public class Compressor extends Function {
    protected int compression;
    public Compressor(DataSet inputData, int compression) {
        super(inputData);
        this.compression = compression;
    }

    @Override
    public int get(int index) {
        if (index == 0) return 0;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < compression; i++) {
            max = Math.max(max, inputData.get(index * compression + i));
        }
        return max;
    }

    @Override
    public double getFrequency() {
        return inputData.getFrequency() / compression;
    }

    @Override
    public int size() {
        return inputData.size()/compression;
    }
}