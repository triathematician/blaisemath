/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph.io;

import org.bm.blaise.scio.graph.Graph;
import org.junit.Test;

/**
 *
 * @author ae3263
 */
public class SimpleGraphIOTest {

    static Graph<Integer> SAMPLE_PADGETT, SAMPLE_AIRPORTS;

    public static Graph<Integer> samplePadgett() {
        if (SAMPLE_PADGETT == null)
            SAMPLE_PADGETT = SimpleGraphIO.importGraph(
                SimpleGraphIO.class.getResource("data/padgett.txt"));
        return SAMPLE_PADGETT;
    }

    public static Graph<Integer> sampleAirports() {
        if (SAMPLE_AIRPORTS == null)
            SAMPLE_AIRPORTS = SimpleGraphIO.importGraph(
                SimpleGraphIO.class.getResource("data/USAirport500.txt"));
        return SAMPLE_AIRPORTS;
    }

    /**
     * Test of readIntegerGraphFile method, of class SimpleGraphIO.
     */
    @Test
    public void testImportSimpleGraph() {
        System.out.println("-- SimpleGraphIOTest --");
        System.out.println("importSimpleGraph: MANUALLY CHECK FOR DESIRED OUTPUT");
        System.out.println(samplePadgett());
    }

}