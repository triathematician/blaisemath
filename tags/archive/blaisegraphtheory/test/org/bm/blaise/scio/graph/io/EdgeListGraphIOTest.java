/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph.io;

import java.util.HashMap;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.io.AbstractGraphIO.GraphType;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author elisha
 */
public class EdgeListGraphIOTest {

    static Graph<Integer> SAMPLE1;

    public static Graph<Integer> sample1() {
        if (SAMPLE1 == null) SAMPLE1 = (Graph<Integer>) EdgeListGraphIO.getInstance().importGraph(new HashMap<Integer,double[]>(),
                EdgeListGraphIO.class.getResource("data/edgeList.txt"), GraphType.REGULAR);
        return SAMPLE1; }

    @Test
    public void testGetInstance() {
        System.out.println("getInstance -- no test");
    }

    @Test
    public void testImportGraph() {
        System.out.println("-- EdgeListGraphIOTest --");
        Graph<Integer> g = sample1();
        assertTrue(g.isDirected());
        assertEquals(5, g.order());
        assertEquals(7, g.edgeNumber());
    }

}