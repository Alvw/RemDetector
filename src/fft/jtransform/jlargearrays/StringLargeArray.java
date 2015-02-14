/* ***** BEGIN LICENSE BLOCK *****
 * JLargeArrays
 * Copyright (C) 2013 onward University of Warsaw, ICM
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ***** END LICENSE BLOCK ***** */
package fft.jtransform.jlargearrays;

import sun.misc.Cleaner;

import java.io.UnsupportedEncodingException;

/**
 *
 * An array of strings that can store up to 2<SUP>63</SUP> elements.
 *
 * @author Piotr Wendykier (p.wendykier@icm.edu.pl)
 */
public class StringLargeArray extends LargeArray
{

    private static final long serialVersionUID = -4096759496772248522L;
    private String[] data;
    private ShortLargeArray stringLengths;
    private int maxStringLength;
    private long size;
    private byte[] byteArray;
    private static final String CHARSET = "UTF-8";
    private static final int CHARSET_SIZE = 4; //UTF-8 uses between 1 and 4 bytes to encode a single character 

    /**
     * Creates new instance of this class. The maximal string length is set to 100.
     *
     * @param length number of elements
     */
    public StringLargeArray(long length)
    {
        this(length, 100);
    }

    /**
     * Creates new instance of this class.
     *
     * @param length          number of elements
     * @param maxStringLength maximal length of the string, it is ignored when number of elements is smaller than LARGEST_32BIT_INDEX
     */
    public StringLargeArray(long length, int maxStringLength)
    {
        this(length, maxStringLength, true);
    }

    /**
     * Creates new instance of this class.
     *
     * @param length           number of elements
     * @param maxStringLength  maximal length of the string, it is ignored when number of elements is smaller than LARGEST_32BIT_INDEX
     * @param zeroNativeMemory if true, then the native memory is zeroed.
     */
    public StringLargeArray(long length, int maxStringLength, boolean zeroNativeMemory)
    {
        this.type = LargeArrayType.STRING;
        this.sizeof = 1;
        if (length <= 0) {
            throw new IllegalArgumentException(length + " is not a positive long value.");
        }
        if (maxStringLength <= 0) {
            throw new IllegalArgumentException(maxStringLength + " is not a positive int value.");
        }
        this.length = length;
        this.size = length * (long) maxStringLength * (long) CHARSET_SIZE;
        this.maxStringLength = maxStringLength;
        if (length > LARGEST_32BIT_INDEX) {
            System.gc();
            this.ptr = Utilities.UNSAFE.allocateMemory(this.size * this.sizeof);
            if (zeroNativeMemory) {
                zeroNativeMemory(this.size);
            }
            Cleaner.create(this, new Deallocator(this.ptr, this.size, this.sizeof));
            MemoryCounter.increaseCounter(this.size * this.sizeof);
            stringLengths = new ShortLargeArray(length);
            byteArray = new byte[maxStringLength * CHARSET_SIZE];
        } else {
            data = new String[(int) length];
        }
    }

    /**
     * Creates a constant array.
     * <p>
     * @param length        number of elements
     * @param constantValue value
     */
    public StringLargeArray(long length, String constantValue)
    {
        this.type = LargeArrayType.STRING;
        this.sizeof = 1;
        if (length <= 0) {
            throw new IllegalArgumentException(length + " is not a positive long value");
        }
        this.length = length;
        this.isConstant = true;
        this.data = new String[]{constantValue};
    }

    /**
     * Creates new instance of this class.
     *
     * @param data data array, this reference is used internally.
     */
    public StringLargeArray(String[] data)
    {
        this.type = LargeArrayType.STRING;
        this.sizeof = 1;
        this.length = data.length;
        this.data = data;
    }

    /**
     * Returns a deep copy of this instance. (The elements themselves are copied.)
     *
     * @return a clone of this instance
     */
    @Override
    public StringLargeArray clone()
    {
        if (isConstant()) {
            return new StringLargeArray(length, get(0));
        } else {
            StringLargeArray v = new StringLargeArray(length, Math.max(1, maxStringLength), false);
            Utilities.arraycopy(this, 0, v, 0, length);
            return v;
        }
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (super.equals(o)) {
            StringLargeArray la = (StringLargeArray) o;
            return this.maxStringLength == la.maxStringLength && this.data == la.data && this.stringLengths.equals(la.stringLengths);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = 29 * super.hashCode() + (this.data != null ? this.data.hashCode() : 0);
        hash = 29 * hash + (int) (this.maxStringLength ^ (this.maxStringLength >>> 16));
        return 29 * hash + (this.stringLengths != null ? this.stringLengths.hashCode() : 0);
    }

    @Override
    public String get(long i)
    {
        if (ptr != 0) {
            short strLen = stringLengths.getShort(i);
            long offset = sizeof * i * maxStringLength * CHARSET_SIZE;
            for (int j = 0; j < strLen; j++) {
                byteArray[j] = Utilities.UNSAFE.getByte(ptr + offset + sizeof * j);
            }
            try {
                return new String(byteArray, 0, strLen, CHARSET);
            } catch (UnsupportedEncodingException ex) {
                return null;
            }
        } else {
            if (isConstant()) {
                return data[0];
            } else {
                return data[(int) i];
            }
        }
    }

    @Override
    public String getFromNative(long i)
    {
        short strLen = stringLengths.getShort(i);
        long offset = sizeof * i * maxStringLength * CHARSET_SIZE;
        for (int j = 0; j < strLen; j++) {
            byteArray[j] = Utilities.UNSAFE.getByte(ptr + offset + sizeof * j);
        }
        try {
            return new String(byteArray, 0, strLen, CHARSET);
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }

    @Override
    public boolean getBoolean(long i)
    {
        String s = get(i);
        return s.length() != 0;
    }

    @Override
    public byte getByte(long i)
    {
        String s = get(i);
        return (byte) s.length();
    }

    @Override
    public short getShort(long i)
    {
        String s = get(i);
        return (short) s.length();
    }

    @Override
    public int getInt(long i)
    {
        String s = get(i);
        return (int) s.length();
    }

    @Override
    public long getLong(long i)
    {
        String s = get(i);
        return (long) s.length();
    }

    @Override
    public float getFloat(long i)
    {
        String s = get(i);
        return (float) s.length();

    }

    @Override
    public double getDouble(long i)
    {
        String s = get(i);
        return (double) s.length();
    }

    @Override
    public String[] getData()
    {
        if (ptr != 0) {
            return null;
        } else {
            if (isConstant()) {
                if (length > getMaxSizeOf32bitArray()) return null;
                String[] out = new String[(int) length];
                for (int i = 0; i < length; i++) {
                    out[i] = data[0];
                }
                return out;
            } else {
                return data;
            }
        }
    }

    @Override
    public boolean[] getBooleanData()
    {
        if (ptr != 0) {
            return null;
        } else {
            if (isConstant()) {
                if (length > getMaxSizeOf32bitArray()) return null;
                boolean[] out = new boolean[(int) length];
                for (int i = 0; i < length; i++) {
                    out[i] = data[0].length() != 0;
                }
                return out;
            } else {
                boolean[] out = new boolean[(int) length];
                for (int i = 0; i < length; i++) {
                    out[i] = data[i].length() != 0;
                }
                return out;
            }
        }
    }

    @Override
    public boolean[] getBooleanData(boolean[] a, long startPos, long endPos, long step)
    {
        if (startPos < 0 || startPos >= length) {
            throw new ArrayIndexOutOfBoundsException("startPos < 0 || startPos >= length");
        }
        if (endPos < 0 || endPos > length || endPos < startPos) {
            throw new ArrayIndexOutOfBoundsException("endPos < 0 || endPos > length || endPos < startPos");
        }
        if (step < 1) {
            throw new IllegalArgumentException("step < 1");
        }

        long len = (long) Math.ceil((endPos - startPos) / (double) step);
        if (len > getMaxSizeOf32bitArray()) {
            return null;
        } else {
            boolean[] out;
            if (a != null && a.length >= len) {
                out = a;
            } else {
                out = new boolean[(int) len];
            }
            int idx = 0;
            if (ptr != 0) {
                for (long i = startPos; i < endPos; i += step) {
                    byte v = Utilities.UNSAFE.getByte(ptr + i);
                    out[idx++] = v == 1;
                }
            } else {
                if (isConstant()) {
                    boolean elem = data[0].length() != 0;
                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = elem;
                    }
                } else {
                    for (long i = startPos; i < endPos; i += step) {
                        int v = data[(int) i].length();
                        out[idx++] = v != 0;
                    }
                }
            }
            return out;
        }
    }

    @Override
    public byte[] getByteData()
    {
        if (ptr != 0) {
            return null;
        } else {
            if (isConstant()) {
                if (length > getMaxSizeOf32bitArray()) return null;
                byte[] out = new byte[(int) length];
                byte elem = (byte) data[0].length();
                for (int i = 0; i < length; i++) {
                    out[i] = elem;
                }
                return out;
            } else {
                byte[] res = new byte[(int) length];
                for (int i = 0; i < length; i++) {
                    res[i] = (byte) data[i].length();

                }
                return res;
            }
        }
    }

    @Override
    public byte[] getByteData(byte[] a, long startPos, long endPos, long step)
    {
        if (startPos < 0 || startPos >= length) {
            throw new ArrayIndexOutOfBoundsException("startPos < 0 || startPos >= length");
        }
        if (endPos < 0 || endPos > length || endPos < startPos) {
            throw new ArrayIndexOutOfBoundsException("endPos < 0 || endPos > length || endPos < startPos");
        }
        if (step < 1) {
            throw new IllegalArgumentException("step < 1");
        }

        long len = (long) Math.ceil((endPos - startPos) / (double) step);
        if (len > getMaxSizeOf32bitArray()) {
            return null;
        } else {
            byte[] out;
            if (a != null && a.length >= len) {
                out = a;
            } else {
                out = new byte[(int) len];
            }
            int idx = 0;
            if (ptr != 0) {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (byte) Utilities.UNSAFE.getFloat(ptr + sizeof * i);
                }
            } else {
                if (isConstant()) {
                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = (byte) data[0].length();
                    }
                } else {
                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = (byte) data[(int) i].length();
                    }
                }
            }
            return out;
        }
    }

    @Override
    public short[] getShortData()
    {
        if (ptr != 0) {
            return null;
        } else {
            if (isConstant()) {
                if (length > getMaxSizeOf32bitArray()) return null;
                short[] out = new short[(int) length];
                short elem = (short) data[0].length();
                for (int i = 0; i < length; i++) {
                    out[i] = elem;
                }
                return out;
            } else {
                short[] res = new short[(int) length];
                for (int i = 0; i < length; i++) {
                    res[i] = (short) data[i].length();

                }
                return res;
            }
        }
    }

    @Override
    public short[] getShortData(short[] a, long startPos, long endPos, long step)
    {
        if (startPos < 0 || startPos >= length) {
            throw new ArrayIndexOutOfBoundsException("startPos < 0 || startPos >= length");
        }
        if (endPos < 0 || endPos > length || endPos < startPos) {
            throw new ArrayIndexOutOfBoundsException("endPos < 0 || endPos > length || endPos < startPos");
        }
        if (step < 1) {
            throw new IllegalArgumentException("step < 1");
        }

        long len = (long) Math.ceil((endPos - startPos) / (double) step);
        if (len > getMaxSizeOf32bitArray()) {
            return null;
        } else {
            short[] out;
            if (a != null && a.length >= len) {
                out = a;
            } else {
                out = new short[(int) len];
            }
            int idx = 0;
            if (ptr != 0) {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (short) Utilities.UNSAFE.getFloat(ptr + sizeof * i);
                }
            } else {
                if (isConstant()) {
                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = (short) data[0].length();
                    }
                } else {
                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = (short) data[(int) i].length();
                    }
                }
            }
            return out;
        }
    }

    @Override
    public int[] getIntData()
    {
        if (ptr != 0) {
            return null;
        } else {
            if (isConstant()) {
                if (length > getMaxSizeOf32bitArray()) return null;
                int[] out = new int[(int) length];
                int elem = (int) data[0].length();
                for (int i = 0; i < length; i++) {
                    out[i] = elem;
                }
                return out;
            } else {
                int[] res = new int[(int) length];
                for (int i = 0; i < length; i++) {
                    res[i] = (int) data[i].length();

                }
                return res;
            }
        }
    }

    @Override
    public int[] getIntData(int[] a, long startPos, long endPos, long step)
    {
        if (startPos < 0 || startPos >= length) {
            throw new ArrayIndexOutOfBoundsException("startPos < 0 || startPos >= length");
        }
        if (endPos < 0 || endPos > length || endPos < startPos) {
            throw new ArrayIndexOutOfBoundsException("endPos < 0 || endPos > length || endPos < startPos");
        }
        if (step < 1) {
            throw new IllegalArgumentException("step < 1");
        }

        long len = (long) Math.ceil((endPos - startPos) / (double) step);
        if (len > getMaxSizeOf32bitArray()) {
            return null;
        } else {
            int[] out;
            if (a != null && a.length >= len) {
                out = a;
            } else {
                out = new int[(int) len];
            }
            int idx = 0;
            if (ptr != 0) {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (int) Utilities.UNSAFE.getFloat(ptr + sizeof * i);
                }
            } else {
                if (isConstant()) {
                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = (int) data[0].length();
                    }
                } else {

                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = (int) data[(int) i].length();
                    }
                }
            }
            return out;
        }
    }

    @Override
    public long[] getLongData()
    {
        if (ptr != 0) {
            return null;
        } else {
            if (isConstant()) {
                if (length > getMaxSizeOf32bitArray()) return null;
                long[] out = new long[(int) length];
                long elem = (long) data[0].length();
                for (int i = 0; i < length; i++) {
                    out[i] = elem;
                }
                return out;
            } else {
                long[] res = new long[(int) length];
                for (int i = 0; i < length; i++) {
                    res[i] = (long) data[i].length();

                }
                return res;
            }
        }
    }

    @Override
    public long[] getLongData(long[] a, long startPos, long endPos, long step)
    {
        if (startPos < 0 || startPos >= length) {
            throw new ArrayIndexOutOfBoundsException("startPos < 0 || startPos >= length");
        }
        if (endPos < 0 || endPos > length || endPos < startPos) {
            throw new ArrayIndexOutOfBoundsException("endPos < 0 || endPos > length || endPos < startPos");
        }
        if (step < 1) {
            throw new IllegalArgumentException("step < 1");
        }

        long len = (long) Math.ceil((endPos - startPos) / (double) step);
        if (len > getMaxSizeOf32bitArray()) {
            return null;
        } else {
            long[] out;
            if (a != null && a.length >= len) {
                out = a;
            } else {
                out = new long[(int) len];
            }
            int idx = 0;
            if (ptr != 0) {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (long) Utilities.UNSAFE.getFloat(ptr + sizeof * i);
                }
            } else {
                if (isConstant()) {
                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = (long) data[0].length();
                    }
                } else {

                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = (long) data[(int) i].length();
                    }
                }
            }
            return out;
        }
    }

    @Override
    public float[] getFloatData()
    {
        if (ptr != 0) {
            return null;
        } else {
            if (isConstant()) {
                if (length > getMaxSizeOf32bitArray()) return null;
                float[] out = new float[(int) length];
                for (int i = 0; i < length; i++) {
                    out[i] = data[0].length();
                }
                return out;
            } else {
                float[] out = new float[(int) length];
                for (int i = 0; i < length; i++) {
                    out[i] = (float) data[i].length();

                }
                return out;
            }
        }
    }

    @Override
    public float[] getFloatData(float[] a, long startPos, long endPos, long step)
    {
        if (startPos < 0 || startPos >= length) {
            throw new ArrayIndexOutOfBoundsException("startPos < 0 || startPos >= length");
        }
        if (endPos < 0 || endPos > length || endPos < startPos) {
            throw new ArrayIndexOutOfBoundsException("endPos < 0 || endPos > length || endPos < startPos");
        }
        if (step < 1) {
            throw new IllegalArgumentException("step < 1");
        }

        long len = (long) Math.ceil((endPos - startPos) / (double) step);
        if (len > getMaxSizeOf32bitArray()) {
            return null;
        } else {
            float[] out;
            if (a != null && a.length >= len) {
                out = a;
            } else {
                out = new float[(int) len];
            }
            int idx = 0;
            if (ptr != 0) {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (float) Utilities.UNSAFE.getByte(ptr + i);
                }
            } else {
                if (isConstant()) {
                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = data[0].length();
                    }
                } else {
                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = (float) data[(int) i].length();
                    }
                }
            }
            return out;
        }
    }

    @Override
    public double[] getDoubleData()
    {
        if (ptr != 0) {
            return null;
        } else {
            if (isConstant()) {
                if (length > getMaxSizeOf32bitArray()) return null;
                double[] out = new double[(int) length];
                for (int i = 0; i < length; i++) {
                    out[i] = data[0].length();
                }
                return out;
            } else {
                double[] out = new double[(int) length];
                for (int i = 0; i < length; i++) {
                    out[i] = (double) data[i].length();

                }
                return out;
            }
        }
    }

    @Override
    public double[] getDoubleData(double[] a, long startPos, long endPos, long step)
    {
        if (startPos < 0 || startPos >= length) {
            throw new ArrayIndexOutOfBoundsException("startPos < 0 || startPos >= length");
        }
        if (endPos < 0 || endPos > length || endPos < startPos) {
            throw new ArrayIndexOutOfBoundsException("endPos < 0 || endPos > length || endPos < startPos");
        }
        if (step < 1) {
            throw new IllegalArgumentException("step < 1");
        }

        long len = (long) Math.ceil((endPos - startPos) / (double) step);
        if (len > getMaxSizeOf32bitArray()) {
            return null;
        } else {
            double[] out;
            if (a != null && a.length >= len) {
                out = a;
            } else {
                out = new double[(int) len];
            }
            int idx = 0;
            if (ptr != 0) {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (double) Utilities.UNSAFE.getByte(ptr + i);
                }
            } else {
                if (isConstant()) {
                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = data[0].length();
                    }
                } else {
                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = (double) data[(int) i].length();
                    }
                }
            }
            return out;
        }
    }

    @Override
    public void setToNative(long i, Object value)
    {
        String s = (String) value;
        if (s.length() > maxStringLength) {
            throw new IllegalArgumentException("String  " + s + " is too long.");
        }
        byte[] tmp;
        try {
            tmp = s.getBytes(CHARSET);
        } catch (UnsupportedEncodingException ex) {
            return;
        }
        int strLen = tmp.length;
        if (strLen > Short.MAX_VALUE) {
            throw new IllegalArgumentException("String  " + s + " is too long.");
        }
        stringLengths.setShort(i, (short) strLen);
        long offset = sizeof * i * maxStringLength * CHARSET_SIZE;
        for (int j = 0; j < strLen; j++) {
            Utilities.UNSAFE.putByte(ptr + offset + sizeof * j, tmp[j]);
        }
    }

    @Override
    public void set(long i, Object o)
    {
        if (!(o instanceof String)) {
            throw new IllegalArgumentException(o + " is not a string.");
        }
        String s = (String) o;
        if (ptr != 0) {
            if (s.length() > maxStringLength) {
                throw new IllegalArgumentException("String  " + s + " is too long.");
            }
            byte[] tmp;
            try {
                tmp = s.getBytes(CHARSET);
            } catch (UnsupportedEncodingException ex) {
                return;
            }
            int strLen = tmp.length;
            if (strLen > Short.MAX_VALUE) {
                throw new IllegalArgumentException("String  " + s + " is too long.");
            }
            stringLengths.setShort(i, (short) strLen);
            long offset = sizeof * i * maxStringLength * CHARSET_SIZE;
            for (int j = 0; j < strLen; j++) {
                Utilities.UNSAFE.putByte(ptr + offset + sizeof * j, tmp[j]);
            }
        } else {
            if (isConstant()) {
                throw new IllegalAccessError("Constant arrays cannot be modified.");
            }
            data[(int) i] = s;
        }
    }

    @Override
    public void set_safe(long i, Object o)
    {
        if (i < 0 || i >= length) {
            throw new ArrayIndexOutOfBoundsException(Long.toString(i));
        }
        if (!(o instanceof String)) {
            throw new IllegalArgumentException(o + " is not a string.");
        }
        String s = (String) o;
        if (ptr != 0) {
            if (s.length() > maxStringLength) {
                throw new IllegalArgumentException("String  " + s + " is too long.");
            }
            byte[] tmp;
            try {
                tmp = s.getBytes(CHARSET);
            } catch (UnsupportedEncodingException ex) {
                return;
            }
            int strLen = tmp.length;
            if (strLen > Short.MAX_VALUE) {
                throw new IllegalArgumentException("String  " + s + " is too long.");
            }
            stringLengths.setShort(i, (short) strLen);
            long offset = sizeof * i * maxStringLength * CHARSET_SIZE;
            for (int j = 0; j < strLen; j++) {
                Utilities.UNSAFE.putByte(ptr + offset + sizeof * j, tmp[j]);
            }
        } else {
            if (isConstant()) {
                throw new IllegalAccessError("Constant arrays cannot be modified.");
            }
            data[(int) i] = s;
        }
    }

    @Override
    public void setBoolean(long i, boolean value)
    {
        set(i, Boolean.toString(value));
    }

    @Override
    public void setByte(long i, byte value)
    {
        set(i, Byte.toString(value));
    }

    @Override
    public void setShort(long i, short value)
    {
        set(i, Short.toString(value));
    }

    @Override
    public void setInt(long i, int value)
    {
        set(i, Integer.toString(value));
    }

    @Override
    public void setLong(long i, long value)
    {
        set(i, Long.toString(value));
    }

    @Override
    public void setFloat(long i, float value)
    {
        set(i, Float.toString(value));
    }

    @Override
    public void setDouble(long i, double value)
    {
        set(i, Double.toString(value));
    }

    /**
     * Returns maximal length of each element.
     * <p>
     * @return maximal length of each element
     *
     */
    public int getMaxStringLength()
    {
        return maxStringLength;
    }

}
