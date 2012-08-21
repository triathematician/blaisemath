/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.blaise.graph.metrics;

import java.util.Arrays;
import java.util.Map;
import org.blaise.graph.Graph;
import org.blaise.graph.SparseGraph;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class EigenCentralityTest {

    static Graph<Integer> TEST2;
    static EigenCentrality INST;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- EigenCentralityTest --");
        INST = new EigenCentrality();
        TEST2 = new SparseGraph(false, Arrays.asList(1,2,3,4,5,6),
                Arrays.asList(
                    new Integer[]{1,2},
                    new Integer[]{1,3},
                    new Integer[]{2,3},
                    new Integer[]{2,6},
                    new Integer[]{3,4},
                    new Integer[]{4,5} ));
        //   1
        //  / \
        // 2---3--4--5
        // |
        // 6
        //
    }

    @Test
    public void testValue() {
        System.out.println("value");
        assertEquals(.475349771, INST.value(TEST2, 1), 1e-8);
        assertEquals(.564129165, INST.value(TEST2, 3), 1e-8);
        assertEquals(.296008301, INST.value(TEST2, 4), 1e-8);
    }
    
    @Test
    public void testAllValues() {
        System.out.println("allValues");
        Map<Integer,Double> vals = INST.allValues(TEST2);
        assertEquals(6, vals.size());
        for (int i = 0; i < 6; i++)
            assertEquals(INST.value(TEST2, i+1), vals.get(i+1));
    }

}