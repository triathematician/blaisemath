/*
 * Matrices.java
 * Created Jul 14, 2010
 */

package com.googlecode.blaisemath.linear;

/*
 * #%L
 * BlaiseMath
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Arrays;

/**
 * Provides standard utilities for computing with matrices.
 * @author Elisha Peterson
 * @version 1.0
 */
public class Matrices {

    /** Utility class */
    private Matrices() {
    }
    
    /**
     * Compute magnitude of vector
     * @param vec vector
     * @return magnitude
     */
    public static double magnitudeOf(double[] vec) {
        double result = 0.0;
        for (int i = 0; i < vec.length; i++)
            result += vec[i]*vec[i];
        return Math.sqrt(result);
    }

    /** 
     * Normalize a vector by dividing by magnitude
     * @param vec vector
     */
    public static void normalize(double[] vec) {
        double magn = magnitudeOf(vec);
        for (int i = 0; i < vec.length; i++)
            vec[i] /= magn;
    }

    /**
     * Multiply matrix by vector
     * @param mx matrix
     * @param vec vector
     * @return product
     */
    public static double[] matrixProduct(double[][] mx, double[] vec) {
        if (mx.length != vec.length)
            throw new IllegalArgumentException("matrixProduct: require mx # rows = length of vector");
        int n = mx.length;
        double[] result = new double[mx[0].length];
        Arrays.fill(result, 0.0);
        for (int row = 0; row < n; row++)
            for (int col = 0; col < n; col++)
                result[row] += mx[row][col] * vec[col];
        return result;
    }

    /**
     * Computes product of two matrices of integers
     * First entry is row, second is column.
     * Requires # columns in m1 equal to number of rows in m2
     * @param m1 first matrix
     * @param m2 second matrix
     * @return product
     */
    public static int[][] matrixProduct(int[][] m1, int[][] m2) {
        int rows1 = m1.length, cols1 = m1[0].length;
        int rows2 = m2.length, cols2 = m2[0].length;
        if (cols1 != rows2)
            throw new IllegalArgumentException("matrixProduct: incompatible matrix sizes");
        int[][] result = new int[rows1][cols2];
        for (int i = 0; i < rows1; i++)
            for (int j = 0; j < rows2; j++) {
                int sum = 0;
                for (int k = 0; k < rows1; k++)
                    sum += m1[i][k]*m2[k][j];
                result[i][j] = sum;
            }
        return result;
    }
    
    /**
     * Computes product of two matrices of doubles
     * First entry is row, second is column.
     * Requires # columns in m1 equal to number of rows in m2
     * @param m1 first matrix
     * @param m2 second matrix
     * @return product
     */
    public static double[][] matrixProduct(double[][] m1, double[][] m2) {
        int rows1 = m1.length, cols1 = m1[0].length;
        int rows2 = m2.length, cols2 = m2[0].length;
        if (cols1 != rows2)
            throw new IllegalArgumentException("matrixProduct: incompatible matrix sizes");
        double[][] result = new double[rows1][cols2];
        for (int i = 0; i < rows1; i++)
            for (int j = 0; j < rows2; j++) {
                double sum = 0;
                for (int k = 0; k < rows1; k++)
                    sum += m1[i][k]*m2[k][j];
                result[i][j] = sum;
            }
        return result;
    }

    
}
