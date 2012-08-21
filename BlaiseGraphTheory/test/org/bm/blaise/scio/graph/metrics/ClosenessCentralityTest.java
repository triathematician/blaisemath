/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph.metrics;

import java.util.Arrays;
import java.util.List;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.GraphFactory;
import org.bm.blaise.scio.graph.io.PajekGraphIOTest;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author elisha
 */
public class ClosenessCentralityTest {

    static Graph<Integer> SAMPLE_PADGETT;
    static Graph<Integer> TEST2;
    static ClosenessCentrality INST1, INST2;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- ClosenessCentralityTest --");
        SAMPLE_PADGETT = PajekGraphIOTest.sampleXPadgett();
        TEST2 = GraphFactory.getGraph(false, Arrays.asList(1,2,3,4,5,6,7),
                Arrays.asList(
                    new Integer[]{1,2},
                    new Integer[]{1,3},
                    new Integer[]{2,4},
                    new Integer[]{3,4},
                    new Integer[]{3,7},
                    new Integer[]{4,5},
                    new Integer[]{5,6} ));
        INST1 = ClosenessCentrality.getInstance();
        INST2 = ClosenessCentrality.getMaxInstance();
        // 1--2
        // |  |
        // 3--4--5--6
        // |
        // 7
    }

    @Test
    public void testValue() {
        System.out.println("value");
        assertEquals(6.0/13, INST1.value(TEST2, 1), 1e-10);
        assertEquals(1.0/2, INST2.value(TEST2, 4), 1e-10);
    }

    @Test
    public void testAllValues() {
        System.out.println("allValues");
        List<Double> vals = INST1.allValues(TEST2);
        assertEquals(7, vals.size());
        for (int i = 0; i < 7; i++)
            assertEquals(INST1.value(TEST2, i+1), vals.get(i), 1e-10);
        List<Double> vals2 = INST2.allValues(TEST2);
        assertEquals(7, vals2.size());
        for (int i = 0; i < 7; i++)
            assertEquals(INST2.value(TEST2, i+1), vals2.get(i), 1e-10);
    }

    @Test
    public void testToString() {
        System.out.println("toString");
        assertEquals("Closeness Centrality", INST1.toString());
        assertEquals("Graph Centrality", INST2.toString());
    }

    @Test
    public void testGetInstance_getMaxInstance() {
        System.out.println("getInstance/getMaxInstance");
        assertTrue(INST1 instanceof ClosenessCentrality);
        assertTrue(INST1.useSum);
        assertTrue(INST2 instanceof ClosenessCentrality);
        assertFalse(INST2.useSum);
    }

    @Test
    public void testSupportsGraph() {
        System.out.println("supportsGraph");
        assertTrue(INST1.supportsGraph(true));
        assertTrue(INST1.supportsGraph(false));
        assertTrue(INST2.supportsGraph(true));
        assertTrue(INST2.supportsGraph(false));
    }

    @Test
    public void testNodeMax() {
        System.out.println("nodeMax");
        assertEquals(1/9.0, INST1.nodeMax(true, 10), 1e-10);
        assertEquals(1/19.0, INST1.nodeMax(false, 20), 1e-10);
        assertEquals(1.0, INST2.nodeMax(true, 10), 1e-10);
        assertEquals(1.0, INST2.nodeMax(false, 20), 1e-10);
    }

    @Test
    public void testCentralMax() {
        System.out.println("centralMax");
        assertTrue(Double.isNaN(INST1.centralMax(true, 10)));
        assertTrue(Double.isNaN(INST1.centralMax(false, 10)));
        assertTrue(Double.isNaN(INST2.centralMax(true, 10)));
        assertTrue(Double.isNaN(INST2.centralMax(false, 10)));
    }

//    @Test
//    public void testProfile() {
//        System.out.println(" (profile test) ");
//        INST1.allValues(RandomGraph.getInstance(200, 200, false));
//    }

}