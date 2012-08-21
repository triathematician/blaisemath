/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author elisha
 */
public class WattsStrogatzRandomGraphTest {

    public WattsStrogatzRandomGraphTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- WattsStrogatzRandomGraphTest --");
    }

    @Test
    public void testGetInstance() {
        System.out.println("getInstance: MANUALLY CHECK FOR DESIRED OUTPUT");
        Graph<Integer> result1 = WattsStrogatzRandomGraph.getInstance(10, 2, 0f);
        assertEquals(10, result1.order()); assertEquals(10, result1.edgeNumber());
        for (int i = 0; i < 10; i++) assertTrue(result1.adjacent(i, (i+1)%10));
        Graph<Integer> result2 = WattsStrogatzRandomGraph.getInstance(50, 4, .5f);
        System.out.println(result2);
        assertEquals(50, result2.order()); assertEquals(100, result2.edgeNumber());
    }

}