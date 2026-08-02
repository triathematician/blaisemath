package com.googlecode.blaisemath.style

import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import com.google.common.collect.Sets
import org.junit.Assert
import org.junit.Test
import java.awt.Color
import java.awt.Point

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
*/   class AttributeSetTest {
    @Test
    fun testToString() {
        println("toString")
        Assert.assertEquals("{ a: }", AttributeSet.Companion.of("a", null).toString())
        Assert.assertEquals("{  }", AttributeSet().toString())
        Assert.assertEquals("{ a:1; b:2 }", AttributeSet.Companion.of("a", 1, "b", 2).toString())
        Assert.assertEquals("{ a:true }", AttributeSet.Companion.of("a", true).toString())
        Assert.assertEquals("{ a:what }", AttributeSet.Companion.of("a", "what").toString())
    }

    //<editor-fold defaultstate="collapsed" desc="FACTORY/BUILDER TESTS">
    @Test
    fun testCreate() {
        println("create")
        Assert.assertEquals("{ a:1 }", AttributeSet.Companion.create(ImmutableMap.of("a", 1)).toString())
    }

    @Test
    fun testWithParent() {
        println("withParent")
        Assert.assertNull(AttributeSet.Companion.withParent(null).getParent().orElse(null))
        Assert.assertEquals("{ a:1 }", AttributeSet.Companion.withParent(AttributeSet.Companion.of("a", 1)).getParent().get().toString())
        val par: AttributeSet = AttributeSet.Companion.of("key", "val")
        val set1: AttributeSet = AttributeSet.Companion.withParent(par)
        Assert.assertEquals("val", set1["key"])
        val set2: AttributeSet = AttributeSet.Companion.withParent(par)
                .and("key", null)
        Assert.assertNull(set2["key"])
    }

    @Test
    fun testCopyOf() {
        println("copyOf")
        val set: AttributeSet = AttributeSet.Companion.withParent(AttributeSet.Companion.of("a", 1)).and("b", 2)
        val copy: AttributeSet = AttributeSet.Companion.copyOf(set)
        Assert.assertNotSame(set, copy)
        Assert.assertEquals(set.getAttributeMap(), copy.getAttributeMap())
        Assert.assertSame(set.getParent().get(), copy.getParent().get())
    }

    @Test
    fun testFlatCopyOf() {
        println("flatCopyOf")
        val set: AttributeSet = AttributeSet.Companion.withParent(AttributeSet.Companion.of("a", 1)).and("b", 2)
        val result: AttributeSet = AttributeSet.Companion.flatCopyOf(set)
        Assert.assertEquals(Sets.newHashSet("b"), set.attributes)
        Assert.assertEquals(Sets.newHashSet("a", "b"), result.attributes)
    }

    @Test
    fun testCopy_AttributeSet_StringArr() {
        println("copy")
        val a: AttributeSet = AttributeSet.Companion.withParent(AttributeSet()).and("a", 1).and("b", 2).and("c", 3)
        val b: AttributeSet = AttributeSet.Companion.copy(a, "a", "b")
        Assert.assertEquals(ImmutableSet.of("a", "b"), b.attributes)
        Assert.assertFalse(b.getParent().isPresent)
    }

    @Test
    fun testOf_String_Object() {
        println("of")
        Assert.assertEquals("{ a:1 }", AttributeSet.Companion.of("a", 1).toString())
    }

    @Test
    fun testOf_4args() {
        println("of")
        Assert.assertEquals("{ a:1; b:2 }", AttributeSet.Companion.of("a", 1, "b", 2).toString())
    }

    @Test
    fun testOf_6args() {
        println("of")
        Assert.assertEquals("{ a:1; b:2; c:3 }", AttributeSet.Companion.of("a", 1, "b", 2, "c", 3).toString())
    }

    @Test
    fun testAnd() {
        println("and")
        Assert.assertEquals("{ a:1 }", AttributeSet().and("a", 1).toString())
    }

    @Test
    fun testImmutable() {
        println("immutable")
        val instance: AttributeSet = AttributeSet.Companion.of("a", 1)
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
    fun testImmutableWithParent() {
        println("immutableWithParent")
        val instance: AttributeSet = AttributeSet.Companion.of("a", 1)
        val par = AttributeSet()
        val result = instance.immutableWithParent(par)
        try {
            result.remove("a")
            Assert.fail()
        } catch (x: UnsupportedOperationException) {
            return
        }
        Assert.fail()
        Assert.assertEquals(par, result.getParent().get())
    }

    @Test
    fun testCopy_0args() {
        println("copy")
        val par = AttributeSet()
        val instance: AttributeSet = AttributeSet.Companion.withParent(par).and("a", 1)
        val result = instance.copy()
        Assert.assertEquals(par, result.getParent().get())
        Assert.assertEquals(1, result["a"])
    }

    @Test
    fun testFlatCopy() {
        println("flatCopy")
        val par: AttributeSet = AttributeSet.Companion.of("b", 2)
        val instance: AttributeSet = AttributeSet.Companion.withParent(par).and("a", 1)
        val result = instance.flatCopy()
        Assert.assertNull(result.getParent().orElse(null))
        Assert.assertEquals(ImmutableSet.of("a", "b"), result.attributes)
        Assert.assertEquals(ImmutableSet.of("a", "b"), result.allAttributes)
    }

    //endregion
    //<editor-fold defaultstate="collapsed" desc="ACCESSOR TESTS">
    @Test
    fun testGetParent() {
        println("getParent")
        val instance = AttributeSet()
        Assert.assertFalse(instance.getParent().isPresent)
        val instance2: AttributeSet = AttributeSet.Companion.withParent(AttributeSet())
        Assert.assertTrue(instance2.getParent().isPresent)
    }

    @Test
    fun testGetAllAttributes_0args() {
        println("getAllAttributes")
        val par: AttributeSet = AttributeSet.Companion.of("b", 2)
        val instance: AttributeSet = AttributeSet.Companion.withParent(par).and("a", 1)
        Assert.assertEquals(ImmutableSet.of("a", "b"), instance.allAttributes)
    }

    @Test
    fun testGetAttributeMap() {
        println("getAttributeMap")
        val instance: AttributeSet = AttributeSet.Companion.of("a", 1)
        Assert.assertEquals(ImmutableMap.of("a", 1), instance.getAttributeMap())
    }

    @Test
    fun testGetAllAttributes_Class() {
        println("getAllAttributes")
        val instance: AttributeSet = AttributeSet.Companion.of("a", 1, "b", 3.0)
        Assert.assertEquals(ImmutableSet.of("a"), instance.getAllAttributes(Int::class.java))
    }

    @Test
    fun testGetAttributes_0args() {
        val par: AttributeSet = AttributeSet.Companion.of("b", 2)
        val instance: AttributeSet = AttributeSet.Companion.withParent(par).and("a", 1)
        Assert.assertEquals(ImmutableSet.of("a"), instance.attributes)
    }

    @Test
    fun testGetAttributes_Predicate() {
        println("getAttributes")
        val instance: AttributeSet = AttributeSet.Companion.of("a", 1, "a2", 2, "b", 3)
        Assert.assertEquals(ImmutableSet.of("a", "a2"), instance.attributes { s: String? -> s.startsWith("a") })
    }

    @Test
    fun testContains() {
        println("contains")
        val `as`: AttributeSet = AttributeSet.Companion.of("a", 1)
        Assert.assertTrue(`as`.contains("a"))
        Assert.assertFalse(`as`.contains("b"))
    }

    @Test
    fun testGet() {
        println("get")
        val `as`: AttributeSet = AttributeSet.Companion.of("a", 1, "b", null)
        Assert.assertEquals(1, `as`["a"])
        Assert.assertNull(`as`["b"])
        Assert.assertNull(`as`["c"])
    }

    @Test
    fun testGetOrDefault() {
        println("getOrDefault")
        val `as`: AttributeSet = AttributeSet.Companion.withParent(AttributeSet.Companion.of("a", 5, "d", 1, "c", null))
                .and("a", null).and("b", 2)
        Assert.assertNull(`as`.getOrDefault("a", -1))
        Assert.assertEquals(2, `as`.getOrDefault("b", -1))
        Assert.assertNull(`as`.getOrDefault("c", -1))
        Assert.assertEquals(1, `as`.getOrDefault("d", -1))
    }

    //endregion
    //<editor-fold defaultstate="collapsed" desc="MUTATOR TESTS">
    @Test
    fun testPut() {
        println("put")
        val `as`: AttributeSet = AttributeSet.Companion.of("a", 1)
        Assert.assertEquals(1, `as`.put("a", 2))
        Assert.assertEquals(2, `as`.put("a", null))
        Assert.assertNull(`as`["a"])
        Assert.assertTrue(`as`.contains("a"))
    }

    @Test
    fun testPutIfAbsent() {
        println("putIfAbsent")
        val `as`: AttributeSet = AttributeSet.Companion.of("a", 1)
        `as`.putIfAbsent("a", 2)
        `as`.putIfAbsent("b", 3)
        Assert.assertEquals(1, `as`["a"])
        Assert.assertEquals(3, `as`["b"])
    }

    @Test
    fun testPutAll() {
        println("putAll")
        val instance = AttributeSet()
        instance.putAll(ImmutableMap.of("a", 1, "b", "bb"))
        Assert.assertEquals("{ a:1; b:bb }", instance.toString())
    }

    @Test
    fun testRemove() {
        println("remove")
        val instance: AttributeSet = AttributeSet.Companion.of("a", 1)
        Assert.assertEquals(1, instance.remove("a"))
        Assert.assertNull(instance["a"])
    }

    //endregion
    //<editor-fold defaultstate="collapsed" desc="TYPED ACCESSOR TESTS">
    @Test
    fun testGetString_String() {
        println("getString")
        val instance: AttributeSet = AttributeSet.Companion.of("a", 1, "b", "2", "c", null)
        Assert.assertEquals("1", instance.getString("a"))
        Assert.assertEquals("2", instance.getString("b"))
        Assert.assertNull(instance.getString("c"))
        Assert.assertNull(instance.getString("d"))
    }

    @Test
    fun testGetString_String_String() {
        println("getString")
        val instance: AttributeSet = AttributeSet.Companion.of("a", 1, "b", "2", "c", null)
        Assert.assertEquals("1", instance.getString("a", "x"))
        Assert.assertEquals("x", instance.getString("d", "x"))
    }

    @Test
    fun testGetBoolean_String() {
        println("getBoolean")
        val instance: AttributeSet = AttributeSet.Companion.of("a", "true", "b", false, "c", null).and("d", 1)
        Assert.assertEquals(true, instance.getBoolean("a"))
        Assert.assertEquals(false, instance.getBoolean("b"))
        Assert.assertNull(instance.getBoolean("c"))
        Assert.assertNull(instance.getBoolean("d"))
        Assert.assertNull(instance.getBoolean("e"))
    }

    @Test
    fun testGetBoolean_String_Boolean() {
        println("getBoolean")
        val instance: AttributeSet = AttributeSet.Companion.of("a", "true")
        Assert.assertEquals(true, instance.getBoolean("a", false))
        Assert.assertEquals(false, instance.getBoolean("b", false))
    }

    @Test
    fun testGetInteger_String() {
        println("getInteger")
        val instance: AttributeSet = AttributeSet.Companion.of("a", "1", "b", 2, "c", null).and("d", 3.0).and("f", "val")
        Assert.assertEquals(1, instance.getInteger("a") as Int.toLong())
        Assert.assertEquals(2, instance.getInteger("b") as Int.toLong())
        Assert.assertNull(instance.getInteger("c"))
        Assert.assertEquals(3, instance.getInteger("d") as Int.toLong())
        Assert.assertNull(instance.getInteger("e"))
        Assert.assertNull(instance.getInteger("f"))
    }

    @Test
    fun testGetInteger_String_Integer() {
        println("getInteger")
        val instance: AttributeSet = AttributeSet.Companion.of("a", "1")
        Assert.assertEquals(1, instance.getInteger("a", 2) as Int.toLong())
        Assert.assertEquals(2, instance.getInteger("b", 2) as Int.toLong())
    }

    @Test
    fun testGetFloat_String() {
        println("getFloat")
        val instance: AttributeSet = AttributeSet.Companion.of("a", "1", "b", 2f, "c", null).and("d", 3)
        Assert.assertEquals(1f as Float, instance.getFloat("a"))
        Assert.assertEquals(2f as Float, instance.getFloat("b"))
        Assert.assertNull(instance.getFloat("c"))
        Assert.assertEquals(3f as Float, instance.getFloat("d"))
        Assert.assertNull(instance.getFloat("e"))
    }

    @Test
    fun testGetFloat_String_Float() {
        println("getFloat")
        val instance: AttributeSet = AttributeSet.Companion.of("a", "1")
        Assert.assertEquals(1f as Float, instance.getFloat("a", 2f))
        Assert.assertEquals(2f as Float, instance.getFloat("b", 2f))
    }

    @Test
    fun testGetColor_String() {
        println("getColor")
        val instance: AttributeSet = AttributeSet.Companion.of("a", "red", "b", Color.red, "c", null).and("d", 1)
        Assert.assertEquals(Color.red, instance.getColor("a"))
        Assert.assertEquals(Color.red, instance.getColor("b"))
        Assert.assertNull(instance.getColor("c"))
        Assert.assertNull(instance.getColor("d"))
        Assert.assertNull(instance.getColor("e"))
    }

    @Test
    fun testGetColor_String_Color() {
        println("getColor")
        val instance: AttributeSet = AttributeSet.Companion.of("a", "red", "b", Color.red, "c", null).and("d", 1)
        Assert.assertEquals(Color.red, instance.getColor("a", Color.black))
        Assert.assertEquals(Color.red, instance.getColor("b", Color.black))
        Assert.assertEquals(Color.black, instance.getColor("c", Color.black))
        Assert.assertEquals(Color.black, instance.getColor("d", Color.black))
        Assert.assertEquals(Color.black, instance.getColor("e", Color.black))
    }

    @Test
    fun testGetPoint_String() {
        println("getPoint")
        val instance: AttributeSet = AttributeSet.Companion.of("a", Point(1, 2),
                "b", Color.red, "c", null, "d", "(1,2)")
        Assert.assertNull(instance.getPoint2D("b"))
        Assert.assertNull(instance.getPoint2D("c"))
        Assert.assertNull(instance.getPoint2D("e"))
        Assert.assertEquals(Point(1, 2), instance.getPoint("a"))
        Assert.assertEquals(Point(1, 2), instance.getPoint("d"))
        Assert.assertEquals(Point(1, 2), instance.getPoint2D("a"))
        Assert.assertEquals(Point(1, 2), instance.getPoint2D("d"))
    }

    @Test
    fun testGetPoint_String_Point2D() {
        println("getPoint")
        val instance: AttributeSet = AttributeSet.Companion.of("a", Point(1, 2), "b", Color.red, "c", null, "d", "(1,2)")
        val def = Point(3, 4)
        Assert.assertEquals(def, instance.getPoint2D("b", def))
        Assert.assertEquals(def, instance.getPoint2D("c", def))
        Assert.assertEquals(def, instance.getPoint2D("e", def))
        Assert.assertEquals(Point(1, 2), instance.getPoint("a", def))
        Assert.assertEquals(Point(1, 2), instance.getPoint("d", def))
        Assert.assertEquals(Point(1, 2), instance.getPoint2D("a", def))
        Assert.assertEquals(Point(1, 2), instance.getPoint2D("d", def))
    } //endregion
}