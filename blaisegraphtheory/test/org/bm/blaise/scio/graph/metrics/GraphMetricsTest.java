/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph.metrics;

import org.bm.blaise.scio.graph.SimpleGraph;
import org.bm.blaise.scio.graph.io.SimpleGraphIOTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ae3263
 */
public class GraphMetricsTest {

    static SimpleGraph SAMPLE;

    public GraphMetricsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        SAMPLE = SimpleGraphIOTest.sample();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testDegreeMetric() {
        System.out.println("DEGREE");
        assertEquals("[1, 3, 2, 3, 3, 1, 4, 1, 6, 1, 3, 0, 3, 2, 4, 3]", GraphMetrics.computeValues(SAMPLE, GraphMetrics.DEGREE).toString());
        assertEquals("{0=1, 1=4, 2=2, 3=6, 4=2, 6=1}", GraphMetrics.computeDistribution(SAMPLE, GraphMetrics.DEGREE).toString());
    }

    @Test
    public void testCliqueMetric() {
        System.out.println("CLIQUE_COUNT");
        assertEquals("[0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 2, 0, 1, 0, 2, 1]", GraphMetrics.computeValues(SAMPLE, GraphMetrics.CLIQUE_COUNT).toString());
        assertEquals("{0=9, 1=5, 2=2}", GraphMetrics.computeDistribution(SAMPLE, GraphMetrics.CLIQUE_COUNT).toString());
    }

    @Test
    public void testDecayCentrality() {
        System.out.println("DECAY_CENTRALITY");
        assertEquals("[2.5625, 3.75, 3.3125, 3.28125, 3.0625, 2.125, 3.8125, 2.15625, 4.625, 1.71875, 2.96875, 0.0, 3.875, 2.9375, 3.625, 3.75]", GraphMetrics.computeValues(SAMPLE, new DecayCentrality(0.5)).toString());
        assertEquals("{0.0=1, 1.71875=1, 2.125=1, 2.15625=1, 2.5625=1, 2.9375=1, 2.96875=1, 3.0625=1, 3.28125=1, 3.3125=1, 3.625=1, 3.75=2, 3.8125=1, 3.875=1, 4.625=1}", GraphMetrics.computeDistribution(SAMPLE, new DecayCentrality(0.5)).toString());
    }

}