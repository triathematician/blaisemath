/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph.metrics;

import java.util.List;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.io.PajekGraphIOTest;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author elisha
 */
public class DecayCentralityTest {

    static Graph<Integer> SAMPLE_PADGETT, SAMPLE_AIRPORTS;
    static DecayCentrality INSTANCE0, INSTANCE5, INSTANCE1;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- DecayCentralityTest --");
        SAMPLE_PADGETT = PajekGraphIOTest.sampleXPadgett();
        SAMPLE_AIRPORTS = PajekGraphIOTest.sampleXAirport();
        INSTANCE0 = DecayCentrality.getInstance(0);
        INSTANCE5 = DecayCentrality.getInstance(0.5);
        INSTANCE1 = DecayCentrality.getInstance(1);
    }

    @Test
    public void testGetParameter_setParameter() {
        System.out.println("getParameter/setParameter");
        DecayCentrality instance = new DecayCentrality(0.1);
        assertEquals(0.1, instance.getParameter(), 0.0);
        assertEquals(instance.parameter, instance.getParameter(), 0.0);
        instance.setParameter(0.2);
        assertEquals(0.2, instance.parameter, 0.0);
        try { instance.setParameter(1.2); fail("Illegal Parameter"); } catch (IllegalArgumentException ex) {}
        try { instance.setParameter(-.2); fail("Illegal Parameter"); } catch (IllegalArgumentException ex) {}
    }

    @Test
    public void testValue() {
        System.out.println("value");
        assertEquals(0, INSTANCE0.value(SAMPLE_PADGETT, 1), 1e-10);
        assertEquals(14, INSTANCE1.value(SAMPLE_PADGETT, 1), 1e-10);
    }

    @Test
    public void testAllValues() {
        System.out.println("allValues");
        Double[] expected = new Double[] {2.5625, 3.75, 3.3125, 3.28125, 3.0625, 2.125, 3.8125, 2.15625, 4.625, 1.71875, 2.96875, 0.0, 3.875, 2.9375, 3.625, 3.75};
        List<Double> vals = INSTANCE5.allValues(SAMPLE_PADGETT);
        for (int i = 0; i < expected.length; i++)
            assertEquals(expected[i], vals.get(i));
        assertEquals("{0.0=1, 1.71875=1, 2.125=1, 2.15625=1, 2.5625=1, 2.9375=1, 2.96875=1, 3.0625=1, 3.28125=1, 3.3125=1, 3.625=1, 3.75=2, 3.8125=1, 3.875=1, 4.625=1}",
                GraphMetrics.computeDistribution(SAMPLE_PADGETT, INSTANCE5).toString());
    }

    @Test
    public void testToString() {
        System.out.println("toString");
        assertEquals("Decay Centrality (0.5)", INSTANCE5.toString());
    }

    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        assertTrue(INSTANCE5 instanceof DecayCentrality);
    }


    @Test
    public void testSupportsGraph() {
        System.out.println("supportsGraph");
        assertTrue(INSTANCE5.supportsGraph(true));
        assertTrue(INSTANCE1.supportsGraph(false));
    }

    @Test
    public void testNodeMax() {
        System.out.println("nodeMax");
        assertEquals(0.0, INSTANCE0.nodeMax(true, 10), 0);
        assertEquals(.5*9999.0, INSTANCE5.nodeMax(false, 10000), 0);
        assertEquals(1898969.0, INSTANCE1.nodeMax(true, 1898970), 0);
    }

    @Test
    public void testCentralMax() {
        System.out.println("centralMax");
        assertTrue(Double.isNaN(INSTANCE0.centralMax(true, 10)));
        assertTrue(Double.isNaN(INSTANCE5.centralMax(false, 1000)));
        assertTrue(Double.isNaN(INSTANCE1.centralMax(true, 2939484)));
    }

}