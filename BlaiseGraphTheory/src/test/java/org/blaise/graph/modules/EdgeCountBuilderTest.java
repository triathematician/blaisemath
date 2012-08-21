/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.blaise.graph.modules;

import org.blaise.graph.Graph;
import org.blaise.graph.GraphUtils;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class EdgeCountBuilderTest {

    @Test
    public void testEdgeCountBuilder() {
        System.out.println("EdgeCountBuilderTest: MANUALLY CHECK FOR DESIRED OUTPUT");
        Graph<Integer> result1 = new EdgeCountBuilder(false, 10, 0).createGraph();
        assertEquals(10, result1.nodeCount()); assertEquals(0, result1.edgeCount());
        result1 = new EdgeCountBuilder(false, 10, 30).createGraph();
        assertEquals(10, result1.nodeCount()); assertEquals(30, result1.edgeCount());
        System.out.println("  UNDIRECTED: " + GraphUtils.printGraph(result1));
        result1 = new EdgeCountBuilder(true, 10, 30).createGraph();
        assertEquals(10, result1.nodeCount()); assertEquals(30, result1.edgeCount());
        System.out.println("  DIRECTED: " + GraphUtils.printGraph(result1));
    }

}