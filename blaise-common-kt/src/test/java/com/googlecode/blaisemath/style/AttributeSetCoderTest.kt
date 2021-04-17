package com.googlecode.blaisemath.style
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
 */

import com.googlecode.blaisemath.style.AttributeSetCoder.Companion.NULL_STRING
import com.googlecode.blaisemath.util.Colors.alpha
import com.googlecode.blaisemath.util.geom.point2
import com.googlecode.blaisemath.util.geom.rectangle2
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import java.awt.Color
import java.awt.Font
import java.awt.Point
import java.awt.Rectangle

class AttributeSetCoderTest {

    private val inst = AttributeSetCoder()
    private val typedInst = AttributeSetCoder(
            listOf(java.lang.Boolean::class.java, java.lang.Integer::class.java, java.lang.Float::class.java, java.lang.Double::class.java,
                    Point::class.java, Rectangle::class.java, Font::class.java, Color::class.java, Marker::class.java)
                    .map { it.simpleName to it }.toMap())

    @Test
    fun testEncode() {
        assertEquals("fill:#ff0000; stroke:#00ff00", inst.encode(AttributeSet("fill" to Color.red, "stroke" to Color.green)))
    }

    @Test
    fun testDecode() {
        val `as` = inst.decode("fill:  #ff0000 ; stroke :#00ff00;")
        assertEquals(2, `as`.attributes.size.toLong())
        assertEquals(Color.red, `as`["fill"])
        assertEquals(Color.green, `as`["stroke"])
        assertEquals(Color.green, inst.decode("fill: red; fill: lime").getColor("fill"))
    }

    @Test
    fun testConvertNull() {
        assertEquals("none", AttributeSetCoder.encodeValue(null))
        Assert.assertNull(AttributeSetCoder.decodeValue("none"))
    }

    @Test
    fun testConvertString() {
        assertEquals("x", AttributeSetCoder.encodeValue("x"))
        assertEquals("x", AttributeSetCoder.decodeValue("x"))
    }

    @Test
    fun testConvertColor() {
        assertEquals("#ff0000", AttributeSetCoder.encodeValue(Color.red))
        assertEquals("#ff000080", AttributeSetCoder.encodeValue(Color.red.alpha(128)))
        assertEquals(Color.red, AttributeSetCoder.decodeValue("#ff0000"))
        assertEquals(Color.red, AttributeSetCoder.decodeValue("#f00"))
        assertEquals("red", AttributeSetCoder.decodeValue("red"))
        assertEquals(Color.red, AttributeSetCoder.decodeValue("red", Color::class.java))
    }

    @Test
    fun testConvertBoolean() {
        assertEquals("true", AttributeSetCoder.encodeValue(true))
        assertEquals("true", AttributeSetCoder.encodeValue("true"))
        assertEquals(true, typedInst.decode("Boolean: true")["Boolean"])
        assertEquals(false, typedInst.decode("Boolean: whatever")["Boolean"])
    }

    @Test
    fun testConvertInteger() {
        assertEquals("4", AttributeSetCoder.encodeValue(4))
        assertEquals(4, AttributeSetCoder.decodeValue("4"))
        assertEquals(5, typedInst.decode("Integer: 5")["Integer"])
    }

    @Test
    fun testConvertFloat() {
        assertEquals("4.0", AttributeSetCoder.encodeValue(4f))
        assertEquals(4.0, AttributeSetCoder.decodeValue("4.0"))
        assertEquals(4f, typedInst.decode("Float: 4")["Float"])
    }

    @Test
    fun testConvertDouble() {
        assertEquals("4.0", AttributeSetCoder.encodeValue(4.0))
        assertEquals(4.0, AttributeSetCoder.decodeValue("4.0"))
        assertEquals(4.0, typedInst.decode("Double: 4")["Double"])
    }

    @Test
    fun testConvertPoint() {
        assertEquals("(5.000000,6.000000)", AttributeSetCoder.encodeValue(point2(5, 6)))
        assertEquals("(5,6)", AttributeSetCoder.encodeValue(Point(5, 6)))
        assertEquals(point2(5, 6), AttributeSetCoder.decodeValue("(5.0,6.0)"))
        assertEquals(Point(5, 6), typedInst.decode("Point: (5,6)")["Point"])
    }

    @Test
    fun testConvertRect() {
        assertEquals("rectangle(5,6,7,8)", AttributeSetCoder.encodeValue(Rectangle(5, 6, 7, 8)))
        assertEquals("rectangle2d(5.000000,6.000000,7.000000,8.000000)", AttributeSetCoder.encodeValue(rectangle2(5, 6, 7, 8)))
        assertEquals(rectangle2(5, 6, 7, 8), AttributeSetCoder.decodeValue("rectangle2d(5,6,7,8) "))
        assertEquals(Rectangle(5, 6, 7, 8), typedInst.decode("Rectangle: rectangle(5,6,7,8)")["Rectangle"])
    }

    @Test
    fun testEncodeDecode1() {
        assertEquals("fill:#ffffff", inst.encode(AttributeSet("fill" to Color.white)))
        assertEquals(mapOf("fill" to Color.white), inst.decode("fill:#ffffff").attributeMap)
        assertEquals(mapOf("fill" to Color.white), inst.decode("fill:#fff").attributeMap)
        assertEquals("fill:none", inst.encode(AttributeSet("fill" to null)))
        assertEquals(mapOf("fill" to null), inst.decode("fill:none").attributeMap)
    }

    @Test
    fun testEncodeDecode2() {
        val result = AttributeSetCoder(
                mapOf("fill" to String::class.java))
        assertEquals("fill:#ffffff", result.encode(AttributeSet("fill" to Color.white)))
        assertEquals(mapOf("fill" to "#ffffff"), result.decode("fill:#ffffff").attributeMap)
    }
}