/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.blaise.graph.metrics;

import java.util.Arrays;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ae3263
 */
public class GraphMetricsTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- GraphMetricsTest --");
    }

    @Test
    public void testDistribution() {
        System.out.println("distribution");
        Map result1 = GraphMetrics.distribution(Arrays.asList(0,3,45,56,5,1,2,4,45,5,21,3,3,2,1,1,2,34,4,4,3,2,2));
        assertEquals("{0=1, 1=3, 2=5, 3=4, 4=3, 5=2, 21=1, 34=1, 45=2, 56=1}", result1.toString());
    }

}