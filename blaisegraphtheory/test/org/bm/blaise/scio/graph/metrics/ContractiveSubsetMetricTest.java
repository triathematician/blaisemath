/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph.metrics;

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
public class ContractiveSubsetMetricTest {

    static Graph<Integer> TEST2;
    static ContractiveSubsetMetric INST;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- AdditiveSubsetMetricTest --");
        INST = new ContractiveSubsetMetric(GraphMetrics.DEGREE);
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
        assertEquals(4, INST.getValue(TEST2, Arrays.asList(1,2,3,4))); // 4 not 2 because of the presence of the loop
        assertEquals(4, INST.getValue(TEST2, Arrays.asList(4,5,6))); // 4 not 2 because of the presence of the loop
    }
}