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
public class BetweenCentralityTest {

    static Graph<Integer> SAMPLE_PADGETT;
    static Graph<Integer> TEST2;
    static BetweenCentrality INST;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- BetweenCentralityTest --");
        INST = BetweenCentrality.getInstance();
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
        // 1--2
        // |  |
        // 3--4--5--6
        // |
        // 7
    }

    @Test
    public void testValue() {
        System.out.println("value");
        assertEquals(1.0, INST.value(TEST2, 1), 1e-10);
        assertEquals(9.0, INST.value(TEST2, 4), 1e-10);

//        System.out.println(" -- running time for #edges-based random graphs -- ");
//        System.out.println(" .. directed .. ");
//        for (int mult : new int[]{1, 2, 5, 10, 20}) {
//            System.out.println(" :: m=" + mult + "*n :: ");
//            for (int n : new int[]{50, 100, 200, 500, 1000, 5000, 10000})
//                bc.allValues(RandomGraph.getInstance(n, n, true));
//        }
//        System.out.println(" .. undirected .. ");
//        for (int mult : new int[]{1, 2, 5, 10, 20}) {
//            System.out.println(" :: m=" + mult + "*n :: ");
//            for (int n : new int[]{50, 100, 200, 500, 1000, 5000, 10000})
//                bc.allValues(RandomGraph.getInstance(n, n, false));
//        }
//
//        System.out.println(" -- running time for density-based random graphs -- ");
//        System.out.println(" .. directed .. ");
//        for (float p : new float[]{.1f, .25f, .5f, .75f, .9f}) {
//            System.out.println(" :: p=" + p + " ::");
//            for (int n : new int[]{50, 100, 200, 500, 1000, 5000, 10000})
//                bc.allValues(RandomGraph.getInstance(n, p, true));
//        }
//        System.out.println(" .. undirected .. ");
//        for (float p : new float[]{.1f, .25f, .5f, .75f, .9f}) {
//            System.out.println(" :: p=" + p + " ::");
//            for (int n : new int[]{50, 100, 200, 500, 1000, 5000, 10000})
//                bc.allValues(RandomGraph.getInstance(n, p, false));
//        }
    }

    @Test
    public void testAllValues() {
        System.out.println("allValues");
        List<Double> vals = INST.allValues(TEST2);
        assertEquals(7, vals.size());
        for (int i = 0; i < 7; i++)
            assertEquals(INST.value(TEST2, i+1), vals.get(i), 1e-10);
    }

    @Test
    public void testToString() {
        System.out.println("toString");
        assertEquals("Betweenness Centrality", INST.toString());
    }

    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        assertTrue(INST instanceof BetweenCentrality);
    }

    @Test
    public void testSupportsGraph() {
        System.out.println("supportsGraph");
        assertTrue(INST.supportsGraph(true));
        assertTrue(INST.supportsGraph(false));
    }

    @Test
    public void testNodeMax() {
        System.out.println("nodeMax");
        assertEquals(9.0*8.0, INST.nodeMax(true, 10), 1e-10);
        assertEquals(19.0*18.0/2.0, INST.nodeMax(false, 20), 1e-10);
    }

    @Test
    public void testCentralMax() {
        System.out.println("centralMax");
        assertTrue(Double.isNaN(INST.centralMax(true, 10)));
        assertTrue(Double.isNaN(INST.centralMax(false, 10)));
    }

}