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

    static SimpleGraph SAMPLE1, SAMPLE2;

    public static SimpleGraph sample1() {
        if (SAMPLE1 == null)
            SAMPLE1 = SimpleGraphIO.importSimpleGraph(
                SimpleGraphIO.class.getResource("data/padgett.txt"));
        return SAMPLE1;
    }

    public static SimpleGraph sample2() {
        if (SAMPLE2 == null)
            SAMPLE2 = SimpleGraphIO.importSimpleGraph(
                SimpleGraphIO.class.getResource("data/InternetISP.txt"));
        return SAMPLE2;
    }

    /**
     * Test of readIntegerGraphFile method, of class SimpleGraphIO.
     */
    @Test
    public void testImportSimpleGraph() {
        System.out.println("importSimpleGraph");
        System.out.println(sample1());
    }

}