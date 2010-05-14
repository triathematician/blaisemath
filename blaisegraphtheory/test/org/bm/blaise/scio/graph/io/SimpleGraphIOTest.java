/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph.io;

import org.bm.blaise.scio.graph.SimpleGraph;
import org.junit.Test;

/**
 *
 * @author ae3263
 */
public class SimpleGraphIOTest {

    static SimpleGraph SAMPLE;

    public static SimpleGraph sample() {
        if (SAMPLE == null)
            SAMPLE = SimpleGraphIO.importSimpleGraph(
                SimpleGraphIO.class.getResource("data/padgett.txt"));
        return SAMPLE;
    }

    /**
     * Test of readIntegerGraphFile method, of class SimpleGraphIO.
     */
    @Test
    public void testImportSimpleGraph() {
        System.out.println("importSimpleGraph");
        System.out.println(sample());        
    }

}