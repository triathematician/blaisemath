/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.io.AbstractGraphIO.GraphType;
import org.bm.blaise.scio.graph.io.UCINetGraphIO.DataFormat;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author elisha
 */
public class DynetMLGraphIOTest {

    static Graph<Integer> SAMPLE;
    static DynetMLGraphIO io = DynetMLGraphIO.getInstance();

    public static Graph<Integer> sample() {
        if (SAMPLE == null) SAMPLE = (Graph<Integer>) io.importGraph(new HashMap<Integer,double[]>(),
                DynetMLGraphIO.class.getResource("data/sample.dynetml"), GraphType.REGULAR);
        return SAMPLE; }

    @Test
    public void testGetInstance() {
        System.out.println("getInstance -- no test");
    }

    @Test
    public void testImportGraph() {
        System.out.println("importGraph");
        Graph<Integer> graph;

        graph = sample();
        System.out.println(graph);
        assertTrue(graph.isDirected());
        assertEquals(20, graph.order());
        assertEquals(38, graph.edgeNumber());
        assertTrue(graph.adjacent(0,12));
        assertFalse(graph.adjacent(0,5));
        assertTrue(graph.adjacent(8,9));
    }

}