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
public class EigenCentralityTest {

    static Graph<Integer> SAMPLE_PADGETT;
    static Graph<Integer> TEST2;
    static EigenCentrality INST;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- EigenCentralityTest --");
        INST = EigenCentrality.getInstance();
        SAMPLE_PADGETT = PajekGraphIOTest.sampleXPadgett();
        TEST2 = GraphFactory.getGraph(false, Arrays.asList(1,2,3,4,5,6),
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
        List<Double> vals = INST.allValues(TEST2);
        assertEquals(6, vals.size());
        for (int i = 0; i < 6; i++)
            assertEquals(INST.value(TEST2, i+1), vals.get(i));
    }

    @Test
    public void testToString() {
        System.out.println("toString & getInstance");
        assertEquals("Eigenvalue Centrality (approx)", INST.toString());
    }

    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        assertTrue(INST instanceof EigenCentrality);
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
        assertEquals(1.0, INST.nodeMax(true, 10), 0);
        assertEquals(1.0, INST.nodeMax(false, 10000), 0);
        assertEquals(1.0, INST.nodeMax(true, 1898970), 0);
    }

    @Test
    public void testCentralMax() {
        System.out.println("centralMax");
        assertTrue(Double.isNaN(INST.centralMax(true, 10)));
        assertTrue(Double.isNaN(INST.centralMax(false, 1000)));
    }

}