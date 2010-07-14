/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph;

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author elisha
 */
public class GraphFactoryTest {


    @Test
    public void testGetGraph() {
        System.out.println("getInstance");
        String[][] edges = {{"A","B"},{"B","C"},{"C","D"},{"D","A"},{"E","E"}};
        Graph<String> result1 = GraphFactory.getGraph(false, Arrays.asList("A","B","C","D","E"), Arrays.asList(edges));
        Graph<String> result2 = GraphFactory.getGraph(true, Arrays.asList("A","B","C","D","E"), Arrays.asList(edges));
        assertEquals("NODES: [A, B, C, D, E]  EDGES: A: [B, D] B: [A, C] C: [B, D] D: [A, C] E: [E]", Graphs.printGraph(result1));
        assertEquals("NODES: [A, B, C, D, E]  EDGES: A: [B] B: [C] C: [D] D: [A] E: [E]", Graphs.printGraph(result2));
    }

    @Test
    public void testGetEmptyGraphInstance() {
        System.out.println("getEmptyGraphInstance");
        assertEquals("0 0 0 0 \n0 0 0 0 \n0 0 0 0 \n0 0 0 0 \n", GraphFactory.getEmptyGraph(4, false).toString());
        assertEquals("0 0 0 0 \n0 0 0 0 \n0 0 0 0 \n0 0 0 0 \n", GraphFactory.getEmptyGraph(4, true).toString());
        Graph result = GraphFactory.getEmptyGraph(10, true);
        assertEquals(10, result.order());
        assertEquals(0, result.edgeNumber());
        for (int i = 0; i < 10; i++)
            assertTrue(result.contains(i));
    }

    @Test
    public void testGetCompleteGraphInstance() {
        System.out.println("getCompleteGraphInstance");
        assertEquals("0 1 1 1 \n1 0 1 1 \n1 1 0 1 \n1 1 1 0 \n", GraphFactory.getCompleteGraph(4, false).toString());
        assertEquals("1 1 1 1 \n1 1 1 1 \n1 1 1 1 \n1 1 1 1 \n", GraphFactory.getCompleteGraph(4, true).toString());
        Graph result = GraphFactory.getCompleteGraph(6, false);
        Graph result2 = GraphFactory.getCompleteGraph(6, true);
        assertEquals(6, result.order());
        assertEquals(6, result2.order());
        assertEquals(15, result.edgeNumber());
        assertEquals(36, result2.edgeNumber());
        for (int i = 0; i < 6; i++) {
            assertTrue(result.contains(i));
            assertTrue(result2.contains(i));
        }
    }

    @Test
    public void testGetCycleGraphInstance() {
        System.out.println("getCycleGraphInstance");
        assertEquals("0 1 0 1 \n1 0 1 0 \n0 1 0 1 \n1 0 1 0 \n", GraphFactory.getCycleGraph(4, false).toString());
        assertEquals("0 1 0 0 \n0 0 1 0 \n0 0 0 1 \n1 0 0 0 \n", GraphFactory.getCycleGraph(4, true).toString());
    }

    @Test
    public void testGetStarGraphInstance() {
        System.out.println("getStarGraphInstance");
        assertEquals("0 1 1 1 \n1 0 0 0 \n1 0 0 0 \n1 0 0 0 \n", GraphFactory.getStarGraph(4).toString());
    }

    @Test
    public void testGetWheelGraphInstance() {
        System.out.println("getWheelGraphInstance");
        assertEquals("0 1 1 1 1 \n1 0 1 0 1 \n1 1 0 1 0 \n1 0 1 0 1 \n1 1 0 1 0 \n", GraphFactory.getWheelGraph(5).toString());
    }
}