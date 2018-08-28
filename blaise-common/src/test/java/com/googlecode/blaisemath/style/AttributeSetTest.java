package com.googlecode.blaisemath.style;

/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2018 Elisha Peterson
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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author triat
 */
public class AttributeSetTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test of toString method, of class AttributeSet.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        assertEquals("{  }", new AttributeSet().toString());
        assertEquals("{ a:1; b:2 }", AttributeSet.of("a", 1, "b", 2).toString());
        assertEquals("{ a:true }", AttributeSet.of("a", true).toString());
        assertEquals("{ a:what }", AttributeSet.of("a", "what").toString());
    }
    
    //<editor-fold defaultstate="collapsed" desc="FACTORY/BUILDER TESTS">

    /**
     * Test of create method, of class AttributeSet.
     */
    @Test
    public void testCreate() {
        System.out.println("create");
        assertEquals("{ a:1 }", AttributeSet.create(ImmutableMap.of("a", 1)).toString());
    }

    /**
     * Test of withParent method, of class AttributeSet.
     */
    @Test
    public void testWithParent() {
        System.out.println("withParent");
        assertEquals(null, AttributeSet.withParent(null).getParent().orElse(null));
        assertEquals("{ a:1 }", AttributeSet.withParent(AttributeSet.of("a", 1)).getParent().get().toString());
        
        AttributeSet par = AttributeSet.of("key", "val");
        AttributeSet set1 = AttributeSet.withParent(par);
        assertEquals("val", set1.get("key"));
        
        AttributeSet set2 = AttributeSet.withParent(par)
                .and("key", null);
        assertEquals(null, set2.get("key"));
    }

    /**
     * Test of copyOf method, of class AttributeSet.
     */
    @Test
    public void testCopyOf() {
        System.out.println("copyOf");
        AttributeSet set = AttributeSet.withParent(AttributeSet.of("a", 1)).and("b", 2);
        AttributeSet copy = AttributeSet.copyOf(set);
        assertFalse(set == copy);
        assertTrue(set.getAttributeMap().equals(copy.getAttributeMap()));
        assertTrue(set.getParent().get() == copy.getParent().get());
    }

    /**
     * Test of flatCopyOf method, of class AttributeSet.
     */
    @Test
    public void testFlatCopyOf() {
        System.out.println("flatCopyOf");
        AttributeSet set = AttributeSet.withParent(AttributeSet.of("a", 1)).and("b", 2);
        AttributeSet result = AttributeSet.flatCopyOf(set);
        assertEquals(Sets.newHashSet("b"), set.getAttributes());
        assertEquals(Sets.newHashSet("a", "b"), result.getAttributes());
    }

    /**
     * Test of copy method, of class AttributeSet.
     */
    @Test
    public void testCopy_AttributeSet_StringArr() {
        System.out.println("copy");
        AttributeSet a = AttributeSet.withParent(new AttributeSet()).and("a", 1).and("b", 2).and("c", 3);
        AttributeSet b = AttributeSet.copy(a, "a", "b");
        assertEquals(ImmutableSet.of("a", "b"), b.getAttributes());
        assertFalse(b.getParent().isPresent());
    }

    /**
     * Test of of method, of class AttributeSet.
     */
    @Test
    public void testOf_String_Object() {
        System.out.println("of");
        assertEquals("{ a:1 }", AttributeSet.of("a", 1).toString());
    }

    /**
     * Test of of method, of class AttributeSet.
     */
    @Test
    public void testOf_4args() {
        System.out.println("of");
        assertEquals("{ a:1; b:2 }", AttributeSet.of("a", 1, "b", 2).toString());
    }

    /**
     * Test of of method, of class AttributeSet.
     */
    @Test
    public void testOf_6args() {
        System.out.println("of");
        assertEquals("{ a:1; b:2; c:3 }", AttributeSet.of("a", 1, "b", 2, "c", 3).toString());
    }

    /**
     * Test of and method, of class AttributeSet.
     */
    @Test
    public void testAnd() {
        System.out.println("and");
        assertEquals("{ a:1 }", new AttributeSet().and("a", 1).toString());
    }

    /**
     * Test of immutable method, of class AttributeSet.
     */
    @Test
    public void testImmutable() {
        System.out.println("immutable");
        AttributeSet instance = AttributeSet.of("a", 1);
        AttributeSet result = instance.immutable();
        try {
            result.remove("a");
            fail();
        } catch (UnsupportedOperationException x) {
            return;
        }
        fail();
    }

    /**
     * Test of immutableWithParent method, of class AttributeSet.
     */
    @Test
    public void testImmutableWithParent() {
        System.out.println("immutableWithParent");
        AttributeSet instance = AttributeSet.of("a", 1);
        AttributeSet par = new AttributeSet();
        AttributeSet result = instance.immutableWithParent(par);
        try {
            result.remove("a");
            fail();
        } catch (UnsupportedOperationException x) {
            return;
        }
        fail();
        assertEquals(par, result.getParent().get());
    }

    /**
     * Test of copy method, of class AttributeSet.
     */
    @Test
    public void testCopy_0args() {
        System.out.println("copy");
        AttributeSet par = new AttributeSet();
        AttributeSet instance = AttributeSet.withParent(par).and("a", 1);
        AttributeSet result = instance.copy();
        assertEquals(par, result.getParent().get());
        assertEquals(1, result.get("a"));
    }

    /**
     * Test of flatCopy method, of class AttributeSet.
     */
    @Test
    public void testFlatCopy() {
        System.out.println("flatCopy");
        AttributeSet par = AttributeSet.of("b", 2);
        AttributeSet instance = AttributeSet.withParent(par).and("a", 1);
        AttributeSet result = instance.flatCopy();
        assertEquals(null, result.getParent().orElse(null));
        assertEquals(ImmutableSet.of("a", "b"), result.getAttributes());
        assertEquals(ImmutableSet.of("a", "b"), result.getAllAttributes());
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="ACCESSOR TESTS">

    @Test
    public void testGetParent() {
        System.out.println("getParent");
        AttributeSet instance = new AttributeSet();
        assertFalse(instance.getParent().isPresent());
        AttributeSet instance2 = AttributeSet.withParent(new AttributeSet());
        assertTrue(instance2.getParent().isPresent());
    }

    @Test
    public void testGetAllAttributes_0args() {
        System.out.println("getAllAttributes");
        AttributeSet par = AttributeSet.of("b", 2);
        AttributeSet instance = AttributeSet.withParent(par).and("a", 1);
        assertEquals(ImmutableSet.of("a", "b"), instance.getAllAttributes());
    }

    @Test
    public void testGetAttributeMap() {
        System.out.println("getAttributeMap");
        AttributeSet instance = AttributeSet.of("a", 1);
        assertEquals(ImmutableMap.of("a", 1), instance.getAttributeMap());
    }

    /**
     * Test of getAllAttributes method, of class AttributeSet.
     */
    @Test
    public void testGetAllAttributes_Class() {
        System.out.println("getAllAttributes");
        AttributeSet instance = AttributeSet.of("a", 1, "b", 3.0);
        assertEquals(ImmutableSet.of("a"), instance.getAllAttributes(Integer.class));
    }

    /**
     * Test of getAttributes method, of class AttributeSet.
     */
    @Test
    public void testGetAttributes_0args() {
        AttributeSet par = AttributeSet.of("b", 2);
        AttributeSet instance = AttributeSet.withParent(par).and("a", 1);
        assertEquals(ImmutableSet.of("a"), instance.getAttributes());
    }

    /**
     * Test of getAttributes method, of class AttributeSet.
     */
    @Test
    public void testGetAttributes_Predicate() {
        System.out.println("getAttributes");
        AttributeSet instance = AttributeSet.of("a", 1, "a2", 2, "b", 3);
        assertEquals(ImmutableSet.of("a", "a2"), instance.getAttributes(s -> s.startsWith("a")));
    }

    /**
     * Test of contains method, of class AttributeSet.
     */
    @Test
    public void testContains() {
        System.out.println("contains");
        AttributeSet as = AttributeSet.of("a", 1);
        assertTrue(as.contains("a"));
        assertFalse(as.contains("b"));
    }

    /**
     * Test of get method, of class AttributeSet.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        AttributeSet as = AttributeSet.of("a", 1, "b", null);
        assertEquals(1, as.get("a"));
        assertEquals(null, as.get("b"));
        assertEquals(null, as.get("c"));
    }

    /**
     * Test of getOrDefault method, of class AttributeSet.
     */
    @Test
    public void testGetOrDefault() {
        System.out.println("getOrDefault");
        AttributeSet as = AttributeSet.withParent(AttributeSet.of("a", 5, "d", 1, "c", null))
                .and("a", null).and("b", 2);
        assertEquals(null, as.getOrDefault("a", -1));
        assertEquals(2, as.getOrDefault("b", -1));
        assertEquals(null, as.getOrDefault("c", -1));
        assertEquals(1, as.getOrDefault("d", -1));
    }
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="MUTATOR TESTS">
    
    @Test
    public void testPut() {
        System.out.println("put");
        AttributeSet as = AttributeSet.of("a", 1);
        assertEquals(1, as.put("a", 2));
        assertEquals(2, as.put("a", null));
        assertEquals(null, as.get("a"));
        assertTrue(as.contains("a"));
    }

    @Test
    public void testPutIfAbsent() {
        System.out.println("putIfAbsent");
        AttributeSet as = AttributeSet.of("a", 1);
        as.putIfAbsent("a", 2);
        as.putIfAbsent("b", 3);
        assertEquals(1, as.get("a"));
        assertEquals(3, as.get("b"));
    }

    @Test
    public void testPutAll() {
        System.out.println("putAll");
        AttributeSet instance = new AttributeSet();
        instance.putAll(ImmutableMap.of("a", 1, "b", "bb"));
        assertEquals("{ a:1; b:bb }", instance.toString());
    }

    @Test
    public void testRemove() {
        System.out.println("remove");
        AttributeSet instance = AttributeSet.of("a", 1);
        assertEquals(1, instance.remove("a"));
        assertEquals(null, instance.get("a"));
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="TYPED ACCESSOR TESTS">

    @Test
    public void testGetString_String() {
        System.out.println("getString");
        AttributeSet instance = AttributeSet.of("a", 1, "b", "2", "c", null);
        assertEquals("1", instance.getString("a"));
        assertEquals("2", instance.getString("b"));
        assertEquals(null, instance.getString("c"));
        assertEquals(null, instance.getString("d"));
    }

    @Test
    public void testGetString_String_String() {
        System.out.println("getString");
        AttributeSet instance = AttributeSet.of("a", 1, "b", "2", "c", null);
        assertEquals("1", instance.getString("a", "x"));
        assertEquals("x", instance.getString("d", "x"));
    }

    @Test
    public void testGetBoolean_String() {
        System.out.println("getBoolean");
        AttributeSet instance = AttributeSet.of("a", "true", "b", false, "c", null).and("d", 1);
        assertEquals(true, instance.getBoolean("a"));
        assertEquals(false, instance.getBoolean("b"));
        assertEquals(null, instance.getBoolean("c"));
        assertEquals(null, instance.getBoolean("d"));
        assertEquals(null, instance.getBoolean("e"));
    }

    @Test
    public void testGetBoolean_String_Boolean() {
        System.out.println("getBoolean");
        AttributeSet instance = AttributeSet.of("a", "true");
        assertEquals(true, instance.getBoolean("a", false));
        assertEquals(false, instance.getBoolean("b", false));
    }
    
    @Test
    public void testGetInteger_String() {
        System.out.println("getInteger");
        AttributeSet instance = AttributeSet.of("a", "1", "b", 2, "c", null).and("d", 3.0);
        assertEquals(1, (int) instance.getInteger("a"));
        assertEquals(2, (int) instance.getInteger("b"));
        assertEquals(null, instance.getInteger("c"));
        assertEquals(3, (int) instance.getInteger("d"));
        assertEquals(null, instance.getInteger("e"));
    }

    @Test
    public void testGetInteger_String_Integer() {
        System.out.println("getInteger");
        AttributeSet instance = AttributeSet.of("a", "1");
        assertEquals(1, (int) instance.getInteger("a", 2));
        assertEquals(2, (int) instance.getInteger("b", 2));
    }

    @Test
    public void testGetFloat_String() {
        System.out.println("getFloat");
        AttributeSet instance = AttributeSet.of("a", "1", "b", 2f, "c", null).and("d", 3);
        assertEquals(new Float(1), instance.getFloat("a"));
        assertEquals(new Float(2), instance.getFloat("b"));
        assertEquals(null, instance.getFloat("c"));
        assertEquals(new Float(3), instance.getFloat("d"));
        assertEquals(null, instance.getFloat("e"));
    }

    @Test
    public void testGetFloat_String_Float() {
        System.out.println("getFloat");
        AttributeSet instance = AttributeSet.of("a", "1");
        assertEquals(new Float(1), instance.getFloat("a", 2f));
        assertEquals(new Float(2), instance.getFloat("b", 2f));
    }

    @Test
    public void testGetColor_String() {
        System.out.println("getColor");
        AttributeSet instance = AttributeSet.of("a", "red", "b", Color.red, "c", null).and("d", 1);
        assertEquals(Color.red, instance.getColor("a"));
        assertEquals(Color.red, instance.getColor("b"));
        assertEquals(null, instance.getColor("c"));
        assertEquals(null, instance.getColor("d"));
        assertEquals(null, instance.getColor("e"));
    }

    @Test
    public void testGetColor_String_Color() {
        System.out.println("getColor");
        AttributeSet instance = AttributeSet.of("a", "red", "b", Color.red, "c", null).and("d", 1);
        assertEquals(Color.red, instance.getColor("a", Color.black));
        assertEquals(Color.red, instance.getColor("b", Color.black));
        assertEquals(Color.black, instance.getColor("c", Color.black));
        assertEquals(Color.black, instance.getColor("d", Color.black));
        assertEquals(Color.black, instance.getColor("e", Color.black));
    }

    @Test
    public void testGetPoint_String() {
        System.out.println("getPoint");
        AttributeSet instance = AttributeSet.of("a", new Point(1, 2), 
                "b", Color.red, "c", null, "d", "(1,2)");
        assertEquals(null, instance.getPoint2D("b"));
        assertEquals(null, instance.getPoint2D("c"));
        assertEquals(null, instance.getPoint2D("e"));
        assertEquals(new Point(1, 2), instance.getPoint("a"));
        assertEquals(new Point(1, 2), instance.getPoint("d"));
        assertEquals(new Point(1, 2), instance.getPoint2D("a"));
        assertEquals(new Point(1, 2), instance.getPoint2D("d"));
    }

    @Test
    public void testGetPoint_String_Point2D() {
        System.out.println("getPoint");
        AttributeSet instance = AttributeSet.of("a", new Point(1, 2), "b", Color.red, "c", null, "d", "(1,2)");
        Point def = new Point(3, 4);
        assertEquals(def, instance.getPoint2D("b", def));
        assertEquals(def, instance.getPoint2D("c", def));
        assertEquals(def, instance.getPoint2D("e", def));
        assertEquals(new Point(1, 2), instance.getPoint("a", def));
        assertEquals(new Point(1, 2), instance.getPoint("d", def));
        assertEquals(new Point(1, 2), instance.getPoint2D("a", def));
        assertEquals(new Point(1, 2), instance.getPoint2D("d", def));
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CHANGE EVENT TESTS">

    /**
     * Test of addChangeListener method, of class AttributeSet.
     */
    @Test
    public void testAddChangeListener() {
        System.out.println("addChangeListener");
        // no test -- boilerplate code
    }

    /**
     * Test of removeChangeListener method, of class AttributeSet.
     */
    @Test
    public void testRemoveChangeListener() {
        System.out.println("removeChangeListener");
        // no test -- boilerplate code
    }

    /**
     * Test of fireStateChanged method, of class AttributeSet.
     */
    @Test
    public void testFireStateChanged() {
        System.out.println("fireStateChanged");
        // no test -- boilerplate code
    }
    
    //</editor-fold>
    
}