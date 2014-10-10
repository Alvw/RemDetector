package com.dream.Filters;

import com.dream.Data.DataStream;

/**
 * Created with IntelliJ IDEA.
 * User: GENA
 * Date: 24.07.14
 * Time: 0:12
 * To change this template use File | Settings | File Templates.
 */
public abstract class Compressor extends FilterBuffered {
    protected int compression;

    public Compressor(DataStream inputData, int compression) {
        super(inputData);
        this.compression = compression;
    }

    @Override
    public int size() {
        return inputData.size()/compression;
    }
}
