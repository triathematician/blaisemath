/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph.io;

import java.util.HashMap;
import org.bm.blaise.scio.graph.LongitudinalGraph;
import org.bm.blaise.scio.graph.io.AbstractGraphIO.GraphType;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author elisha
 */
public class LongitudinalGraphIOTest {

    static LongitudinalGraph<Integer> SAMPLE_NEWFRAT;

    public static LongitudinalGraph<Integer> sampleNewFrat() {
        if (SAMPLE_NEWFRAT == null)
            SAMPLE_NEWFRAT = (LongitudinalGraph<Integer>) LongitudinalGraphIO.getInstance().importGraph(
                new HashMap<Integer,double[]>(),
                LongitudinalGraphIO.class.getResource("data/newfrat.netx"), GraphType.LONGITUDINAL);
        return SAMPLE_NEWFRAT;
    }

    /**
     * Test of readIntegerGraphFile method, of class SimpleGraphIO.
     */
    @Test
    public void testImportGraph() {
        System.out.println("-- LongitudinalGraphIOTest --");
        System.out.println("importGraph: MANUALLY CHECK FOR DESIRED OUTPUT");
        assertEquals(15, sampleNewFrat().getTimes().size());
        assertEquals(0.0, sampleNewFrat().getMinimumTime(), 1e-10);
        assertEquals(15.0, sampleNewFrat().getMaximumTime(), 1e-10);
        System.out.println(sampleNewFrat());
    }
    
}