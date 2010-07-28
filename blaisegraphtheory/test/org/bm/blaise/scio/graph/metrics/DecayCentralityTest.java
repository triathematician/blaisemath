/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph.metrics;

import java.util.List;
import java.util.Map;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.io.PajekGraphIOTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author elisha
 */
public class DecayCentralityTest {

    static Graph<Integer> SAMPLE_PADGETT, SAMPLE_AIRPORTS;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- DecayCentralityTest --");
        SAMPLE_PADGETT = PajekGraphIOTest.sampleXPadgett();
        SAMPLE_AIRPORTS = PajekGraphIOTest.sampleXAirport();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testGetParameter() {
        System.out.println("getParameter");
        DecayCentrality instance = new DecayCentrality(0.1);
        assertEquals(0.1, instance.getParameter(), 0.0);
        assertEquals(instance.parameter, instance.getParameter(), 0.0);
    }

    @Test
    public void testSetParameter() {
        System.out.println("setParameter");
        DecayCentrality instance = new DecayCentrality();
        instance.setParameter(0.1);
        assertEquals(0.1, instance.parameter, 0.0);
    }

    @Test
    public void testGetValue() {
        System.out.println("getValue");
        assertEquals(0, new DecayCentrality(0).value(SAMPLE_PADGETT, 1), 1e-10);
        assertEquals(14, new DecayCentrality(1).value(SAMPLE_PADGETT, 1), 1e-10);
    }

    @Test
    public void testGetAllValues() {
        System.out.println("getAllValues");
        Double[] eDeg = new Double[] {2.5625, 3.75, 3.3125, 3.28125, 3.0625, 2.125, 3.8125, 2.15625, 4.625, 1.71875, 2.96875, 0.0, 3.875, 2.9375, 3.625, 3.75};
        List<Double> vals = new DecayCentrality(0.5).allValues(SAMPLE_PADGETT);
        for (int i = 0; i < eDeg.length; i++)
            assertEquals(eDeg[i], vals.get(i));
        assertEquals("{0.0=1, 1.71875=1, 2.125=1, 2.15625=1, 2.5625=1, 2.9375=1, 2.96875=1, 3.0625=1, 3.28125=1, 3.3125=1, 3.625=1, 3.75=2, 3.8125=1, 3.875=1, 4.625=1}",
                GraphMetrics.computeDistribution(SAMPLE_PADGETT, new DecayCentrality(0.5)).toString());
    }

}