/*
 * Copyright 2014 Elisha.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googlecode.blaisemath.util;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2014 - 2016 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Elisha
 */
public class EdgeTest {
    
    @Test
    public void testConstruct() {
        System.out.println("construct");
        Edge e = new Edge(1,2);
        try {
            Edge e2 = new Edge(1, null);
            fail();
        } catch (NullPointerException x) {
            // expected
        }
        try {
            Edge e3 = new Edge(null, 1);
            fail();
        } catch (NullPointerException x) {
            // expected
        }
    }

    /**
     * Test of toString method, of class Edge.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Edge<Integer> inst = new Edge<Integer>(1,2);
        assertEquals("1:2", inst.toString());
    }

    /**
     * Test of getNode1 method, of class Edge.
     */
    @Test
    public void testGetNode1() {
        System.out.println("getNode1");
        Edge<Integer> inst = new Edge<Integer>(1,2);
        assertEquals(1, (int) inst.getNode1());
    }

    /**
     * Test of getNode2 method, of class Edge.
     */
    @Test
    public void testGetNode2() {
        System.out.println("getNode2");
        Edge<Integer> inst = new Edge<Integer>(1,2);
        assertEquals(2, (int) inst.getNode2());
    }

    /**
     * Test of adjacent method, of class Edge.
     */
    @Test
    public void testAdjacent() {
        System.out.println("adjacent");
        Edge<Integer> inst = new Edge<Integer>(1,2);
        assertTrue(inst.adjacent(1));
        assertTrue(inst.adjacent(2));
        assertFalse(inst.adjacent(3));
    }

    /**
     * Test of opposite method, of class Edge.
     */
    @Test
    public void testOpposite() {
        System.out.println("opposite");
        Edge<Integer> inst = new Edge<Integer>(1,2);
        assertEquals(1, (int) inst.opposite(2));
        assertEquals(2, (int) inst.opposite(1));
        
        Edge<Integer> inst2 = new Edge<Integer>(1,1);
        assertEquals(1, (int) inst2.opposite(1));
    }

    /**
     * Test of equals method, of class Edge.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Edge<Integer> e1 = new Edge<Integer>(1,2);
        Edge<Integer> e1b = new Edge<Integer>(1,2);
        Edge<Integer> e2 = new Edge<Integer>(2,1);
        Edge<Integer> e3 = new Edge<Integer>(1,3);
        Edge<Integer> e4 = new Edge<Integer>(1,1);
        Edge<String> e5 = new Edge<String>("","");
        assertTrue(e1.equals(e1b));
        assertFalse(e1.equals(e2));
        assertFalse(e1.equals(e3));
        assertFalse(e1.equals(e4));
        assertFalse(e1.equals(e5));
    }

    /**
     * Test of hashCode method, of class Edge.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        Edge<Integer> e1 = new Edge<Integer>(1,2);
        Edge<Integer> e1b = new Edge<Integer>(1,2);
        assertTrue(e1.hashCode() == e1b.hashCode());
    }
    
}
