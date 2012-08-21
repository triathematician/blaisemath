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
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class AdditiveSubsetMetricTest {

    static Graph<Integer> TEST2;
    static AdditiveSubsetMetric INST;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- AdditiveSubsetMetricTest --");
        INST = new AdditiveSubsetMetric(new DegreeCentrality());
        TEST2 = new SparseGraph(false, Arrays.asList(1,2,3,4,5,6,7),
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
    }

    @Test
    public void testGetValue() {
        System.out.println("getValue");
        assertEquals(10, INST.getValue(TEST2, new HashSet(Arrays.asList(1,2,3,4))));
        assertEquals(6, INST.getValue(TEST2, new HashSet(Arrays.asList(4,5,6))));
    }

}