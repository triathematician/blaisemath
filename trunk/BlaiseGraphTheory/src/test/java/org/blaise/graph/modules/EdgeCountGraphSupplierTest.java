/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.blaise.graph.modules;

import org.blaise.graph.Graph;
import org.blaise.graph.GraphUtils;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class EdgeCountGraphSupplierTest {

    @Test
    public void testEdgeCountBuilder() {
        System.out.println("EdgeCountBuilderTest: MANUALLY CHECK FOR DESIRED OUTPUT");
        Graph<Integer> result1 = new EdgeCountGraphSupplier(false, 10, 0).get();
        assertEquals(10, result1.nodeCount()); assertEquals(0, result1.edgeCount());
        result1 = new EdgeCountGraphSupplier(false, 10, 30).get();
        assertEquals(10, result1.nodeCount()); assertEquals(30, result1.edgeCount());
        System.out.println("  UNDIRECTED: " + GraphUtils.printGraph(result1));
        result1 = new EdgeCountGraphSupplier(true, 10, 30).get();
        assertEquals(10, result1.nodeCount()); assertEquals(30, result1.edgeCount());
        System.out.println("  DIRECTED: " + GraphUtils.printGraph(result1));
    }

}