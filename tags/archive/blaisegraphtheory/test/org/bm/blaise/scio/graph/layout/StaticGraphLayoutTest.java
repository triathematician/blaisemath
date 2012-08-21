/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph.layout;

import java.awt.geom.Point2D.Double;
import java.util.Map;
import org.bm.blaise.scio.graph.Graph;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author elisha
 */
public class StaticGraphLayoutTest {

    public StaticGraphLayoutTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testLayout() {
        System.out.println("layout");
        Graph g = null;
        double[] parameters = null;
        StaticGraphLayout instance = new StaticGraphLayoutImpl();
        Map expResult = null;
        Map result = instance.layout(g, parameters);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    public class StaticGraphLayoutImpl implements StaticGraphLayout {

        public Map<Object, Double> layout(Graph g, double[] parameters) {
            return null;
        }
    }

}