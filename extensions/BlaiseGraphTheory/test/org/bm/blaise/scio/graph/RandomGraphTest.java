/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author elisha
 */
public class RandomGraphTest {

    @Test
    public void testGetInstance_probability() {
        System.out.println("getInstance (probability): MANUALLY CHECK FOR DESIRED OUTPUT");
        Graph<Integer> result1 = RandomGraph.getInstance(10, 0f, false);
        assertEquals(10, result1.order()); assertEquals(0, result1.edgeNumber());
        result1 = RandomGraph.getInstance(10, 1f, true);
        assertEquals(10, result1.order()); assertEquals(100, result1.edgeNumber());
        result1 = RandomGraph.getInstance(10, 1f, false);
        assertEquals(10, result1.order()); assertEquals(45, result1.edgeNumber());
        result1 = RandomGraph.getInstance(10, .25f, false);
        System.out.println("  UNDIRECTED (.25 probability): " + result1.edgeNumber() + " edges, " + GraphUtils.printGraph(result1));
        result1 = RandomGraph.getInstance(10, .25f, true);
        System.out.println("  DIRECTED (.25 probability): " + result1.edgeNumber() + " edges, " + GraphUtils.printGraph(result1));
    }

    @Test
    public void testGetInstance_number_of_edges() {
        System.out.println("getInstance (number of edges): MANUALLY CHECK FOR DESIRED OUTPUT");
        Graph<Integer> result1 = RandomGraph.getInstance(10, 0, false);
        assertEquals(10, result1.order()); assertEquals(0, result1.edgeNumber());
        result1 = RandomGraph.getInstance(10, 30, false);
        assertEquals(10, result1.order()); assertEquals(30, result1.edgeNumber());
        System.out.println("  UNDIRECTED: " + GraphUtils.printGraph(result1));
        result1 = RandomGraph.getInstance(10, 30, true);
        assertEquals(10, result1.order()); assertEquals(30, result1.edgeNumber());
        System.out.println("  DIRECTED: " + GraphUtils.printGraph(result1));
    }

}