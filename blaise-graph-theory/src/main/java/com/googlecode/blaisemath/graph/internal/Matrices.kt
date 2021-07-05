package com.googlecode.blaisemath.graph.internal

import com.google.common.base.Preconditions
import com.googlecode.blaisemath.graph.ContractedGraphTest
import com.googlecode.blaisemath.graph.GraphUtilsTest
import com.googlecode.blaisemath.graph.SubgraphTest
import com.googlecode.blaisemath.graph.generate.GraphGrowthRule
import com.googlecode.blaisemath.graph.generate.GraphSeedRule
import com.googlecode.blaisemath.graph.generate.HopGrowthRule
import com.googlecode.blaisemath.graph.layout.SpringLayoutPerformanceTest
import com.googlecode.blaisemath.graph.metrics.AdditiveSubsetMetricTest
import com.googlecode.blaisemath.graph.metrics.BetweenCentralityTest
import com.googlecode.blaisemath.graph.metrics.ClosenessCentralityTest
import com.googlecode.blaisemath.graph.metrics.CooperationMetric
import com.googlecode.blaisemath.graph.metrics.EigenCentralityTest
import com.googlecode.blaisemath.graph.metrics.GraphCentralityTest
import com.googlecode.blaisemath.graph.metrics.SubsetMetricsTest
import com.googlecode.blaisemath.test.AssertUtils
import org.junit.BeforeClass
import java.util.*

/*
* #%L
* BlaiseMath
* --
* Copyright (C) 2009 - 2021 Elisha Peterson
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
*/ /**
 * Utilities for computing with matrices.
 * @author Elisha Peterson
 */
object Matrices {
    /**
     * Compute magnitude of vector.
     * @param vec vector
     * @return magnitude
     */
    fun magnitudeOf(vec: DoubleArray?): Double {
        var result = 0.0
        for (v in vec) {
            result += v * v
        }
        return Math.sqrt(result)
    }

    /**
     * Normalize a vector by dividing by magnitude.
     * @param vec vector
     */
    fun normalize(vec: DoubleArray?) {
        val magnitude = magnitudeOf(vec)
        for (i in vec.indices) {
            vec.get(i) /= magnitude
        }
    }

    /**
     * Multiply matrix by vector.
     * @param mx matrix
     * @param vec vector
     * @return product
     */
    fun matrixProduct(mx: Array<DoubleArray?>?, vec: DoubleArray?): DoubleArray? {
        Preconditions.checkArgument(mx.size == vec.size, "matrixProduct: require mx # rows = length of vector")
        val n = mx.size
        val result = DoubleArray(mx.get(0).length)
        Arrays.fill(result, 0.0)
        for (row in 0 until n) {
            for (col in 0 until n) {
                result[row] += mx.get(row).get(col) * vec.get(col)
            }
        }
        return result
    }

    /**
     * Computes product of two matrices of integers; first entry is row, second is column.
     * Requires # columns in m1 equal to number of rows in m2.
     * @param m1 first matrix
     * @param m2 second matrix
     * @return product
     */
    fun matrixProduct(m1: Array<IntArray?>?, m2: Array<IntArray?>?): Array<IntArray?>? {
        val rows1 = m1.size
        val cols1 = if (rows1 == 0) 0 else m1.get(0).length
        val rows2 = m2.size
        val cols2 = if (rows2 == 0) 0 else m2.get(0).length
        Preconditions.checkArgument(cols1 == rows2, "matrixProduct: incompatible matrix sizes")
        val result = Array<IntArray?>(rows1) { IntArray(cols2) }
        for (i in 0 until rows1) {
            for (j in 0 until rows2) {
                var sum = 0
                for (k in 0 until rows1) {
                    sum += m1.get(i).get(k) * m2.get(k).get(j)
                }
                result[i].get(j) = sum
            }
        }
        return result
    }

    /**
     * Computes product of two matrices of doubles; first entry is row, second is column.
     * Requires # columns in m1 equal to number of rows in m2.
     * @param m1 first matrix
     * @param m2 second matrix
     * @return product
     */
    fun matrixProduct(m1: Array<DoubleArray?>?, m2: Array<DoubleArray?>?): Array<DoubleArray?>? {
        val rows1 = m1.size
        val cols1 = if (rows1 == 0) 0 else m1.get(0).length
        val rows2 = m2.size
        val cols2 = if (rows2 == 0) 0 else m2.get(0).length
        Preconditions.checkArgument(cols1 == rows2, "matrixProduct: incompatible matrix sizes")
        val result = Array<DoubleArray?>(rows1) { DoubleArray(cols2) }
        for (i in 0 until rows1) {
            for (j in 0 until rows2) {
                var sum = 0.0
                for (k in 0 until rows1) {
                    sum += m1.get(i).get(k) * m2.get(k).get(j)
                }
                result[i].get(j) = sum
            }
        }
        return result
    }
}