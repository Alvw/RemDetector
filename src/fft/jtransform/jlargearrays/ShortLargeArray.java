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

/**
 *
 * An array of shorts that can store up to 2<SUP>63</SUP> elements.
 *
 * @author Piotr Wendykier (p.wendykier@icm.edu.pl)
 */
public class ShortLargeArray extends LargeArray
{

    private static final long serialVersionUID = 8813991144303908703L;
    private short[] data;

    /**
     * Creates new instance of this class.
     *
     * @param length number of elements
     */
    public ShortLargeArray(long length)
    {
        this(length, true);
    }

    /**
     * Creates new instance of this class.
     *
     * @param length           number of elements
     * @param zeroNativeMemory if true, then the native memory is zeroed.
     */
    public ShortLargeArray(long length, boolean zeroNativeMemory)
    {
        this.type = LargeArrayType.SHORT;
        this.sizeof = 2;
        if (length <= 0) {
            throw new IllegalArgumentException(length + " is not a positive long value");
        }
        this.length = length;
        if (length > LARGEST_32BIT_INDEX) {
            System.gc();
            this.ptr = Utilities.UNSAFE.allocateMemory(this.length * this.sizeof);
            if (zeroNativeMemory) {
                zeroNativeMemory(length);
            }
            Cleaner.create(this, new Deallocator(this.ptr, this.length, this.sizeof));
            MemoryCounter.increaseCounter(this.length * this.sizeof);
        } else {
            data = new short[(int) length];
        }
    }

    /**
     * Creates a constant array.
     * <p>
     * @param length        number of elements
     * @param constantValue value
     */
    public ShortLargeArray(long length, short constantValue)
    {
        this.type = LargeArrayType.DOUBLE;
        this.sizeof = 8;
        if (length <= 0) {
            throw new IllegalArgumentException(length + " is not a positive long value");
        }
        this.length = length;
        this.isConstant = true;
        this.data = new short[]{constantValue};
    }

    /**
     * Creates new instance of this class.
     *
     * @param data data array, this reference is used internally.
     */
    public ShortLargeArray(short[] data)
    {
        this.type = LargeArrayType.SHORT;
        this.sizeof = 2;
        this.length = data.length;
        this.data = data;
    }

    /**
     * Returns a deep copy of this instance. (The elements themselves are copied.)
     *
     * @return a clone of this instance
     */
    @Override
    public ShortLargeArray clone()
    {
        if (isConstant()) {
            return new ShortLargeArray(length, getShort(0));
        } else {
            ShortLargeArray v = new ShortLargeArray(length, false);
            Utilities.arraycopy(this, 0, v, 0, length);
            return v;
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (super.equals(o)) {
            ShortLargeArray la = (ShortLargeArray) o;
            return this.data == la.data;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return 29 * super.hashCode() + (this.data != null ? this.data.hashCode() : 0);
    }

    @Override
    public Short get(long i)
    {
        return getShort(i);
    }

    @Override
    public Short getFromNative(long i)
    {
        return Utilities.UNSAFE.getShort(ptr + sizeof * i);
    }

    @Override
    public boolean getBoolean(long i)
    {
        if (ptr != 0) {
            return (Utilities.UNSAFE.getShort(ptr + sizeof * i)) != 0;
        } else {
            if (isConstant()) {
                return data[0] != 0;
            } else {
                return data[(int) i] != 0;
            }
        }
    }

    @Override
    public byte getByte(long i)
    {
        if (ptr != 0) {
            return (byte) (Utilities.UNSAFE.getShort(ptr + sizeof * i));
        } else {
            if (isConstant()) {
                return (byte) data[0];
            } else {
                return (byte) data[(int) i];
            }
        }
    }

    @Override
    public short getShort(long i)
    {
        if (ptr != 0) {
            return Utilities.UNSAFE.getShort(ptr + sizeof * i);
        } else {
            if (isConstant()) {
                return data[0];
            } else {
                return data[(int) i];
            }
        }
    }

    @Override
    public int getInt(long i)
    {
        if (ptr != 0) {
            return (int) (Utilities.UNSAFE.getShort(ptr + sizeof * i));
        } else {
            if (isConstant()) {
                return (int) data[0];
            } else {
                return (int) data[(int) i];
            }
        }
    }

    @Override
    public long getLong(long i)
    {
        if (ptr != 0) {
            return (long) (Utilities.UNSAFE.getShort(ptr + sizeof * i));
        } else {
            if (isConstant()) {
                return (long) data[0];
            } else {
                return (long) data[(int) i];
            }
        }
    }

    @Override
    public float getFloat(long i)
    {
        if (ptr != 0) {
            return (float) (Utilities.UNSAFE.getShort(ptr + sizeof * i));
        } else {
            if (isConstant()) {
                return (float) data[0];
            } else {
                return (float) data[(int) i];
            }
        }
    }

    @Override
    public double getDouble(long i)
    {
        if (ptr != 0) {
            return (double) (Utilities.UNSAFE.getShort(ptr + sizeof * i));
        } else {
            if (isConstant()) {
                return (double) data[0];
            } else {
                return (double) data[(int) i];
            }
        }
    }

    @Override
    public short[] getData()
    {
        if (ptr != 0) {
            return null;
        } else {
            if (isConstant()) {
                if (length > getMaxSizeOf32bitArray()) return null;
                short[] out = new short[(int) length];
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
                boolean elem = data[0] != 0;
                for (int i = 0; i < length; i++) {
                    out[i] = elem;
                }
                return out;
            } else {
                boolean[] out = new boolean[(int) length];
                for (int i = 0; i < length; i++) {
                    out[i] = data[i] != 0;

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
                    short v = Utilities.UNSAFE.getShort(ptr + sizeof * i);
                    out[idx++] = v != 0;
                }
            } else {
                if (isConstant()) {
                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = data[0] != 0;
                    }
                } else {
                    for (long i = startPos; i < endPos; i += step) {
                        short v = data[(int) i];
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
                byte elem = (byte) data[0];
                for (int i = 0; i < length; i++) {
                    out[i] = elem;
                }
                return out;
            } else {
                byte[] out = new byte[(int) length];
                for (int i = 0; i < length; i++) {
                    out[i] = (byte) data[i];

                }
                return out;
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
                    out[idx++] = (byte) Utilities.UNSAFE.getShort(ptr + sizeof * i);
                }
            } else {
                if (isConstant()) {
                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = (byte) data[0];
                    }
                } else {
                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = (byte) data[(int) i];
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
                short elem = (short) data[0];
                for (int i = 0; i < length; i++) {
                    out[i] = elem;
                }
                return out;
            } else {
                return data.clone();
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
                    out[idx++] = Utilities.UNSAFE.getShort(ptr + sizeof * i);
                }
            } else {
                if (isConstant()) {
                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = data[0];
                    }
                } else {
                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = data[(int) i];
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
                int elem = (int) data[0];
                for (int i = 0; i < length; i++) {
                    out[i] = elem;
                }
                return out;
            } else {
                int[] out = new int[(int) length];
                for (int i = 0; i < length; i++) {
                    out[i] = (int) data[i];

                }
                return out;
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
                    out[idx++] = (int) Utilities.UNSAFE.getShort(ptr + sizeof * i);
                }
            } else {
                if (isConstant()) {
                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = (int) data[0];
                    }
                } else {
                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = (int) data[(int) i];
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
                long elem = (long) data[0];
                for (int i = 0; i < length; i++) {
                    out[i] = elem;
                }
                return out;
            } else {
                long[] out = new long[(int) length];
                for (int i = 0; i < length; i++) {
                    out[i] = (long) data[i];

                }
                return out;
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
                    out[idx++] = (long) Utilities.UNSAFE.getShort(ptr + sizeof * i);
                }
            } else {
                if (isConstant()) {
                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = (long) data[0];
                    }
                } else {
                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = (long) data[(int) i];
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
                float elem = (float) data[0];
                for (int i = 0; i < length; i++) {
                    out[i] = elem;
                }
                return out;
            } else {
                float[] out = new float[(int) length];
                for (int i = 0; i < length; i++) {
                    out[i] = (float) data[i];

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
                    out[idx++] = (float) Utilities.UNSAFE.getShort(ptr + sizeof * i);
                }
            } else {
                if (isConstant()) {
                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = (float) data[0];
                    }
                } else {
                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = (float) data[(int) i];
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
                double elem = (double) data[0];
                for (int i = 0; i < length; i++) {
                    out[i] = elem;
                }
                return out;
            } else {
                double[] out = new double[(int) length];
                for (int i = 0; i < length; i++) {
                    out[i] = (double) data[i];

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
                    out[idx++] = (double) Utilities.UNSAFE.getShort(ptr + sizeof * i);
                }
            } else {
                if (isConstant()) {
                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = (double) data[0];
                    }
                } else {
                    for (long i = startPos; i < endPos; i += step) {
                        out[idx++] = (double) data[(int) i];
                    }
                }
            }
            return out;
        }
    }

    @Override
    public void setToNative(long i, Object value)
    {
        Utilities.UNSAFE.putShort(ptr + sizeof * i, (Short) value);
    }

    @Override
    public void setBoolean(long i, boolean value)
    {
        if (ptr != 0) {
            Utilities.UNSAFE.putShort(ptr + sizeof * i, value == true ? (short) 1 : (short) 0);
        } else {
            if (isConstant()) {
                throw new IllegalAccessError("Constant arrays cannot be modified.");
            }
            data[(int) i] = value == true ? (short) 1 : (short) 0;
        }
    }

    @Override
    public void setByte(long i, byte value)
    {
        if (ptr != 0) {
            Utilities.UNSAFE.putShort(ptr + sizeof * i, (short) value);
        } else {
            if (isConstant()) {
                throw new IllegalAccessError("Constant arrays cannot be modified.");
            }
            data[(int) i] = (short) value;
        }
    }

    @Override
    public void setShort(long i, short value)
    {
        if (ptr != 0) {
            Utilities.UNSAFE.putShort(ptr + sizeof * i, value);
        } else {
            if (isConstant()) {
                throw new IllegalAccessError("Constant arrays cannot be modified.");
            }
            data[(int) i] = value;
        }
    }

    @Override
    public void setInt(long i, int value)
    {
        if (ptr != 0) {
            Utilities.UNSAFE.putShort(ptr + sizeof * i, (short) value);
        } else {
            if (isConstant()) {
                throw new IllegalAccessError("Constant arrays cannot be modified.");
            }
            data[(int) i] = (short) value;
        }
    }

    @Override
    public void setLong(long i, long value)
    {
        if (ptr != 0) {
            Utilities.UNSAFE.putShort(ptr + sizeof * i, (short) value);
        } else {
            if (isConstant()) {
                throw new IllegalAccessError("Constant arrays cannot be modified.");
            }
            data[(int) i] = (short) value;
        }
    }

    @Override
    public void setFloat(long i, float value)
    {
        if (ptr != 0) {
            Utilities.UNSAFE.putShort(ptr + sizeof * i, (short) value);
        } else {
            if (isConstant()) {
                throw new IllegalAccessError("Constant arrays cannot be modified.");
            }
            data[(int) i] = (short) value;
        }
    }

    @Override
    public void setDouble(long i, double value)
    {
        if (ptr != 0) {
            Utilities.UNSAFE.putShort(ptr + sizeof * i, (short) value);
        } else {
            if (isConstant()) {
                throw new IllegalAccessError("Constant arrays cannot be modified.");
            }
            data[(int) i] = (short) value;
        }
    }
}
