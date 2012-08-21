/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.blaise.graph.metrics;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class DecayCentralityTest {

    static DecayCentrality INSTANCE0, INSTANCE5, INSTANCE1;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- DecayCentralityTest --");
        INSTANCE0 = new DecayCentrality(0);
        INSTANCE5 = new DecayCentrality(0.5);
        INSTANCE1 = new DecayCentrality(1);
    }

    @Test
    public void testGetParameter_setParameter() {
        System.out.println("getParameter/setParameter");
        DecayCentrality instance = new DecayCentrality(0.1);
        assertEquals(0.1, instance.getParameter(), 0.0);
        assertEquals(instance.parameter, instance.getParameter(), 0.0);
        instance.setParameter(0.2);
        assertEquals(0.2, instance.parameter, 0.0);
        try { instance.setParameter(1.2); fail("Illegal Parameter"); } catch (IllegalArgumentException ex) {}
        try { instance.setParameter(-.2); fail("Illegal Parameter"); } catch (IllegalArgumentException ex) {}
    }

}