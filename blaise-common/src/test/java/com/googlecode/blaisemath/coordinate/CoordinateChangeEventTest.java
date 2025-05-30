package com.googlecode.blaisemath.coordinate;

/*
 * #%L
 * BlaiseCommon
 * --
 * Copyright (C) 2014 - 2025 Elisha Peterson
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


import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class CoordinateChangeEventTest {

    private Map add;
    private Set remove;
    private CoordinateChangeEvent addInst;
    private CoordinateChangeEvent removeInst;
    private CoordinateChangeEvent bothInst;
    
    @Before
    public void setUp() {
        add = Collections.singletonMap("Object", new Point());
        remove = Collections.singleton("Object2");
        addInst = CoordinateChangeEvent.createAddEvent("Test", add);
        removeInst = CoordinateChangeEvent.createRemoveEvent("Test", remove);
        bothInst = CoordinateChangeEvent.createAddRemoveEvent("Test", add, remove);
    }
    
    @Test
    public void testCreateAddEvent() {
        System.out.println("createAddEvent");
        assertEquals("Test", addInst.getSource());
        assertNull(addInst.getRemoved());
        assertEquals(add, addInst.getAdded());
    }

    @Test
    public void testCreateRemoveEvent() {
        System.out.println("createRemoveEvent");
        assertEquals("Test", removeInst.getSource());
        assertEquals(remove, removeInst.getRemoved());
        assertNull(removeInst.getAdded());
    }

    @Test
    public void testCreateAddRemoveEvent() {
        System.out.println("createAddRemoveEvent");
        assertEquals("Test", bothInst.getSource());
        assertEquals(remove, bothInst.getRemoved());
        assertEquals(add, bothInst.getAdded());
    }

    @Test
    public void testToString() {
        System.out.println("toString");
        assertEquals("CoordinateChangeEvent[1 added,1 removed,source=Test]", bothInst.toString());
    }

    @Test
    public void testIsAddEvent() {
        System.out.println("isAddEvent");
        assertTrue(addInst.isAddEvent());
        assertFalse(removeInst.isAddEvent());
        assertTrue(bothInst.isAddEvent());
    }

    @Test
    public void testIsRemoveEvent() {
        System.out.println("isRemoveEvent");
        assertFalse(addInst.isRemoveEvent());
        assertTrue(removeInst.isRemoveEvent());
        assertTrue(bothInst.isRemoveEvent());
    }

    @Test
    public void testGetAdded() {
        System.out.println("getAdded");
        assertEquals(add, addInst.getAdded());
        assertNull(removeInst.getAdded());
    }

    @Test
    public void testGetRemoved() {
        System.out.println("getRemoved");
        assertNull(addInst.getRemoved());
        assertEquals(remove, removeInst.getRemoved());
    }
    
}
