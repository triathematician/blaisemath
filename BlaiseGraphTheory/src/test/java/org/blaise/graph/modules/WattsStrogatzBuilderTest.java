/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.blaise.graph.modules;

import org.blaise.graph.Graph;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class WattsStrogatzBuilderTest {
    
    @Test
    public void testWattsStrogatzBuilder() {
        System.out.println("-- WattsStrogatzRandomGraphTest --");
        System.out.println("getInstance: MANUALLY CHECK FOR DESIRED OUTPUT");
        Graph<Integer> result1 = new WattsStrogatzBuilder(false, 10, 2, 0f).createGraph();
        assertEquals(10, result1.nodeCount());
        assertEquals(10, result1.edgeCount());
        for (int i = 0; i < 10; i++) {
            assertTrue(result1.adjacent(i, (i + 1) % 10));
        }
        Graph<Integer> result2 = new WattsStrogatzBuilder(false, 50, 4, .5f).createGraph();
        System.out.println(result2);
        assertEquals(50, result2.nodeCount());
        assertEquals(100, result2.edgeCount());
    }
    
}