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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Benchmarks.
 *
 * @author Piotr Wendykier (p.wendykier@icm.edu.pl)
 */
public class Benchmark
{

    public static void writeToFile(long[] sizes, int[] nthreads, double[][] results, String file)
    {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version"));
            writer.newLine();
            writer.write(System.getProperty("java.vendor") + " " + System.getProperty("java.version"));
            writer.newLine();
            writer.write("Available processors (cores): " + Runtime.getRuntime().availableProcessors());
            writer.newLine();
            writer.write("Total memory (bytes): " + Runtime.getRuntime().totalMemory());
            writer.newLine();
            writer.write("Number of threads: {");
            for (int th = 0; th < nthreads.length; th++) {
                if (th < nthreads.length - 1) {
                    writer.write(nthreads[th] + ",");
                } else {
                    writer.write(nthreads[nthreads.length - 1] + "}");
                }
            }
            writer.newLine();
            writer.write("Sizes: {");
            for (int i = 0; i < sizes.length; i++) {
                if (i < sizes.length - 1) {
                    writer.write(sizes[i] + ",");
                } else {
                    writer.write(sizes[sizes.length - 1] + "}");
                }
            }
            writer.newLine();
            writer.write("Timings: {");
            for (int th = 0; th < nthreads.length; th++) {
                writer.write("{");
                if (th < nthreads.length - 1) {
                    for (int i = 0; i < sizes.length; i++) {
                        if (i < sizes.length - 1) {
                            writer.write(results[th][i] + ",");
                        } else {
                            writer.write(results[th][i] + "},");
                        }
                    }
                    writer.newLine();
                } else {
                    for (int i = 0; i < sizes.length; i++) {
                        if (i < sizes.length - 1) {
                            writer.write(results[th][i] + ",");
                        } else {
                            writer.write(results[th][i] + "}}");
                        }
                    }
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static double[][] benchmarkJavaArraysByteSequential(long[] sizes, int[] nthreads, int iters, String file)
    {
        for (int i = 0; i < sizes.length; i++) {
            if (sizes[i] > Integer.MAX_VALUE - 4) {
                return null;
            }
        }
        double[][] results = new double[nthreads.length][sizes.length];
        long k;
        System.out.println("Benchmarking java arrays (bytes, sequentual)");
        for (int th = 0; th < nthreads.length; th++) {
            int nt = nthreads[th];
            Thread[] threads = new Thread[nt];
            System.out.println("\tNumber of threads = " + nt);
            for (int i = 0; i < sizes.length; i++) {
                System.out.print("\tSize = " + sizes[i]);
                final byte[] a = new byte[(int) sizes[i]];
                double t = System.nanoTime();
                for (int it = 0; it < iters; it++) {
                    k = sizes[i] / nt;
                    for (int j = 0; j < nt; j++) {
                        final int firstIdx = (int) (j * k);
                        final int lastIdx = (int) ((j == nt - 1) ? sizes[i] : firstIdx + k);
                        threads[j] = new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                for (int k = firstIdx; k < lastIdx; k++) {
                                    a[k] = 1;
                                    a[k] += 1;
                                }
                            }
                        });
                        threads[j].start();
                    }
                    try {
                        for (int j = 0; j < nt; j++) {
                            threads[j].join();
                            threads[j] = null;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                results[th][i] = (System.nanoTime() - t) / 1000000000.0 / (double) iters;
                System.out.println(" : " + String.format("%.7f sec", results[th][i]));
            }
        }
        writeToFile(sizes, nthreads, results, file);
        return results;
    }

    public static double[][] benchmarkJavaArraysDoubleSequential(long[] sizes, int[] nthreads, int iters, String file)
    {
        for (int i = 0; i < sizes.length; i++) {
            if (sizes[i] > Integer.MAX_VALUE - 4) {
                return null;
            }
        }
        double[][] results = new double[nthreads.length][sizes.length];
        long k;
        System.out.println("Benchmarking java arrays (doubles, sequentual)");
        for (int th = 0; th < nthreads.length; th++) {
            int nt = nthreads[th];
            Thread[] threads = new Thread[nt];
            System.out.println("\tNumber of threads = " + nt);
            for (int i = 0; i < sizes.length; i++) {
                System.out.print("\tSize = " + sizes[i]);
                final double[] a = new double[(int) sizes[i]];
                double t = System.nanoTime();
                for (int it = 0; it < iters; it++) {
                    k = sizes[i] / nt;
                    for (int j = 0; j < nt; j++) {
                        final int firstIdx = (int) (j * k);
                        final int lastIdx = (int) ((j == nt - 1) ? sizes[i] : firstIdx + k);
                        threads[j] = new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                for (int k = firstIdx; k < lastIdx; k++) {
                                    a[k] = 1;
                                    a[k] += 1;
                                }
                            }
                        });
                        threads[j].start();
                    }
                    try {
                        for (int j = 0; j < nt; j++) {
                            threads[j].join();
                            threads[j] = null;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                results[th][i] = (System.nanoTime() - t) / 1000000000.0 / (double) iters;
                System.out.println(" : " + String.format("%.7f sec", results[th][i]));
            }
        }
        writeToFile(sizes, nthreads, results, file);
        return results;
    }

    public static double[][] benchmarkJavaArraysByteRandom(long[] sizes, int[] nthreads, int iters, String file)
    {
        for (int i = 0; i < sizes.length; i++) {
            if (sizes[i] > Integer.MAX_VALUE - 4) {
                return null;
            }
        }

        final int[] randIdx = new int[(int) sizes[sizes.length - 1]];
        double[][] results = new double[nthreads.length][sizes.length];
        long k;
        Random r = new Random(0);
        System.out.println("generating random indices.");
        int max = (int) sizes[sizes.length - 1];
        for (int i = 0; i < max; i++) {
            randIdx[i] = (int) (r.nextDouble() * (max - 1));
        }

        System.out.println("Benchmarking java arrays (bytes, random)");
        for (int th = 0; th < nthreads.length; th++) {
            int nt = nthreads[th];
            Thread[] threads = new Thread[nt];
            System.out.println("\tNumber of threads = " + nt);
            for (int i = 0; i < sizes.length; i++) {
                System.out.print("\tSize = " + sizes[i]);
                final byte[] a = new byte[(int) sizes[i]];
                final long size = sizes[i];
                double t = System.nanoTime();
                for (int it = 0; it < iters; it++) {
                    k = sizes[i] / nt;
                    for (int j = 0; j < nt; j++) {
                        final int firstIdx = (int) (j * k);
                        final int lastIdx = (int) ((j == nt - 1) ? size : firstIdx + k);
                        threads[j] = new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                for (int k = firstIdx; k < lastIdx; k++) {
                                    int idx = (int) (randIdx[k] % size);
                                    a[idx] = 1;
                                    a[idx] += 1;
                                }
                            }
                        });
                        threads[j].start();
                    }
                    try {
                        for (int j = 0; j < nt; j++) {
                            threads[j].join();
                            threads[j] = null;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                results[th][i] = (System.nanoTime() - t) / 1000000000.0 / (double) iters;
                System.out.println(" : " + String.format("%.7f sec", results[th][i]));
            }
        }
        writeToFile(sizes, nthreads, results, file);
        return results;
    }

    public static double[][] benchmarkJavaArraysDoubleRandom(long[] sizes, int[] nthreads, int iters, String file)
    {
        for (int i = 0; i < sizes.length; i++) {
            if (sizes[i] > Integer.MAX_VALUE - 4) {
                return null;
            }
        }

        final int[] randIdx = new int[(int) sizes[sizes.length - 1]];
        double[][] results = new double[nthreads.length][sizes.length];
        long k;
        Random r = new Random(0);
        System.out.println("generating random indices.");
        int max = (int) sizes[sizes.length - 1];
        for (int i = 0; i < max; i++) {
            randIdx[i] = (int) (r.nextDouble() * (max - 1));
        }

        System.out.println("Benchmarking java arrays (double, random)");
        for (int th = 0; th < nthreads.length; th++) {
            int nt = nthreads[th];
            Thread[] threads = new Thread[nt];
            System.out.println("\tNumber of threads = " + nt);
            for (int i = 0; i < sizes.length; i++) {
                System.out.print("\tSize = " + sizes[i]);
                final double[] a = new double[(int) sizes[i]];
                final long size = sizes[i];
                double t = System.nanoTime();
                for (int it = 0; it < iters; it++) {
                    k = sizes[i] / nt;
                    for (int j = 0; j < nt; j++) {
                        final int firstIdx = (int) (j * k);
                        final int lastIdx = (int) ((j == nt - 1) ? size : firstIdx + k);
                        threads[j] = new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                for (int k = firstIdx; k < lastIdx; k++) {
                                    int idx = (int) (randIdx[k] % size);
                                    a[idx] = 1.;
                                    a[idx] += 1.;
                                }
                            }
                        });
                        threads[j].start();
                    }
                    try {
                        for (int j = 0; j < nt; j++) {
                            threads[j].join();
                            threads[j] = null;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                results[th][i] = (System.nanoTime() - t) / 1000000000.0 / (double) iters;
                System.out.println(" : " + String.format("%.7f sec", results[th][i]));
            }
        }
        writeToFile(sizes, nthreads, results, file);
        return results;
    }

    public static double[][] benchmarkJLargeArraysByteSequentual(long[] sizes, int[] nthreads, int iters, String file)
    {
        double[][] results = new double[nthreads.length][sizes.length];
        long k;
        System.out.println("Benchmarking JLargeArrays (bytes, sequentual)");
        for (int th = 0; th < nthreads.length; th++) {
            int nt = nthreads[th];
            Thread[] threads = new Thread[nt];
            System.out.println("\tNumber of threads = " + nt);
            for (int i = 0; i < sizes.length; i++) {
                System.out.print("\tSize = " + sizes[i]);
                final ByteLargeArray a = new ByteLargeArray(sizes[i]);
                double t = System.nanoTime();
                for (int it = 0; it < iters; it++) {
                    k = sizes[i] / nt;
                    for (int j = 0; j < nt; j++) {
                        final long firstIdx = j * k;
                        final long lastIdx = (j == nt - 1) ? sizes[i] : firstIdx + k;
                        threads[j] = new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                for (long k = firstIdx; k < lastIdx; k++) {
                                    a.setByte(k, (byte) 1);
                                    a.setByte(k, (byte) (a.getByte(k) + 1));
                                }
                            }
                        });
                        threads[j].start();
                    }
                    try {
                        for (int j = 0; j < nt; j++) {
                            threads[j].join();
                            threads[j] = null;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                results[th][i] = (System.nanoTime() - t) / 1000000000.0 / (double) iters;
                System.out.println(" : " + String.format("%.7f sec", results[th][i]));
            }
        }
        writeToFile(sizes, nthreads, results, file);
        return results;
    }

    public static double[][] benchmarkJLargeArraysByteSequentualNative(long[] sizes, int[] nthreads, int iters, String file)
    {
        LargeArray.setMaxSizeOf32bitArray(1);
        double[][] results = new double[nthreads.length][sizes.length];
        long k;
        System.out.println("Benchmarking JLargeArrays (bytes, sequentual, native)");
        for (int th = 0; th < nthreads.length; th++) {
            int nt = nthreads[th];
            Thread[] threads = new Thread[nt];
            System.out.println("\tNumber of threads = " + nt);
            for (int i = 0; i < sizes.length; i++) {
                System.out.print("\tSize = " + sizes[i]);
                final ByteLargeArray a = new ByteLargeArray(sizes[i]);
                double t = System.nanoTime();
                for (int it = 0; it < iters; it++) {
                    k = sizes[i] / nt;
                    for (int j = 0; j < nt; j++) {
                        final long firstIdx = j * k;
                        final long lastIdx = (j == nt - 1) ? sizes[i] : firstIdx + k;
                        threads[j] = new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                for (long k = firstIdx; k < lastIdx; k++) {
                                    a.setToNative(k, (byte) 1);
                                    a.setToNative(k, (byte) (a.getFromNative(k) + 1));
                                }
                            }
                        });
                        threads[j].start();
                    }
                    try {
                        for (int j = 0; j < nt; j++) {
                            threads[j].join();
                            threads[j] = null;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                results[th][i] = (System.nanoTime() - t) / 1000000000.0 / (double) iters;
                System.out.println(" : " + String.format("%.7f sec", results[th][i]));
            }
        }
        writeToFile(sizes, nthreads, results, file);
        return results;
    }

    public static double[][] benchmarkJLargeArraysByteSequentual_safe(long[] sizes, int[] nthreads, int iters, String file)
    {
        double[][] results = new double[nthreads.length][sizes.length];
        long k;
        System.out.println("Benchmarking JLargeArrays (bytes, sequentual, with bounds checking)");
        for (int th = 0; th < nthreads.length; th++) {
            int nt = nthreads[th];
            Thread[] threads = new Thread[nt];
            System.out.println("\tNumber of threads = " + nt);
            for (int i = 0; i < sizes.length; i++) {
                System.out.print("\tSize = " + sizes[i]);
                final ByteLargeArray a = new ByteLargeArray(sizes[i]);
                double t = System.nanoTime();
                for (int it = 0; it < iters; it++) {
                    k = sizes[i] / nt;
                    for (int j = 0; j < nt; j++) {
                        final long firstIdx = j * k;
                        final long lastIdx = (j == nt - 1) ? sizes[i] : firstIdx + k;
                        threads[j] = new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                for (long k = firstIdx; k < lastIdx; k++) {
                                    a.setByte_safe(k, (byte) 1);
                                    a.setByte_safe(k, (byte) (a.getByte_safe(k) + 1));
                                }
                            }
                        });
                        threads[j].start();
                    }
                    try {
                        for (int j = 0; j < nt; j++) {
                            threads[j].join();
                            threads[j] = null;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                results[th][i] = (System.nanoTime() - t) / 1000000000.0 / (double) iters;
                System.out.println(" : " + String.format("%.7f sec", results[th][i]));
            }
        }
        writeToFile(sizes, nthreads, results, file);
        return results;
    }

    public static double[][] benchmarkJLargeArraysDoubleSequentual(long[] sizes, int[] nthreads, int iters, String file)
    {
        double[][] results = new double[nthreads.length][sizes.length];
        long k;
        System.out.println("Benchmarking JLargeArrays (doubles, sequentual)");
        for (int th = 0; th < nthreads.length; th++) {
            int nt = nthreads[th];
            Thread[] threads = new Thread[nt];
            System.out.println("\tNumber of threads = " + nt);
            for (int i = 0; i < sizes.length; i++) {
                System.out.print("\tSize = " + sizes[i]);
                final DoubleLargeArray a = new DoubleLargeArray(sizes[i]);
                double t = System.nanoTime();
                for (int it = 0; it < iters; it++) {
                    k = sizes[i] / nt;
                    for (int j = 0; j < nt; j++) {
                        final long firstIdx = j * k;
                        final long lastIdx = (j == nt - 1) ? sizes[i] : firstIdx + k;
                        threads[j] = new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                for (long k = firstIdx; k < lastIdx; k++) {
                                    a.setDouble(k, 1.);
                                    a.setDouble(k, (a.getDouble(k) + 1.));
                                }
                            }
                        });
                        threads[j].start();
                    }
                    try {
                        for (int j = 0; j < nt; j++) {
                            threads[j].join();
                            threads[j] = null;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                results[th][i] = (System.nanoTime() - t) / 1000000000.0 / (double) iters;
                System.out.println(" : " + String.format("%.7f sec", results[th][i]));
            }
        }
        writeToFile(sizes, nthreads, results, file);
        return results;
    }

    public static double[][] benchmarkJLargeArraysDoubleSequentualNative(long[] sizes, int[] nthreads, int iters, String file)
    {
        LargeArray.setMaxSizeOf32bitArray(1);
        double[][] results = new double[nthreads.length][sizes.length];
        long k;
        System.out.println("Benchmarking JLargeArrays (doubles, sequentual, native)");
        for (int th = 0; th < nthreads.length; th++) {
            int nt = nthreads[th];
            Thread[] threads = new Thread[nt];
            System.out.println("\tNumber of threads = " + nt);
            for (int i = 0; i < sizes.length; i++) {
                System.out.print("\tSize = " + sizes[i]);
                final DoubleLargeArray a = new DoubleLargeArray(sizes[i]);
                double t = System.nanoTime();
                for (int it = 0; it < iters; it++) {
                    k = sizes[i] / nt;
                    for (int j = 0; j < nt; j++) {
                        final long firstIdx = j * k;
                        final long lastIdx = (j == nt - 1) ? sizes[i] : firstIdx + k;
                        threads[j] = new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                for (long k = firstIdx; k < lastIdx; k++) {
                                    a.setToNative(k, 1.);
                                    a.setToNative(k, (a.getFromNative(k) + 1.));
                                }
                            }
                        });
                        threads[j].start();
                    }
                    try {
                        for (int j = 0; j < nt; j++) {
                            threads[j].join();
                            threads[j] = null;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                results[th][i] = (System.nanoTime() - t) / 1000000000.0 / (double) iters;
                System.out.println(" : " + String.format("%.7f sec", results[th][i]));
            }
        }
        writeToFile(sizes, nthreads, results, file);
        return results;
    }

    public static double[][] benchmarkJLargeArraysByteRandom(long[] sizes, int[] nthreads, int iters, String file)
    {
        final int[] randIdx = new int[(int) sizes[sizes.length - 1]];
        double[][] results = new double[nthreads.length][sizes.length];
        long k;
        Random r = new Random(0);
        System.out.println("generating random indices.");
        int max = (int) sizes[sizes.length - 1];
        for (int i = 0; i < max; i++) {
            randIdx[i] = (int) (r.nextDouble() * (max - 1));
        }
        System.out.println("Benchmarking JLargeArrays (bytes, random)");
        for (int th = 0; th < nthreads.length; th++) {
            int nt = nthreads[th];
            Thread[] threads = new Thread[nt];
            System.out.println("\tNumber of threads = " + nt);
            for (int i = 0; i < sizes.length; i++) {
                System.out.print("\tSize = " + sizes[i]);
                final ByteLargeArray a = new ByteLargeArray(sizes[i]);
                final int size = (int) sizes[i];
                double t = System.nanoTime();
                for (int it = 0; it < iters; it++) {
                    k = sizes[i] / nt;
                    for (int j = 0; j < nt; j++) {
                        final long firstIdx = j * k;
                        final long lastIdx = (j == nt - 1) ? sizes[i] : firstIdx + k;
                        threads[j] = new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                for (long k = firstIdx; k < lastIdx; k++) {
                                    long idx = randIdx[(int) k] % size;
                                    a.setByte(idx, (byte) 1);
                                    a.setByte(idx, (byte) (a.getByte(idx) + 1));
                                }
                            }
                        });
                        threads[j].start();
                    }
                    try {
                        for (int j = 0; j < nt; j++) {
                            threads[j].join();
                            threads[j] = null;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                results[th][i] = (System.nanoTime() - t) / 1000000000.0 / (double) iters;
                System.out.println(" : " + String.format("%.7f sec", results[th][i]));
            }
        }
        writeToFile(sizes, nthreads, results, file);
        return results;
    }

    public static double[][] benchmarkJLargeArraysDoubleRandom(long[] sizes, int[] nthreads, int iters, String file)
    {
        final int[] randIdx = new int[(int) sizes[sizes.length - 1]];
        double[][] results = new double[nthreads.length][sizes.length];
        long k;
        Random r = new Random(0);
        System.out.println("generating random indices.");
        int max = (int) sizes[sizes.length - 1];
        for (int i = 0; i < max; i++) {
            randIdx[i] = (int) (r.nextDouble() * (max - 1));
        }
        System.out.println("Benchmarking JLargeArrays (doubles, random)");
        for (int th = 0; th < nthreads.length; th++) {
            int nt = nthreads[th];
            Thread[] threads = new Thread[nt];
            System.out.println("\tNumber of threads = " + nt);
            for (int i = 0; i < sizes.length; i++) {
                System.out.print("\tSize = " + sizes[i]);
                final DoubleLargeArray a = new DoubleLargeArray(sizes[i]);
                final int size = (int) sizes[i];
                double t = System.nanoTime();
                for (int it = 0; it < iters; it++) {
                    k = sizes[i] / nt;
                    for (int j = 0; j < nt; j++) {
                        final long firstIdx = j * k;
                        final long lastIdx = (j == nt - 1) ? sizes[i] : firstIdx + k;
                        threads[j] = new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                for (long k = firstIdx; k < lastIdx; k++) {
                                    long idx = randIdx[(int) k] % size;
                                    a.setDouble(idx, 1.);
                                    a.setDouble(idx, (a.getDouble(idx) + 1.));
                                }
                            }
                        });
                        threads[j].start();
                    }
                    try {
                        for (int j = 0; j < nt; j++) {
                            threads[j].join();
                            threads[j] = null;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                results[th][i] = (System.nanoTime() - t) / 1000000000.0 / (double) iters;
                System.out.println(" : " + String.format("%.7f sec", results[th][i]));
            }
        }
        writeToFile(sizes, nthreads, results, file);
        return results;
    }

    public static void benchmarkByteSequential(long[] sizes, int[] nthreads, int iters, String directory)
    {
        benchmarkJavaArraysByteSequential(sizes, nthreads, iters, directory + System.getProperty("file.separator") + "java_arrays_byte_sequential.txt");
        System.gc();
        benchmarkJLargeArraysByteSequentual(sizes, nthreads, iters, directory + System.getProperty("file.separator") + "jlargearrays_byte_sequentual.txt");
    }

    public static void benchmarkDoubleSequential(long[] sizes, int[] nthreads, int iters, String directory)
    {
        benchmarkJavaArraysDoubleSequential(sizes, nthreads, iters, directory + System.getProperty("file.separator") + "java_arrays_double_sequential.txt");
        System.gc();
        benchmarkJLargeArraysDoubleSequentual(sizes, nthreads, iters, directory + System.getProperty("file.separator") + "jlargearrays_double_sequentual.txt");
    }

    public static void benchmarkByteRandom(long[] sizes, int[] nthreads, int iters, String directory)
    {
        benchmarkJavaArraysByteRandom(sizes, nthreads, iters, directory + System.getProperty("file.separator") + "java_arrays_byte_random.txt");
        System.gc();
        benchmarkJLargeArraysByteRandom(sizes, nthreads, iters, directory + System.getProperty("file.separator") + "jlargearrays_byte_random.txt");
    }

    public static void benchmarkDoubleRandom(long[] sizes, int[] nthreads, int iters, String directory)
    {
        benchmarkJavaArraysDoubleRandom(sizes, nthreads, iters, directory + System.getProperty("file.separator") + "java_arrays_double_random.txt");
        System.gc();
        benchmarkJLargeArraysDoubleRandom(sizes, nthreads, iters, directory + System.getProperty("file.separator") + "jlargearrays_double_random.txt");
    }

    public static void benchmarkByteLargeArray()
    {
        System.out.println("Benchmarking ByteLargeArray.");
        long length = (long) Math.pow(2, 32);
        long start = System.nanoTime();
        ByteLargeArray array = new ByteLargeArray(length);
        System.out.println("Constructor time: " + (System.nanoTime() - start) / 1e9 + " sec");
        int iters = 5;
        byte one = 1;
        for (int it = 0; it < iters; it++) {
            start = System.nanoTime();
            for (long i = 0; i < length; i++) {
                array.getByte(i);
                array.setByte(i, one);
                array.setByte(i, (byte) (array.getByte(i) + one));
            }
            System.out.println("Computation time: " + (System.nanoTime() - start) / 1e9 + "sec");
        }
    }

    public static void benchmarkByteLargeArrayInANewThread()
    {
        System.out.println("Benchmarking ByteLargeArray in a new thread.");
        final long length = (long) Math.pow(2, 32);
        long start = System.nanoTime();
        final ByteLargeArray array = new ByteLargeArray(length);
        System.out.println("Constructor time: " + (System.nanoTime() - start) / 1e9 + " sec");
        final int iters = 5;
        final byte one = 1;
        for (int it = 0; it < iters; it++) {
            start = System.nanoTime();
            Thread thread = new Thread(new Runnable()
            {
                public void run()
                {
                    for (long k = 0; k < length; k++) {
                        array.setByte(k, one);
                        array.setByte(k, (byte) (array.getByte(k) + one));
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.out.println("Computation time: " + (System.nanoTime() - start) / 1e9 + " sec");
        }
    }

    public static void benchmarkFloatLargeArray()
    {
        System.out.println("Benchmarking FloatLargeArray.");
        long length = (long) Math.pow(2, 32);
        long start = System.nanoTime();
        FloatLargeArray array = new FloatLargeArray(length);
        System.out.println("Constructor time: " + (System.nanoTime() - start) / 1e9 + " sec");
        int iters = 5;
        for (int it = 0; it < iters; it++) {
            start = System.nanoTime();
            for (long i = 0; i < length; i++) {
                array.getFloat(i);
                array.setFloat(i, 1.0f);
                array.setFloat(i, (array.getFloat(i) + 1.0f));
            }
            System.out.println("Computation time: " + (System.nanoTime() - start) / 1e9 + "sec");
        }
    }

    public static void benchmarkFloatLargeArrayInANewThread()
    {
        System.out.println("Benchmarking FloatLargeArray in a new thread.");
        final long length = (long) Math.pow(2, 32);
        long start = System.nanoTime();
        final FloatLargeArray array = new FloatLargeArray(length);
        System.out.println("Constructor time: " + (System.nanoTime() - start) / 1e9 + " sec");
        final int iters = 5;
        for (int it = 0; it < iters; it++) {
            start = System.nanoTime();
            Thread thread = new Thread(new Runnable()
            {
                public void run()
                {
                    for (long k = 0; k < length; k++) {
                        array.setFloat(k, 1.0f);
                        array.setFloat(k, (array.getFloat(k) + 1.0f));
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.out.println("Computation time: " + (System.nanoTime() - start) / 1e9 + " sec");
        }
    }

    public static void benchmarkByteLargeArrayNative()
    {
        System.out.println("Benchmarking ByteLargeArray native.");
        long length = (long) Math.pow(2, 32);
        long start = System.nanoTime();
        ByteLargeArray array = new ByteLargeArray(length, false);
        System.out.println("Constructor time: " + (System.nanoTime() - start) / 1e9 + " sec");
        int iters = 5;
        byte one = 1;
        if (array.isLarge()) {
            for (int it = 0; it < iters; it++) {
                start = System.nanoTime();
                for (long i = 0; i < length; i++) {
                    array.getFromNative(i);
                    array.setToNative(i, one);
                    array.setToNative(i, (byte) (array.getFromNative(i) + one));
                }
                System.out.println("Computation time: " + (System.nanoTime() - start) / 1e9 + " sec");
            }
        }
    }

    public static void benchmarkByteLargeArrayNativeInANewThread()
    {
        System.out.println("Benchmarking ByteLargeArray native in a new thread.");
        final long length = (long) Math.pow(2, 32);
        long start = System.nanoTime();
        final ByteLargeArray array = new ByteLargeArray(length);
        System.out.println("Constructor time: " + (System.nanoTime() - start) / 1e9 + " sec");
        final int iters = 5;
        final byte one = 1;
        for (int it = 0; it < iters; it++) {
            start = System.nanoTime();
            Thread thread = new Thread(new Runnable()
            {
                public void run()
                {
                    for (long k = 0; k < length; k++) {
                        array.setToNative(k, one);
                        array.setToNative(k, (byte) (array.getFromNative(k) + one));
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.out.println("Computation time: " + (System.nanoTime() - start) / 1e9 + " sec");
        }
    }

    public static void benchmarkFloatLargeArrayNative()
    {
        System.out.println("Benchmarking FloatLargeArray native.");
        long length = (long) Math.pow(2, 32);
        long start = System.nanoTime();
        FloatLargeArray array = new FloatLargeArray(length, false);
        System.out.println("Constructor time: " + (System.nanoTime() - start) / 1e9 + " sec");
        int iters = 5;
        if (array.isLarge()) {
            for (int it = 0; it < iters; it++) {
                start = System.nanoTime();
                for (long i = 0; i < length; i++) {
                    array.getFromNative(i);
                    array.setToNative(i, 1.0f);
                    array.setToNative(i, array.getFromNative(i) + 1.0f);
                }
                System.out.println("Computation time: " + (System.nanoTime() - start) / 1e9 + " sec");
            }
        }
    }

    public static void benchmarkFloatLargeArrayNativeInANewThread()
    {
        System.out.println("Benchmarking FloatLargeArray native in a new thread.");
        final long length = (long) Math.pow(2, 32);
        long start = System.nanoTime();
        final FloatLargeArray array = new FloatLargeArray(length);
        System.out.println("Constructor time: " + (System.nanoTime() - start) / 1e9 + " sec");
        final int iters = 5;
        for (int it = 0; it < iters; it++) {
            start = System.nanoTime();
            Thread thread = new Thread(new Runnable()
            {
                public void run()
                {
                    for (long k = 0; k < length; k++) {
                        array.setToNative(k, 1.0f);
                        array.setToNative(k, array.getFromNative(k) + 1.0f);
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.out.println("Computation time: " + (System.nanoTime() - start) / 1e9 + " sec");
        }
    }

    public static void main(String[] args)
    {
        final int smallSizesIters = 10;
        int initial_power_of_two_exp = 27;
        int final_power_of_two_exp = 32;
        int length = final_power_of_two_exp - initial_power_of_two_exp + 1;
        long[] smallSizes = new long[length];
        for (int i = 0; i < length; i++) {
            if (initial_power_of_two_exp + i == 31) {
                smallSizes[i] = (long) Math.pow(2, 31) - 4;
            } else {
                smallSizes[i] = (long) Math.pow(2, initial_power_of_two_exp + i);
            }
        }

        final int largeSizesIters = 2;
        initial_power_of_two_exp = 32;
        final_power_of_two_exp = 35;
        length = final_power_of_two_exp - initial_power_of_two_exp + 1;
        long[] largeSizes = new long[length];
        for (int i = 0; i < length; i++) {
            largeSizes[i] = (long) Math.pow(2, initial_power_of_two_exp + i);
        }
        int[] threads = {1, 2, 4, 8, 16};

        LargeArray.setMaxSizeOf32bitArray(1);
        benchmarkByteSequential(smallSizes, threads, smallSizesIters, "/tmp/");
        benchmarkDoubleSequential(smallSizes, threads, smallSizesIters, "/tmp/");
        benchmarkByteRandom(smallSizes, threads, smallSizesIters, "/tmp/");
        benchmarkDoubleRandom(smallSizes, threads, smallSizesIters, "/tmp/");
    }

}
