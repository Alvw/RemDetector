/*
 * Copyright (c) 2014, piotrw
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package fft.jtransform.jlargearrays;

/* VisNow
 Copyright (C) 2006-2013 University of Warsaw, ICM

 This file is part of GNU Classpath.

 GNU Classpath is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2, or (at your option)
 any later version.

 GNU Classpath is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with GNU Classpath; see the file COPYING.  If not, write to the 
 University of Warsaw, Interdisciplinary Centre for Mathematical and 
 Computational Modelling, Pawinskiego 5a, 02-106 Warsaw, Poland. 

 Linking this library statically or dynamically with other modules is
 making a combined work based on this library.  Thus, the terms and
 conditions of the GNU General Public License cover the whole
 combination.

 As a special exception, the copyright holders of this library give you
 permission to link this library with independent modules to produce an
 executable, regardless of the license terms of these independent
 modules, and to copy and distribute the resulting executable under
 terms of your choice, provided that you also meet, for each linked
 independent module, the terms and conditions of the license of that
 module.  An independent module is a module which is not derived from
 or based on this library.  If you modify this library, you may extend
 this exception to your version of the library, but you are not
 obligated to do so.  If you do not wish to do so, delete this
 exception statement from your version. */
/**
 *
 * An array of complex numbers (double precision) that can store up to 2<SUP>63</SUP> elements.
 *
 * @author Piotr Wendykier (p.wendykier@icm.edu.pl)
 */
public class ComplexDoubleLargeArray extends LargeArray
{

    private static final long serialVersionUID = 155390537810310407L;

    private DoubleLargeArray dataRe;
    private DoubleLargeArray dataIm;

    /**
     * Creates new instance of this class.
     *
     * @param length number of elements
     */
    public ComplexDoubleLargeArray(long length)
    {
        this(length, true);
    }

    /**
     * Creates new instance of this class.
     *
     * @param length           number of elements
     * @param zeroNativeMemory if true, then the native memory is zeroed.
     */
    public ComplexDoubleLargeArray(long length, boolean zeroNativeMemory)
    {
        this.type = LargeArrayType.COMPLEX_DOUBLE;
        this.sizeof = 8;
        if (length <= 0) {
            throw new IllegalArgumentException(length + " is not a positive long value");
        }
        this.length = length;
        dataRe = new DoubleLargeArray(length, zeroNativeMemory);
        dataIm = new DoubleLargeArray(length, zeroNativeMemory);
    }

    /**
     * Creates a constant array.
     * <p>
     * @param length        number of elements
     * @param constantValue value
     */
    public ComplexDoubleLargeArray(long length, double[] constantValue)
    {
        this.type = LargeArrayType.COMPLEX_DOUBLE;
        this.sizeof = 8;
        if (length <= 0) {
            throw new IllegalArgumentException(length + " is not a positive long value");
        }
        if (constantValue == null || constantValue.length != 2) {
            throw new IllegalArgumentException("constantValue == null || constantValue.length != 2");
        }
        this.length = length;
        this.isConstant = true;
        this.dataRe = new DoubleLargeArray(length, constantValue[0]);
        this.dataIm = new DoubleLargeArray(length, constantValue[1]);
    }

    /**
     * Creates new instance of this class.
     *
     * @param data data array, this reference is not used internally.
     */
    public ComplexDoubleLargeArray(double[] data)
    {
        this(new DoubleLargeArray(data));
    }

    /**
     * Creates new instance of this class.
     *
     * @param data data array, this reference is not used internally.
     */
    public ComplexDoubleLargeArray(DoubleLargeArray data)
    {
        if (data.length() % 2 != 0) {
            throw new IllegalArgumentException("The length of the data array must be even.");
        }
        if (data.length() <= 0) {
            throw new IllegalArgumentException(data.length() + " is not a positive long value");
        }
        this.type = LargeArrayType.COMPLEX_DOUBLE;
        this.sizeof = 8;
        this.length = data.length / 2;
        this.isConstant = data.isConstant;
        if (this.isConstant) {
            this.dataRe = new DoubleLargeArray(length, data.getDouble(0));
            this.dataIm = new DoubleLargeArray(length, data.getDouble(1));
        } else {
            dataRe = new DoubleLargeArray(length, false);
            dataIm = new DoubleLargeArray(length, false);
            for (long i = 0; i < this.length; i++) {
                dataRe.setDouble(i, data.getDouble(2 * i));
                dataIm.setDouble(i, data.getDouble(2 * i + 1));
            }
        }
    }

    /**
     * Creates new instance of this class.
     * <p>
     * @param dataRe real part, this reference is used internally.
     * @param dataIm imaginary part, this reference not used internally.
     */
    public ComplexDoubleLargeArray(double[] dataRe, double[] dataIm)
    {
        this(new DoubleLargeArray(dataRe), new DoubleLargeArray(dataIm));
    }

    /**
     * Creates new instance of this class.
     * <p>
     * @param dataRe real part, this reference is used internally.
     * @param dataIm imaginary part, this reference is used internally.
     */
    public ComplexDoubleLargeArray(DoubleLargeArray dataRe, DoubleLargeArray dataIm)
    {
        if (dataRe.length() != dataIm.length()) {
            throw new IllegalArgumentException("The length of the dataRe must be equal to the length of dataIm.");
        }
        if (dataRe.length() <= 0) {
            throw new IllegalArgumentException(dataRe.length() + " is not a positive long value");
        }
        this.type = LargeArrayType.COMPLEX_DOUBLE;
        this.sizeof = 8;
        this.length = dataRe.length();
        this.dataRe = dataRe;
        this.dataIm = dataIm;
    }

    /**
     * Returns a deep copy of this instance. (The elements themselves are copied.)
     *
     * @return a clone of this instance
     */
    @Override
    public ComplexDoubleLargeArray clone()
    {
        if (isConstant()) {
            return new ComplexDoubleLargeArray(length, new double[]{dataRe.getDouble(0), dataIm.getDouble(0)});
        } else {
            ComplexDoubleLargeArray v = new ComplexDoubleLargeArray(length, false);
            Utilities.arraycopy(this, 0, v, 0, length);
            return v;
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (super.equals(o)) {
            ComplexDoubleLargeArray la = (ComplexDoubleLargeArray) o;
            return this.dataRe.equals(la.dataRe) && this.dataIm.equals(la.dataIm);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = 29 * super.hashCode() + (this.dataRe != null ? this.dataRe.hashCode() : 0);
        return 29 * hash + (this.dataIm != null ? this.dataIm.hashCode() : 0);
    }

    /**
     * Returns the real part of this array.
     * <p>
     * @return reference to the real part.
     */
    public DoubleLargeArray getRealArray()
    {
        return dataRe;
    }

    /**
     * Returns the imaginary part of this array.
     * <p>
     * @return reference to the imaginary part.
     */
    public DoubleLargeArray getImaginaryArray()
    {
        return dataIm;
    }

    /**
     * Returns the absolute value of this array.
     * <p>
     * @return the absolute value.
     */
    public DoubleLargeArray getAbsArray()
    {
        DoubleLargeArray out = new DoubleLargeArray(length, false);
        for (long i = 0; i < length; i++) {
            double re = dataRe.getDouble(i);
            double im = dataIm.getDouble(i);
            out.setDouble(i, Math.sqrt(re * re + im * im));
        }
        return out;
    }

    /**
     * Returns the argument of this array.
     * <p>
     * @return the argument
     */
    public DoubleLargeArray getArgArray()
    {
        DoubleLargeArray out = new DoubleLargeArray(length, false);
        for (long i = 0; i < length; i++) {
            double re = dataRe.getDouble(i);
            double im = dataIm.getDouble(i);
            out.setDouble(i, Math.atan2(im, re));
        }
        return out;
    }

    /**
     * Returns a complex value ({re, im}) at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value at index i ({re, im}).
     */
    @Override
    public double[] get(long i)
    {
        return getComplexDouble(i);
    }

    /**
     * Returns a complex value ({re, im}) at index i. Array bounds are not checked. If isLarge()
     * returns false for a given array or the index argument is invalid, then
     * calling this method will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value at index i ({re, im}).
     */
    @Override
    public double[] getFromNative(long i)
    {
        return new double[]{dataRe.getFromNative(i), dataIm.getFromNative(i)};
    }

    /**
     * Returns a boolean value that corresponds to the real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value that corresponds to the real part at index i.
     */
    @Override
    public boolean getBoolean(long i)
    {
        return dataRe.getBoolean(i);
    }

    /**
     * Returns a byte value that corresponds to the real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value that corresponds to the real part at index i.
     */
    @Override
    public byte getByte(long i)
    {
        return dataRe.getByte(i);
    }

    /**
     * Returns a short value that corresponds to the real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value that corresponds to the real part at index i.
     */
    @Override
    public short getShort(long i)
    {
        return dataRe.getShort(i);
    }

    /**
     * Returns a int value that corresponds to the real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value that corresponds to the real part at index i.
     */
    @Override
    public int getInt(long i)
    {
        return dataRe.getInt(i);
    }

    /**
     * Returns a long value that corresponds to the real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value that corresponds to the real part at index i.
     */
    @Override
    public long getLong(long i)
    {
        return dataRe.getLong(i);
    }

    /**
     * Returns a float value that corresponds to the real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value that corresponds to the real part at index i.
     */
    @Override
    public float getFloat(long i)
    {
        return dataRe.getFloat(i);
    }

    /**
     * Returns a double value that corresponds to the real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value that corresponds to the real part at index i.
     */
    @Override
    public double getDouble(long i)
    {
        return dataRe.getDouble(i);
    }

    /**
     * Returns a complex value ({re, im}) at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value at index i ({re, im}).
     */
    public float[] getComplexFloat(long i)
    {
        return new float[]{dataRe.getFloat(i), dataIm.getFloat(i)};
    }

    /**
     * Returns a complex value ({re, im}) at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value at index i ({re, im}).
     */
    public double[] getComplexDouble(long i)
    {
        return new double[]{dataRe.getDouble(i), dataIm.getDouble(i)};
    }

    /**
     * If the size of the array is smaller than LARGEST_32BIT_INDEX, then this
     * method returns complex data in the interleaved layout. Otherwise, it returns null.
     *
     * @return an array containing the elements of the list or null
     */
    @Override
    public double[] getData()
    {
        return getComplexData();
    }

    /**
     * If the size of the array is smaller than LARGEST_32BIT_INDEX, then this
     * method returns boolean data that correspond to the real part of this object. Otherwise, it returns null.
     *
     * @return an array containing the elements of the real part of this object or null
     */
    @Override
    public boolean[] getBooleanData()
    {
        return dataRe.getBooleanData();
    }

    /**
     * If (endPos - startPos) / step is smaller than LARGEST_32BIT_INDEX, then
     * this method returns selected elements of the real part of this object. Otherwise, it returns
     * null. If (endPos - startPos) / step is smaller or equal to a.length, it
     * is returned therein. Otherwise, a new array is allocated and returned.
     * Array bounds are checked.
     *
     * @param a        the array into which the elements are to be stored, if it is big
     *                 enough; otherwise, a new array of is allocated for this purpose.
     * @param startPos starting position (included)
     * @param endPos   ending position (excluded)
     * @param step     step size
     *
     * @return an array containing the elements of the real part this object or null
     */
    @Override
    public boolean[] getBooleanData(boolean[] a, long startPos, long endPos, long step)
    {
        return dataRe.getBooleanData(a, startPos, endPos, step);
    }

    /**
     * If the size of the array is smaller than LARGEST_32BIT_INDEX, then this
     * method returns byte data that correspond to the real part of this object. Otherwise, it returns null.
     *
     * @return an array containing the elements of the real part of this object or null
     */
    @Override
    public byte[] getByteData()
    {
        return dataRe.getByteData();
    }

    /**
     * If (endPos - startPos) / step is smaller than LARGEST_32BIT_INDEX, then
     * this method returns selected elements of the real part of this object. Otherwise, it returns
     * null. If (endPos - startPos) / step is smaller or equal to a.length, it
     * is returned therein. Otherwise, a new array is allocated and returned.
     * Array bounds are checked.
     *
     * @param a        the array into which the elements are to be stored, if it is big
     *                 enough; otherwise, a new array of is allocated for this purpose.
     * @param startPos starting position (included)
     * @param endPos   ending position (excluded)
     * @param step     step size
     *
     * @return an array containing the elements of the real part this object or null
     */
    @Override
    public byte[] getByteData(byte[] a, long startPos, long endPos, long step)
    {
        return dataRe.getByteData(a, startPos, endPos, step);
    }

    /**
     * If the size of the array is smaller than LARGEST_32BIT_INDEX, then this
     * method returns short data that correspond to the real part of this object. Otherwise, it returns null.
     *
     * @return an array containing the elements of the real part of this object or null
     */
    @Override
    public short[] getShortData()
    {
        return dataRe.getShortData();
    }

    /**
     * If (endPos - startPos) / step is smaller than LARGEST_32BIT_INDEX, then
     * this method returns selected elements of the real part of this object. Otherwise, it returns
     * null. If (endPos - startPos) / step is smaller or equal to a.length, it
     * is returned therein. Otherwise, a new array is allocated and returned.
     * Array bounds are checked.
     *
     * @param a        the array into which the elements are to be stored, if it is big
     *                 enough; otherwise, a new array of is allocated for this purpose.
     * @param startPos starting position (included)
     * @param endPos   ending position (excluded)
     * @param step     step size
     *
     * @return an array containing the elements of the real part this object or null
     */
    @Override
    public short[] getShortData(short[] a, long startPos, long endPos, long step)
    {
        return dataRe.getShortData(a, startPos, endPos, step);
    }

    /**
     * If the size of the array is smaller than LARGEST_32BIT_INDEX, then this
     * method returns int data that correspond to the real part of this object. Otherwise, it returns null.
     *
     * @return an array containing the elements of the real part of this object or null
     */
    @Override
    public int[] getIntData()
    {
        return dataRe.getIntData();
    }

    /**
     * If (endPos - startPos) / step is smaller than LARGEST_32BIT_INDEX, then
     * this method returns selected elements of the real part of this object. Otherwise, it returns
     * null. If (endPos - startPos) / step is smaller or equal to a.length, it
     * is returned therein. Otherwise, a new array is allocated and returned.
     * Array bounds are checked.
     *
     * @param a        the array into which the elements are to be stored, if it is big
     *                 enough; otherwise, a new array of is allocated for this purpose.
     * @param startPos starting position (included)
     * @param endPos   ending position (excluded)
     * @param step     step size
     *
     * @return an array containing the elements of the real part this object or null
     */
    @Override
    public int[] getIntData(int[] a, long startPos, long endPos, long step)
    {
        return dataRe.getIntData(a, startPos, endPos, step);
    }

    /**
     * If the size of the array is smaller than LARGEST_32BIT_INDEX, then this
     * method returns long data that correspond to the real part of this object. Otherwise, it returns null.
     *
     * @return an array containing the elements of the real part of this object or null
     */
    @Override
    public long[] getLongData()
    {
        return dataRe.getLongData();
    }

    /**
     * If (endPos - startPos) / step is smaller than LARGEST_32BIT_INDEX, then
     * this method returns selected elements of the real part of this object. Otherwise, it returns
     * null. If (endPos - startPos) / step is smaller or equal to a.length, it
     * is returned therein. Otherwise, a new array is allocated and returned.
     * Array bounds are checked.
     *
     * @param a        the array into which the elements are to be stored, if it is big
     *                 enough; otherwise, a new array of is allocated for this purpose.
     * @param startPos starting position (included)
     * @param endPos   ending position (excluded)
     * @param step     step size
     *
     * @return an array containing the elements of the real part this object or null
     */
    @Override
    public long[] getLongData(long[] a, long startPos, long endPos, long step)
    {
        return dataRe.getLongData(a, startPos, endPos, step);
    }

    /**
     * If the size of the array is smaller than LARGEST_32BIT_INDEX, then this
     * method returns float data that correspond to the real part of this object. Otherwise, it returns null.
     *
     * @return an array containing the elements of the real part of this object or null
     */
    @Override
    public float[] getFloatData()
    {
        return dataRe.getFloatData();
    }

    /**
     * If (endPos - startPos) / step is smaller than LARGEST_32BIT_INDEX, then
     * this method returns selected elements of the real part of this object. Otherwise, it returns
     * null. If (endPos - startPos) / step is smaller or equal to a.length, it
     * is returned therein. Otherwise, a new array is allocated and returned.
     * Array bounds are checked.
     *
     * @param a        the array into which the elements are to be stored, if it is big
     *                 enough; otherwise, a new array of is allocated for this purpose.
     * @param startPos starting position (included)
     * @param endPos   ending position (excluded)
     * @param step     step size
     *
     * @return an array containing the elements of the real part this object or null
     */
    @Override
    public float[] getFloatData(float[] a, long startPos, long endPos, long step)
    {
        return dataRe.getFloatData(a, startPos, endPos, step);
    }

    /**
     * If the size of the array is smaller than LARGEST_32BIT_INDEX, then this
     * method returns double data that correspond to the real part of this object. Otherwise, it returns null.
     *
     * @return an array containing the elements of the real part of this object or null
     */
    @Override
    public double[] getDoubleData()
    {
        return dataRe.getDoubleData();
    }

    /**
     * If (endPos - startPos) / step is smaller than LARGEST_32BIT_INDEX, then
     * this method returns selected elements of the real part of this object. Otherwise, it returns
     * null. If (endPos - startPos) / step is smaller or equal to a.length, it
     * is returned therein. Otherwise, a new array is allocated and returned.
     * Array bounds are checked.
     *
     * @param a        the array into which the elements are to be stored, if it is big
     *                 enough; otherwise, a new array of is allocated for this purpose.
     * @param startPos starting position (included)
     * @param endPos   ending position (excluded)
     * @param step     step size
     *
     * @return an array containing the elements of the real part this object or null
     */
    @Override
    public double[] getDoubleData(double[] a, long startPos, long endPos, long step)
    {
        return dataRe.getDoubleData(a, startPos, endPos, step);
    }

    /**
     * If the size of the array is smaller than LARGEST_32BIT_INDEX, then this
     * method returns complex data in the interleaved layout. Otherwise, it returns null.
     *
     * @return an array containing the elements of the list or null
     */
    public double[] getComplexData()
    {
        if (2 * length > getMaxSizeOf32bitArray()) {
            return null;
        } else {
            double[] out = new double[(int) (2 * length)];
            for (int i = 0; i < length; i++) {
                out[2 * i] = dataRe.getDouble(i);
                out[2 * i + 1] = dataIm.getDouble(i);
            }
            return out;
        }
    }

    /**
     * If (endPos - startPos) / step is smaller than LARGEST_32BIT_INDEX, then
     * this method returns selected elements of an array. Otherwise, it returns
     * null. If 2 * ((endPos - startPos) / step) is smaller or equal to a.length, it
     * is returned therein. Otherwise, a new array is allocated and returned.
     * Array bounds are checked.
     *
     * @param a        the array into which the elements are to be stored, if it is big
     *                 enough; otherwise, a new array of is allocated for this purpose.
     * @param startPos starting position (included)
     * @param endPos   ending position (excluded)
     * @param step     step size
     *
     * @return an array containing the elements of the list or null
     */
    public double[] getComplexData(double[] a, long startPos, long endPos, long step)
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

        long len = 2 * (long) Math.ceil((endPos - startPos) / (double) step);
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
            for (long i = startPos; i < endPos; i += step) {
                out[idx++] = dataRe.getDouble(i);
                out[idx++] = dataIm.getDouble(i);
            }

            return out;
        }
    }

    /**
     * Sets a complex value at index i. Array bounds are not checked. If isLarge()
     * returns false for a given array or the index argument is invalid, then
     * calling this method will cause JVM crash.
     *
     * @param i     index
     * @param value value to set, must be double[] of length 2
     *
     * @throws ClassCastException if the type of value argument is different
     *                            than the type of the array
     */
    @Override
    public void setToNative(long i, Object value)
    {
        if (!(value instanceof double[])) {
            throw new IllegalArgumentException(value + " is not an array of doubles.");
        }
        dataRe.setToNative(i, ((double[]) value)[0]);
        dataIm.setToNative(i, ((double[]) value)[1]);
    }

    /**
     * Sets a boolean value as a real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set as a real part
     */
    @Override
    public void setBoolean(long i, boolean value)
    {
        dataRe.setBoolean(i, value);
    }

    /**
     * Sets a byte value as a real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set as a real part
     */
    @Override
    public void setByte(long i, byte value)
    {
        dataRe.setByte(i, value);
    }

    /**
     * Sets a short value as a real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set as a real part
     */
    @Override
    public void setShort(long i, short value)
    {
        dataRe.setShort(i, value);
    }

    /**
     * Sets an int value as a real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set as a real part
     */
    @Override
    public void setInt(long i, int value)
    {
        dataRe.setInt(i, value);
    }

    /**
     * Sets a long value as a real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set as a real part
     */
    @Override
    public void setLong(long i, long value)
    {
        dataRe.setLong(i, value);
    }

    /**
     * Sets a float value as a real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set as a real part
     */
    @Override
    public void setFloat(long i, float value)
    {
        dataRe.setFloat(i, value);
    }

    /**
     * Sets a double value as a real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set as a real part
     */
    @Override
    public void setDouble(long i, double value)
    {
        dataRe.setDouble(i, value);
    }

    /**
     * Sets a complex value ({re, im}) at index i. Array bounds are not checked. Calling this
     * method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set, must be double[] of length 2
     */
    @Override
    public void set(long i, Object value)
    {
        if (!(value instanceof double[])) {
            throw new IllegalArgumentException(value + " is not an array of doubles.");
        }
        setComplexDouble(i, (double[]) value);
    }

    /**
     * Sets a complex value ({re, im}) at index i. Array bounds are not checked. Calling this
     * method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set
     */
    public void setComplexFloat(long i, float[] value)
    {
        dataRe.setFloat(i, value[0]);
        dataIm.setFloat(i, value[1]);
    }

    /**
     * Sets a complex value ({re, im}) at index i. Array bounds are not checked. Calling this
     * method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set
     */
    public void setComplexDouble(long i, double[] value)
    {
        dataRe.setDouble(i, value[0]);
        dataIm.setDouble(i, value[1]);
    }

}
