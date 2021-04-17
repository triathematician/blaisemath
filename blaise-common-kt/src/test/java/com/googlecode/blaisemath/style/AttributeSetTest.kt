package com.googlecode.blaisemath.style
/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2021 Elisha Peterson
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

import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.awt.Color
import java.awt.Point
import kotlin.test.assertNotNull

class AttributeSetTest {

    @Test
    fun testToString() {
        assertEquals("{  }", AttributeSet().toString())
        assertEquals("{ a:1; b:2 }", AttributeSet("a" to 1, "b" to 2).toString())
        assertEquals("{ a:true }", AttributeSet("a" to true).toString())
        assertEquals("{ a:what }", AttributeSet("a" to "what").toString())
    }

    @Test
    fun testCreate() {
        assertEquals("{ a:1 }", AttributeSet("a" to 1).toString())
    }

    @Test
    fun testImmutableWithParent() {
        val par = AttributeSet("key" to "val")
        val set1 = AttributeSet().immutableWithParent(par)
        assertEquals("val", set1["key"])
        val set2 = AttributeSet("key" to null).immutableWithParent(par)
        assertNull(set2["key"])
        try {
            set2.remove("key")
            Assert.fail()
        } catch (x: UnsupportedOperationException) {
            return
        }
    }

    @Test
    fun testCopyOf() {
        val set = AttributeSet(AttributeSet("a" to 1)).and("b" to 2)
        val copy = set.copy()
        Assert.assertNotSame(set, copy)
        assertEquals(set.attributeMap, copy.attributeMap)
        Assert.assertSame(set.parent, copy.parent)
    }

    @Test
    fun testCopyOnly() {
        val a = AttributeSet(AttributeSet()).and("a" to 1).and("b" to 2).and("c" to 3)
        val b = a.copyOnly("a", "b")
        assertEquals(setOf("a", "b"), b.attributes)
        assertNull(b.parent)
    }

    @Test
    fun testOf_String_Object() {
        assertEquals("{ a:1 }", AttributeSet("a" to 1).toString())
    }

    @Test
    fun testOf_4args() {
        assertEquals("{ a:1; b:2 }", AttributeSet("a" to 1, "b" to 2).toString())
    }

    @Test
    fun testOf_6args() {
        assertEquals("{ a:1; b:2; c:3 }", AttributeSet("a" to 1, "b" to 2, "c" to 3).toString())
    }

    @Test
    fun testAnd() {
        assertEquals("{ a:1 }", AttributeSet().and("a" to 1).toString())
    }

    @Test
    fun testImmutable() {
        val instance = AttributeSet("a" to 1)
        val result = instance.immutable()
        try {
            result.remove("a")
            Assert.fail()
        } catch (x: UnsupportedOperationException) {
            return
        }
        Assert.fail()
    }

    @Test
    fun testCopy_0args() {
        val par = AttributeSet()
        val instance: AttributeSet = AttributeSet(par).and("a" to 1)
        val result = instance.copy()
        assertEquals(par, result.parent)
        assertEquals(1, result["a"])
    }

    @Test
    fun testFlatCopy() {
        val par = AttributeSet("b" to 2)
        val instance = AttributeSet(par).and("a" to 1)
        val result1 = instance.flatCopy()
        assertNull(result1.parent)
        assertEquals(setOf("a", "b"), result1.attributes)
        assertEquals(setOf("a", "b"), result1.allAttributes)

        val set = AttributeSet(AttributeSet("a" to 1)).and("b" to 2)
        val result2 = set.flatCopy()
        assertEquals(setOf("b"), set.attributes)
        assertEquals(setOf("a", "b"), result2.attributes)
    }

    @Test
    fun testGetParent() {
        assertNull(AttributeSet().parent)
        assertNotNull(AttributeSet(AttributeSet()).parent)
    }

    @Test
    fun testGetAllAttributes_0args() {
        val par: AttributeSet = AttributeSet("b" to 2)
        val instance: AttributeSet = AttributeSet(par).and("a" to 1)
        assertEquals(setOf("a", "b"), instance.allAttributes)
    }

    @Test
    fun testAttributeMap() {
        val instance = AttributeSet("a" to 1)
        assertEquals(mapOf("a" to 1), instance.attributeMap)
    }

    @Test
    fun testAllAttributes_type() {
        val instance = AttributeSet("a" to 1, "b" to 3.0)
        assertEquals(setOf("a"), instance.allAttributes(Integer::class.java))
    }

    @Test
    fun testAttributes() {
        val par = AttributeSet("b" to 2)
        val instance = AttributeSet(par).and("a" to 1)
        assertEquals(setOf("a"), instance.attributes)
    }

    @Test
    fun testAllAttributes_predicate() {
        val instance = AttributeSet("a" to 1, "a2" to 2, "b" to 3)
        assertEquals(listOf("a", "a2"), instance.allAttributes { it.startsWith("a") })
    }

    @Test
    fun testContains() {
        val `as`: AttributeSet = AttributeSet("a" to 1)
        Assert.assertTrue(`as`.contains("a"))
        Assert.assertFalse(`as`.contains("b"))
    }

    @Test
    fun testGet() {
        val `as`: AttributeSet = AttributeSet("a" to 1, "b" to null)
        assertEquals(1, `as`["a"])
        assertNull(`as`["b"])
        assertNull(`as`["c"])
    }

    @Test
    fun testGetOrDefault() {
        val `as`: AttributeSet = AttributeSet(AttributeSet("a" to 5, "d" to 1, "c" to null))
                .and("a" to null, "b" to 2)
        assertNull(`as`.getOrDefault("a", -1))
        assertEquals(2, `as`.getOrDefault("b", -1))
        assertNull(`as`.getOrDefault("c", -1))
        assertEquals(1, `as`.getOrDefault("d", -1))
    }

    @Test
    fun testPut() {
        val `as`: AttributeSet = AttributeSet("a" to 1)
        assertEquals(1, `as`.put("a", 2))
        assertEquals(2, `as`.put("a", null))
        assertNull(`as`["a"])
        Assert.assertTrue(`as`.contains("a"))
    }

    @Test
    fun testPutIfAbsent() {
        val `as`: AttributeSet = AttributeSet("a" to 1)
        `as`.putIfAbsent("a", 2)
        `as`.putIfAbsent("b", 3)
        assertEquals(1, `as`["a"])
        assertEquals(3, `as`["b"])
    }

    @Test
    fun testPutAll() {
        val instance = AttributeSet()
        instance.putAll(mapOf("a" to 1, "b" to "bb"))
        assertEquals("{ a:1; b:bb }", instance.toString())
    }

    @Test
    fun testRemove() {
        val instance: AttributeSet = AttributeSet("a" to 1)
        assertEquals(1, instance.remove("a"))
        assertNull(instance["a"])
    }

    @Test
    fun testGetString_String() {
        val instance: AttributeSet = AttributeSet("a" to 1, "b" to "2", "c" to null)
        assertEquals("1", instance.getString("a"))
        assertEquals("2", instance.getString("b"))
        assertNull(instance.getString("c"))
        assertNull(instance.getString("d"))
    }

    @Test
    fun testGetString_String_String() {
        val instance: AttributeSet = AttributeSet("a" to 1, "b" to "2", "c" to null)
        assertEquals("1", instance.getString("a", "x"))
        assertEquals("x", instance.getString("d", "x"))
    }

    @Test
    fun testGetBoolean_String() {
        val instance: AttributeSet = AttributeSet("a" to "true", "b" to false, "c" to null).and("d" to 1)
        assertEquals(true, instance.getBoolean("a"))
        assertEquals(false, instance.getBoolean("b"))
        assertNull(instance.getBoolean("c"))
        assertNull(instance.getBoolean("d"))
        assertNull(instance.getBoolean("e"))
    }

    @Test
    fun testGetBoolean_String_Boolean() {
        val instance: AttributeSet = AttributeSet("a" to "true")
        assertEquals(true, instance.getBoolean("a", false))
        assertEquals(false, instance.getBoolean("b", false))
    }

    @Test
    fun testGetInteger_String() {
        val instance: AttributeSet = AttributeSet("a" to "1", "b" to 2, "c" to null).and("d" to 3.0)
        assertEquals(1, instance.getInteger("a"))
        assertEquals(2, instance.getInteger("b"))
        assertNull(instance.getInteger("c"))
        assertEquals(3, instance.getInteger("d"))
        assertNull(instance.getInteger("e"))

        // TODO - test failure case
    }

    @Test
    fun testGetInteger_String_Integer() {
        val instance: AttributeSet = AttributeSet("a" to "1")
        assertEquals(1, instance.getInteger("a", 2))
        assertEquals(2, instance.getInteger("b", 2))
    }

    @Test
    fun testGetFloat_String() {
        val instance: AttributeSet = AttributeSet("a" to "1", "b" to 2f, "c" to null).and("d" to 3)
        assertEquals(1f, instance.getFloat("a"))
        assertEquals(2f, instance.getFloat("b"))
        assertNull(instance.getFloat("c"))
        assertEquals(3f, instance.getFloat("d"))
        assertNull(instance.getFloat("e"))
    }

    @Test
    fun testGetFloat_String_Float() {
        val instance: AttributeSet = AttributeSet("a" to "1")
        assertEquals(1f, instance.getFloat("a", 2f))
        assertEquals(2f, instance.getFloat("b", 2f))
    }

    @Test
    fun testGetColor_String() {
        val instance: AttributeSet = AttributeSet("a" to "red", "b" to Color.red, "c" to null).and("d" to 1)
        assertEquals(Color.red, instance.getColor("a"))
        assertEquals(Color.red, instance.getColor("b"))
        assertNull(instance.getColor("c"))
        assertNull(instance.getColor("d"))
        assertNull(instance.getColor("e"))
    }

    @Test
    fun testGetColor_String_Color() {
        val instance: AttributeSet = AttributeSet("a" to "red", "b" to Color.red, "c" to null).and("d" to 1)
        assertEquals(Color.red, instance.getColor("a", Color.black))
        assertEquals(Color.red, instance.getColor("b", Color.black))
        assertEquals(Color.black, instance.getColor("c", Color.black))
        assertEquals(Color.black, instance.getColor("d", Color.black))
        assertEquals(Color.black, instance.getColor("e", Color.black))
    }

    @Test
    fun testGetPoint_String() {
        val instance: AttributeSet = AttributeSet("a" to Point(1, 2), "b" to Color.red, "c" to null, "d" to "(1,2)")
        assertNull(instance.getPoint2D("b"))
        assertNull(instance.getPoint2D("c"))
        assertNull(instance.getPoint2D("e"))
        assertEquals(Point(1, 2), instance.getPoint("a"))
        assertEquals(Point(1, 2), instance.getPoint("d"))
        assertEquals(Point(1, 2), instance.getPoint2D("a"))
        assertEquals(Point(1, 2), instance.getPoint2D("d"))
    }

    @Test
    fun testGetPoint_String_Point2D() {
        val instance: AttributeSet = AttributeSet("a" to Point(1, 2), "b" to Color.red, "c" to null, "d" to "(1,2)")
        val def = Point(3, 4)
        assertEquals(def, instance.getPoint2D("b", def))
        assertEquals(def, instance.getPoint2D("c", def))
        assertEquals(def, instance.getPoint2D("e", def))
        assertEquals(Point(1, 2), instance.getPoint("a", def))
        assertEquals(Point(1, 2), instance.getPoint("d", def))
        assertEquals(Point(1, 2), instance.getPoint2D("a", def))
        assertEquals(Point(1, 2), instance.getPoint2D("d", def))
    }
}