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
public class EdgeProbabilityBuilderTest {

    @Test
    public void testEdgeProbailityBuilder() {
        System.out.println("EdgeProbabilityBuilder: MANUALLY CHECK FOR DESIRED OUTPUT");
        Graph<Integer> result1 = new EdgeProbabilityBuilder(false, 10, 0f).createGraph();
        assertEquals(10, result1.nodeCount()); assertEquals(0, result1.edgeCount());
        result1 = new EdgeProbabilityBuilder(true, 10, 1f).createGraph();
        assertEquals(10, result1.nodeCount()); assertEquals(100, result1.edgeCount());
        result1 = new EdgeProbabilityBuilder(false, 10, 1f).createGraph();
        assertEquals(10, result1.nodeCount()); assertEquals(45, result1.edgeCount());
        result1 = new EdgeProbabilityBuilder(false, 10, .25f).createGraph();
        System.out.println("  UNDIRECTED (.25 probability): " + result1.edgeCount() + " edges, " + GraphUtils.printGraph(result1));
        result1 = new EdgeProbabilityBuilder(true, 10, .25f).createGraph();
        System.out.println("  DIRECTED (.25 probability): " + result1.edgeCount() + " edges, " + GraphUtils.printGraph(result1));
    }

}