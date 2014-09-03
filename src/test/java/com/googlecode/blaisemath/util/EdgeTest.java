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
 * Copyright (C) 2009 - 2014 Elisha Peterson
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
        new Edge(1,2);
        try {
            new Edge(1, null);
            fail();
        } catch (IllegalArgumentException x) {
            // expected
        }
        try {
            new Edge(null, 1);
            fail();
        } catch (IllegalArgumentException x) {
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
    
}
