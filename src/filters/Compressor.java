package filters;

import data.DataSet;

/**
 * Created with IntelliJ IDEA.
 * User: GENA
 * Date: 24.07.14
 * Time: 0:12
 * To change this template use File | Settings | File Templates.
 */
public abstract class Compressor extends FilterBuffered {
    protected int compression;

    public Compressor(DataSet inputData, int compression) {
        super(inputData);
        this.compression = compression;
    }

    @Override
    public int size() {
        return inputData.size()/compression;
    }

    @Override
    public double getFrequency() {
        return super.getFrequency()/compression;
    }
}
