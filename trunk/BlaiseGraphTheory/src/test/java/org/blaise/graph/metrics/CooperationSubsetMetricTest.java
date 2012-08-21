/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.blaise.graph.metrics;

import java.util.Arrays;
import java.util.HashSet;
import org.blaise.graph.Graph;
import org.blaise.graph.SparseGraph;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class CooperationSubsetMetricTest {

    static Graph<Integer> TEST2 = new SparseGraph(false, Arrays.asList(1,2,3,4,5,6,7),
                Arrays.asList(
                    new Integer[]{1,2},
                    new Integer[]{1,3},
                    new Integer[]{2,4},
                    new Integer[]{3,4},
                    new Integer[]{3,7},
                    new Integer[]{4,5},
                    new Integer[]{5,6} ));
        // 1--2
        // |  |
        // 3--4--5--6
        // |
        // 7
    static CooperationMetric INST = new CooperationMetric(new AdditiveSubsetMetric(new DegreeCentrality()));

    @Test
    public void testGetValue() {
        System.out.println("getValue");
        double[] result1 = INST.getValue(TEST2, new HashSet(Arrays.asList(1,2,3,4)));
        double[] result2 = INST.getValue(TEST2, new HashSet(Arrays.asList(1,4,5)));
        assertEquals(5, result1.length);
        assertEquals(5, result2.length);
        assertArrayEquals(new double[]{10,2,14,4,2}, result1, 1e-10);
        assertArrayEquals(new double[]{7,5,14,7,2}, result2, 1e-10);
    }
}