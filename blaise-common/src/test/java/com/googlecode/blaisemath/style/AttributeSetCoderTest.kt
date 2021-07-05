package com.googlecode.blaisemath.style

import com.google.common.collect.ImmutableMap
import com.googlecode.blaisemath.primitive.Marker
import com.googlecode.blaisemath.util.Colors
import org.junit.Assert
import org.junit.Test
import java.awt.Color
import java.awt.Font
import java.awt.Point
import java.awt.Rectangle
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Stream

/*
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
*/   class AttributeSetCoderTest {
    private val inst: AttributeSetCoder? = AttributeSetCoder()
    private val typedInst: AttributeSetCoder? = AttributeSetCoder(
            Stream.of(Boolean::class.java, Int::class.java, Float::class.java, Double::class.java,
                    Point::class.java, Rectangle::class.java, Font::class.java, Color::class.java, Marker::class.java)
                    .collect(Collectors.toMap(Function { obj: Class<*>? -> obj.getSimpleName() }, Function { c: Class<*>? -> c })))

    @Test
    fun testEncode() {
        Assert.assertEquals("fill:#ff0000; stroke:#00ff00", inst.encode(AttributeSet.Companion.of("fill", Color.red, "stroke", Color.green)))
    }

    @Test
    fun testDecode() {
        val `as` = inst.decode("fill:  #ff0000 ; stroke :#00ff00;")
        Assert.assertEquals(2, `as`.attributes.size.toLong())
        Assert.assertEquals(Color.red, `as`["fill"])
        Assert.assertEquals(Color.green, `as`["stroke"])
        Assert.assertEquals(Color.green, inst.decode("fill: red; fill: lime").getColor("fill"))
    }

    //<editor-fold defaultstate="collapsed" desc="VALUE CONVERSIONS">
    @Test
    fun testConvertNull() {
        Assert.assertEquals("none", AttributeSetCoder.Companion.encodeValue(null))
        Assert.assertNull(AttributeSetCoder.Companion.decodeValue<Any?>("none", Any::class.java))
        try {
            AttributeSetCoder.Companion.decodeValue<Any?>(null, Any::class.java)
            Assert.fail("Expected NPE")
        } catch (x: NullPointerException) {
            // expected
        }
    }

    @Test
    fun testConvertString() {
        Assert.assertEquals("x", AttributeSetCoder.Companion.encodeValue("x"))
        Assert.assertEquals("x", AttributeSetCoder.Companion.decodeValue<Any?>("x", Any::class.java))
    }

    @Test
    fun testConvertColor() {
        Assert.assertEquals("#ff0000", AttributeSetCoder.Companion.encodeValue(Color.red))
        Assert.assertEquals("#ff000080", AttributeSetCoder.Companion.encodeValue(Colors.alpha(Color.red, 128)))
        Assert.assertEquals(Color.red, AttributeSetCoder.Companion.decodeValue<Any?>("#ff0000", Any::class.java))
        Assert.assertEquals(Color.red, AttributeSetCoder.Companion.decodeValue<Any?>("#f00", Any::class.java))
        Assert.assertEquals("red", AttributeSetCoder.Companion.decodeValue<Any?>("red", Any::class.java))
        Assert.assertEquals(Color.red, AttributeSetCoder.Companion.decodeValue<Color?>("red", Color::class.java))
    }

    @Test
    fun testConvertBoolean() {
        Assert.assertEquals("true", AttributeSetCoder.Companion.encodeValue(true))
        Assert.assertEquals("true", AttributeSetCoder.Companion.encodeValue("true"))
        Assert.assertEquals(true, typedInst.decode("Boolean: true")["Boolean"])
        Assert.assertEquals(false, typedInst.decode("Boolean: whatever")["Boolean"])
    }

    @Test
    fun testConvertInteger() {
        println("testConvertInteger")
        Assert.assertEquals("4", AttributeSetCoder.Companion.encodeValue(4))
        Assert.assertEquals(4, AttributeSetCoder.Companion.decodeValue<Any?>("4", Any::class.java))
        Assert.assertEquals(5, typedInst.decode("Integer: 5")["Integer"])
    }

    @Test
    fun testConvertFloat() {
        println("testConvertFloat")
        Assert.assertEquals("4.0", AttributeSetCoder.Companion.encodeValue(4f))
        Assert.assertEquals(4.0, AttributeSetCoder.Companion.decodeValue<Any?>("4.0", Any::class.java))
        Assert.assertEquals(4f, typedInst.decode("Float: 4")["Float"])
    }

    @Test
    fun testConvertDouble() {
        Assert.assertEquals("4.0", AttributeSetCoder.Companion.encodeValue(4.0))
        Assert.assertEquals(4.0, AttributeSetCoder.Companion.decodeValue<Any?>("4.0", Any::class.java))
        Assert.assertEquals(4.0, typedInst.decode("Double: 4")["Double"])
    }

    @Test
    fun testConvertPoint() {
        Assert.assertEquals("(5.000000,6.000000)", AttributeSetCoder.Companion.encodeValue(Point2D.Double(5, 6)))
        Assert.assertEquals("(5,6)", AttributeSetCoder.Companion.encodeValue(Point(5, 6)))
        Assert.assertEquals(Point2D.Double(5, 6), AttributeSetCoder.Companion.decodeValue<Any?>("(5.0,6.0)", Any::class.java))
        Assert.assertEquals(Point(5, 6), typedInst.decode("Point: (5,6)")["Point"])
    }

    @Test
    fun testConvertRect() {
        Assert.assertEquals("rectangle(5,6,7,8)", AttributeSetCoder.Companion.encodeValue(Rectangle(5, 6, 7, 8)))
        Assert.assertEquals("rectangle2d(5.000000,6.000000,7.000000,8.000000)", AttributeSetCoder.Companion.encodeValue(Rectangle2D.Double(5, 6, 7, 8)))
        Assert.assertEquals(Rectangle2D.Double(5, 6, 7, 8), AttributeSetCoder.Companion.decodeValue<Any?>("rectangle2d(5,6,7,8) ", Any::class.java))
        Assert.assertEquals(Rectangle(5, 6, 7, 8), typedInst.decode("Rectangle: rectangle(5,6,7,8)")["Rectangle"])
    }

    //endregion
    @Test
    fun testEncodeDecode1() {
        Assert.assertEquals("fill:#ffffff", inst.encode(AttributeSet.Companion.of("fill", Color.white)))
        Assert.assertEquals(ImmutableMap.of("fill", Color.white), inst.decode("fill:#ffffff").getAttributeMap())
        Assert.assertEquals(ImmutableMap.of("fill", Color.white), inst.decode("fill:#fff").getAttributeMap())
        Assert.assertEquals("fill:none", inst.encode(AttributeSet.Companion.of("fill", null)))
        Assert.assertEquals(nullMap("fill"), inst.decode("fill:none").getAttributeMap())
    }

    @Test
    fun testEncodeDecode2() {
        val result = AttributeSetCoder(
                ImmutableMap.of<String?, Class<*>?>("fill", String::class.java))
        Assert.assertEquals("fill:#ffffff", result.encode(AttributeSet.Companion.of("fill", Color.white)))
        Assert.assertEquals(ImmutableMap.of("fill", "#ffffff"), result.decode("fill:#ffffff").getAttributeMap())
    }

    companion object {
        private fun nullMap(key: String?): MutableMap<String?, Any?>? {
            return Collections.singletonMap(key, null)
        }
    }
}