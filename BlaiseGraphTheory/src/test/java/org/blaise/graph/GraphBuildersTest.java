/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.blaise.graph;

import org.blaise.graph.GraphBuilders.CompleteGraphBuilder;
import org.blaise.graph.GraphBuilders.CycleGraphBuilder;
import org.blaise.graph.GraphBuilders.EmptyGraphBuilder;
import org.blaise.graph.GraphBuilders.StarGraphBuilder;
import org.blaise.graph.GraphBuilders.WheelGraphBuilder;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class GraphBuildersTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- GraphBuildersTest --");
    }

    @Test
    public void testGetEmptyGraphInstance() {
        System.out.println("getEmptyGraphInstance");
        assertEquals("NODES: [0, 1, 2, 3]  EDGES: 0: [] 1: [] 2: [] 3: []", GraphUtils.printGraph(new EmptyGraphBuilder(false,4).createGraph()));
        assertEquals("NODES: [0, 1, 2, 3]  EDGES: 0: [] 1: [] 2: [] 3: []", GraphUtils.printGraph(new EmptyGraphBuilder(true,4).createGraph()));
        Graph result = new EmptyGraphBuilder(true,10).createGraph();
        assertEquals(10, result.nodeCount());
        assertEquals(0, result.edgeCount());
        for (int i = 0; i < 10; i++)
            assertTrue(result.contains(i));
    }

    @Test
    public void testGetCompleteGraphInstance() {
        System.out.println("getCompleteGraphInstance");
        assertEquals("NODES: [0, 1, 2, 3]  EDGES: 0: [1, 2, 3] 1: [0, 2, 3] 2: [0, 1, 3] 3: [0, 1, 2]", GraphUtils.printGraph(new CompleteGraphBuilder(false,4).createGraph()));
        assertEquals("NODES: [0, 1, 2, 3]  EDGES: 0: [1, 2, 3] 1: [0, 2, 3] 2: [0, 1, 3] 3: [0, 1, 2]", GraphUtils.printGraph(new CompleteGraphBuilder(true,4).createGraph()));
        Graph result = new CompleteGraphBuilder(false,6).createGraph();
        Graph result2 = new CompleteGraphBuilder(true,6).createGraph();
        assertEquals(6, result.nodeCount());
        assertEquals(6, result2.nodeCount());
        assertEquals(15, result.edgeCount());
        assertEquals(30, result2.edgeCount());
        for (int i = 0; i < 6; i++) {
            assertTrue(result.contains(i));
            assertTrue(result2.contains(i));
        }
    }

    @Test
    public void testGetCycleGraphInstance() {
        System.out.println("getCycleGraphInstance");
        assertEquals("NODES: [0, 1, 2, 3]  EDGES: 0: [1, 3] 1: [0, 2] 2: [1, 3] 3: [0, 2]", GraphUtils.printGraph(new CycleGraphBuilder(false,4).createGraph()));
        assertEquals("NODES: [0, 1, 2, 3]  EDGES: 0: [1] 1: [2] 2: [3] 3: [0]", GraphUtils.printGraph(new CycleGraphBuilder(true,4).createGraph()));
    }

    @Test
    public void testGetStarGraphInstance() {
        System.out.println("getStarGraphInstance");
        assertEquals("NODES: [0, 1, 2, 3]  EDGES: 0: [1, 2, 3] 1: [0] 2: [0] 3: [0]", GraphUtils.printGraph(new StarGraphBuilder(false,4).createGraph()));
        assertEquals("NODES: [0, 1, 2, 3]  EDGES: 0: [1, 2, 3] 1: [] 2: [] 3: []", GraphUtils.printGraph(new StarGraphBuilder(true,4).createGraph()));
    }

    @Test
    public void testGetWheelGraphInstance() {
        System.out.println("getWheelGraphInstance");
        assertEquals("NODES: [0, 1, 2, 3, 4]  EDGES: 0: [1, 2, 3, 4] 1: [0, 2, 4] 2: [0, 1, 3] 3: [0, 2, 4] 4: [0, 1, 3]", GraphUtils.printGraph(new WheelGraphBuilder(false,5).createGraph()));
        assertEquals("NODES: [0, 1, 2, 3, 4]  EDGES: 0: [1, 2, 3, 4] 1: [2, 4] 2: [1, 3] 3: [2, 4] 4: [1, 3]", GraphUtils.printGraph(new WheelGraphBuilder(true,5).createGraph()));
    }
}