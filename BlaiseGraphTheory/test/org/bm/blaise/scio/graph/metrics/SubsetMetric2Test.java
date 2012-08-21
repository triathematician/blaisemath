/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph.metrics;

import org.bm.blaise.scio.graph.metrics.subset.SubsetMetric2;
import org.bm.blaise.scio.graph.metrics.subset.AdditiveSubsetMetric;
import java.util.Arrays;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.GraphFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author elisha
 */
public class SubsetMetric2Test {

    static Graph<Integer> TEST2;
    static SubsetMetric2 INST;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- AdditiveSubsetMetricTest --");
        INST = new SubsetMetric2(new AdditiveSubsetMetric(new DegreeCentrality()));
        TEST2 = GraphFactory.getGraph(false, Arrays.asList(1,2,3,4,5,6,7),
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
        assertEquals(6, INST.getValue(TEST2, Arrays.asList(1,2,3,4,5), Arrays.asList(1,2,3)));
        assertEquals(2, INST.getValue(TEST2, Arrays.asList(4,5,6), Arrays.asList(5)));
    }
}