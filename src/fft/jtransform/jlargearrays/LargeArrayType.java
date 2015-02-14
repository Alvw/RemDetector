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

/**
 * Supported types of large arrays.
 *
 * @author Piotr Wendykier (p.wendykier@icm.edu.pl)
 */
public enum LargeArrayType
{

    LOGIC, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, COMPLEX_FLOAT, COMPLEX_DOUBLE, STRING, OBJECT;

    public long sizeOf()
    {
        switch (this)
        {
        case LOGIC:
        case BYTE:
            return 1;
        case SHORT:
            return 2;
        case INT:
            return 4;
        case LONG:
            return 8;
        case FLOAT:
            return 4;
        case DOUBLE:
            return 8;
        case COMPLEX_FLOAT:
            return 4;
        case COMPLEX_DOUBLE:
            return 8;
        case STRING:
            return 1;
        case OBJECT:
            return 1;
        }
        return 0;

    }
}
