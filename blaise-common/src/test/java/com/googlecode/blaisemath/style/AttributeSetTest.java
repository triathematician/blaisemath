package com.googlecode.blaisemath.style;

import com.google.common.base.Predicate;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Set;
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
        AttributeSet instance = new AttributeSet();
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of create method, of class AttributeSet.
     */
    @Test
    public void testCreate() {
        System.out.println("create");
        Map map = null;
        AttributeSet expResult = null;
        AttributeSet result = AttributeSet.create(map);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createWithParent method, of class AttributeSet.
     */
    @Test
    public void testCreateWithParent() {
        System.out.println("createWithParent");
        AttributeSet parent = null;
        AttributeSet expResult = null;
        AttributeSet result = AttributeSet.withParent(parent);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of copyOf method, of class AttributeSet.
     */
    @Test
    public void testCopyOf() {
        System.out.println("copyOf");
        AttributeSet set = null;
        AttributeSet expResult = null;
        AttributeSet result = AttributeSet.copyOf(set);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of flatCopyOf method, of class AttributeSet.
     */
    @Test
    public void testFlatCopyOf() {
        System.out.println("flatCopyOf");
        AttributeSet set = null;
        AttributeSet expResult = null;
        AttributeSet result = AttributeSet.flatCopyOf(set);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of copy method, of class AttributeSet.
     */
    @Test
    public void testCopy_AttributeSet_StringArr() {
        System.out.println("copy");
        AttributeSet sty = null;
        String[] keys = null;
        AttributeSet expResult = null;
        AttributeSet result = AttributeSet.copy(sty, keys);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of of method, of class AttributeSet.
     */
    @Test
    public void testOf_String_Object() {
        System.out.println("of");
        String k1 = "";
        Object v1 = null;
        AttributeSet expResult = null;
        AttributeSet result = AttributeSet.of(k1, v1);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of of method, of class AttributeSet.
     */
    @Test
    public void testOf_4args() {
        System.out.println("of");
        String k1 = "";
        Object v1 = null;
        String k2 = "";
        Object v2 = null;
        AttributeSet expResult = null;
        AttributeSet result = AttributeSet.of(k1, v1, k2, v2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of of method, of class AttributeSet.
     */
    @Test
    public void testOf_6args() {
        System.out.println("of");
        String k1 = "";
        Object v1 = null;
        String k2 = "";
        Object v2 = null;
        String k3 = "";
        Object v3 = null;
        AttributeSet expResult = null;
        AttributeSet result = AttributeSet.of(k1, v1, k2, v2, k3, v3);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of and method, of class AttributeSet.
     */
    @Test
    public void testAnd() {
        System.out.println("and");
        String key = "";
        Object val = null;
        AttributeSet instance = new AttributeSet();
        AttributeSet expResult = null;
        AttributeSet result = instance.and(key, val);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of immutable method, of class AttributeSet.
     */
    @Test
    public void testImmutable() {
        System.out.println("immutable");
        AttributeSet instance = new AttributeSet();
        AttributeSet expResult = null;
        AttributeSet result = instance.immutable();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of immutableWithParent method, of class AttributeSet.
     */
    @Test
    public void testImmutableWithParent() {
        System.out.println("immutableWithParent");
        AttributeSet par = null;
        AttributeSet instance = new AttributeSet();
        AttributeSet expResult = null;
        AttributeSet result = instance.immutableWithParent(par);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of copy method, of class AttributeSet.
     */
    @Test
    public void testCopy_0args() {
        System.out.println("copy");
        AttributeSet instance = new AttributeSet();
        AttributeSet expResult = null;
        AttributeSet result = instance.copy();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of flatCopy method, of class AttributeSet.
     */
    @Test
    public void testFlatCopy() {
        System.out.println("flatCopy");
        AttributeSet instance = new AttributeSet();
        AttributeSet expResult = null;
        AttributeSet result = instance.flatCopy();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getParent method, of class AttributeSet.
     */
    @Test
    public void testGetParent() {
        System.out.println("getParent");
        AttributeSet instance = new AttributeSet();
        assertFalse(instance.getParent().isPresent());
        AttributeSet instance2 = AttributeSet.withParent(new AttributeSet());
        assertTrue(instance2.getParent().isPresent());
    }

    /**
     * Test of getAttributes method, of class AttributeSet.
     */
    @Test
    public void testGetAttributes() {
        System.out.println("getAttributes");
        AttributeSet instance = new AttributeSet();
        Set<String> expResult = null;
        Set<String> result = instance.getAttributes();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllAttributes method, of class AttributeSet.
     */
    @Test
    public void testGetAllAttributes_0args() {
        System.out.println("getAllAttributes");
        AttributeSet instance = new AttributeSet();
        Set<String> expResult = null;
        Set<String> result = instance.getAllAttributes();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAttributeMap method, of class AttributeSet.
     */
    @Test
    public void testGetAttributeMap() {
        System.out.println("getAttributeMap");
        AttributeSet instance = new AttributeSet();
        Map<String, Object> expResult = null;
        Map<String, Object> result = instance.getAttributeMap();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllAttributes method, of class AttributeSet.
     */
    @Test
    public void testGetAllAttributes_Class() {
        System.out.println("getAllAttributes");
        Class type = null;
        AttributeSet instance = new AttributeSet();
        Set<String> expResult = null;
        Set<String> result = instance.getAllAttributes(type);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
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

    /**
     * Test of put method, of class AttributeSet.
     */
    @Test
    public void testPut() {
        System.out.println("put");
        AttributeSet as = AttributeSet.of("a", 1);
        assertEquals(1, as.put("a", 2));
        assertEquals(2, as.put("a", null));
        assertEquals(null, as.get("a"));
        assertTrue(as.contains("a"));
    }

    /**
     * Test of putIfAbsent method, of class AttributeSet.
     */
    @Test
    public void testPutIfAbsent() {
        System.out.println("putIfAbsent");
        AttributeSet as = AttributeSet.of("a", 1);
        as.putIfAbsent("a", 2);
        as.putIfAbsent("b", 3);
        assertEquals(1, as.get("a"));
        assertEquals(3, as.get("b"));
    }

    /**
     * Test of putAll method, of class AttributeSet.
     */
    @Test
    public void testPutAll() {
        System.out.println("putAll");
        AttributeSet instance = new AttributeSet();
        instance.putAll(null);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of remove method, of class AttributeSet.
     */
    @Test
    public void testRemove() {
        System.out.println("remove");
        String key = "";
        AttributeSet instance = new AttributeSet();
        Object expResult = null;
        Object result = instance.remove(key);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getString method, of class AttributeSet.
     */
    @Test
    public void testGetString_String() {
        System.out.println("getString");
        String key = "";
        AttributeSet instance = new AttributeSet();
        String expResult = "";
        String result = instance.getString(key);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getString method, of class AttributeSet.
     */
    @Test
    public void testGetString_String_String() {
        System.out.println("getString");
        String key = "";
        String def = "";
        AttributeSet instance = new AttributeSet();
        String expResult = "";
        String result = instance.getString(key, def);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getColor method, of class AttributeSet.
     */
    @Test
    public void testGetColor_String() {
        System.out.println("getColor");
        String key = "";
        AttributeSet instance = new AttributeSet();
        Color expResult = null;
        Color result = instance.getColor(key);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getColor method, of class AttributeSet.
     */
    @Test
    public void testGetColor_String_Color() {
        System.out.println("getColor");
        String key = "";
        Color def = null;
        AttributeSet instance = new AttributeSet();
        Color expResult = null;
        Color result = instance.getColor(key, def);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPoint method, of class AttributeSet.
     */
    @Test
    public void testGetPoint_String() {
        System.out.println("getPoint");
        String key = "";
        AttributeSet instance = new AttributeSet();
        Point2D expResult = null;
        Point2D result = instance.getPoint(key);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPoint method, of class AttributeSet.
     */
    @Test
    public void testGetPoint_String_Point2D() {
        System.out.println("getPoint");
        String key = "";
        Point2D def = null;
        AttributeSet instance = new AttributeSet();
        Point2D expResult = null;
        Point2D result = instance.getPoint(key, def);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBoolean method, of class AttributeSet.
     */
    @Test
    public void testGetBoolean_String() {
        System.out.println("getBoolean");
        String key = "";
        AttributeSet instance = new AttributeSet();
        Boolean expResult = null;
        Boolean result = instance.getBoolean(key);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBoolean method, of class AttributeSet.
     */
    @Test
    public void testGetBoolean_String_Boolean() {
        System.out.println("getBoolean");
        String key = "";
        Boolean def = null;
        AttributeSet instance = new AttributeSet();
        Boolean expResult = null;
        Boolean result = instance.getBoolean(key, def);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFloat method, of class AttributeSet.
     */
    @Test
    public void testGetFloat_String() {
        System.out.println("getFloat");
        String key = "";
        AttributeSet instance = new AttributeSet();
        Float expResult = null;
        Float result = instance.getFloat(key);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFloat method, of class AttributeSet.
     */
    @Test
    public void testGetFloat_String_Float() {
        System.out.println("getFloat");
        String key = "";
        Float def = null;
        AttributeSet instance = new AttributeSet();
        Float expResult = null;
        Float result = instance.getFloat(key, def);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getInteger method, of class AttributeSet.
     */
    @Test
    public void testGetInteger_String() {
        System.out.println("getInteger");
        String key = "";
        AttributeSet instance = new AttributeSet();
        Integer expResult = null;
        Integer result = instance.getInteger(key);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getInteger method, of class AttributeSet.
     */
    @Test
    public void testGetInteger_String_Integer() {
        System.out.println("getInteger");
        String key = "";
        Integer def = null;
        AttributeSet instance = new AttributeSet();
        Integer expResult = null;
        Integer result = instance.getInteger(key, def);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

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

    /**
     * Test of withParent method, of class AttributeSet.
     */
    @Test
    public void testWithParent() {
        System.out.println("withParent");
        AttributeSet parent = null;
        AttributeSet expResult = null;
        AttributeSet result = AttributeSet.withParent(parent);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAttributes method, of class AttributeSet.
     */
    @Test
    public void testGetAttributes_0args() {
        System.out.println("getAttributes");
        AttributeSet instance = new AttributeSet();
        Set<String> expResult = null;
        Set<String> result = instance.getAttributes();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAttributes method, of class AttributeSet.
     */
    @Test
    public void testGetAttributes_Predicate() {
        System.out.println("getAttributes");
        Predicate<String> filter = null;
        AttributeSet instance = new AttributeSet();
        Set<String> expResult = null;
        Set<String> result = instance.getAttributes(filter);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
