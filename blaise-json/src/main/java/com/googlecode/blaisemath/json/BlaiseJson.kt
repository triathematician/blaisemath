package com.googlecode.blaisemath.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.databind.module.SimpleModule
import com.google.common.collect.Range
import com.googlecode.blaisemath.palette.Palette
import com.googlecode.blaisemath.style.AttributeSet
import java.awt.*
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D

/*-
* #%L
* blaise-json
* --
* Copyright (C) 2019 - 2021 Elisha Peterson
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
*/ /**
 * Provides modules and custom coders/registration for Blaise, Java AWT, Guava objects.
 * @author Elisha Peterson
 */
object BlaiseJson {
    /**
     * Get a module configured with all serializers/deserializers available in this package.
     * @return module
     */
    fun allModule(): SimpleModule? {
        val sm = SimpleModule()
        sm.addSerializer(Range::class.java, RangeSerializer())
        sm.addDeserializer(Range::class.java, RangeDeserializer())
        sm.addSerializer(Class::class.java, ClassSerializer())
        sm.addDeserializer(Class::class.java, ClassDeserializer())
        sm.addSerializer(Color::class.java, ColorSerializer())
        sm.addDeserializer(Color::class.java, ColorDeserializer())
        sm.addSerializer(Rectangle::class.java, RectangleSerializer())
        sm.addDeserializer(Rectangle::class.java, RectangleDeserializer())
        sm.addSerializer(Rectangle2D.Double::class.java, Rectangle2DSerializer())
        sm.addDeserializer(Rectangle2D.Double::class.java, Rectangle2DDeserializer())
        sm.addDeserializer(Insets::class.java, InsetsDeserializer())
        sm.addSerializer(Point::class.java, PointSerializer())
        sm.addDeserializer(Point::class.java, PointDeserializer())
        sm.addDeserializer(Point2D::class.java, Point2DDeserializer())
        sm.addSerializer(Font::class.java, FontSerializer())
        sm.addDeserializer(Font::class.java, FontDeserializer())
        sm.addSerializer(AttributeSet::class.java, AttributeSetSerializer())
        sm.addDeserializer(AttributeSet::class.java, AttributeSetDeserializer())
        sm.addSerializer(Palette::class.java, PaletteSerializer())
        sm.addDeserializer(Palette::class.java, PaletteDeserializer())
        return sm
    }

    /**
     * Get a mapper configured with all serializers/deserializers available in this package.
     * @return mapper
     */
    fun allMapper(): ObjectMapper? {
        val m = ObjectMapper()
        m.registerModule(allModule())
        return m
    }

    /**
     * Get a writer with all serializers/deserializers available in this package.
     * @return writer
     */
    fun writerWithDefaultPrettyPrinter(): ObjectWriter? {
        return allMapper().writerWithDefaultPrettyPrinter()
    }

    /**
     * Get a module configured with java.awt serializers/deserializers available in this package.
     * @return module
     */
    fun awtModule(): SimpleModule? {
        val sm = SimpleModule()
        sm.addSerializer(Color::class.java, ColorSerializer())
        sm.addDeserializer(Color::class.java, ColorDeserializer())
        sm.addSerializer(Rectangle::class.java, RectangleSerializer())
        sm.addDeserializer(Rectangle::class.java, RectangleDeserializer())
        sm.addSerializer(Rectangle2D::class.java, Rectangle2DSerializer())
        sm.addDeserializer(Insets::class.java, InsetsDeserializer())
        sm.addSerializer(Point::class.java, PointSerializer())
        sm.addDeserializer(Point::class.java, PointDeserializer())
        sm.addDeserializer(Point2D.Double::class.java, Point2DDeserializer())
        sm.addSerializer(Font::class.java, FontSerializer())
        sm.addDeserializer(Font::class.java, FontDeserializer())
        return sm
    }

    /**
     * Get a mapper configured with java.awt serializers/deserializers available in this package.
     * @return mapper
     */
    fun awtMapper(): ObjectMapper? {
        val m = ObjectMapper()
        m.registerModule(awtModule())
        return m
    }
}