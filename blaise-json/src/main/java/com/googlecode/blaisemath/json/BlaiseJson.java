package com.googlecode.blaisemath.json;

/*-
 * #%L
 * blaise-json
 * --
 * Copyright (C) 2019 - 2025 Elisha Peterson
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.Range;
import com.googlecode.blaisemath.palette.Palette;
import com.googlecode.blaisemath.style.AttributeSet;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Provides modules and custom coders/registration for Blaise, Java AWT, Guava objects.
 * @author Elisha Peterson
 */
public class BlaiseJson {

    private BlaiseJson() {
    }

    /**
     * Get a module configured with all serializers/deserializers available in this package.
     * @return module
     */
    public static SimpleModule allModule() {
        SimpleModule sm = new SimpleModule();
        sm.addSerializer(Range.class, new RangeSerializer());
        sm.addDeserializer(Range.class, new RangeDeserializer());
        sm.addSerializer(Class.class, new ClassSerializer());
        sm.addDeserializer(Class.class, new ClassDeserializer());
        sm.addSerializer(Color.class, new ColorSerializer());
        sm.addDeserializer(Color.class, new ColorDeserializer());
        sm.addSerializer(Rectangle.class, new RectangleSerializer());
        sm.addDeserializer(Rectangle.class, new RectangleDeserializer());
        sm.addSerializer(Rectangle2D.Double.class, new Rectangle2DSerializer());
        sm.addDeserializer(Rectangle2D.Double.class, new Rectangle2DDeserializer());
        sm.addDeserializer(Insets.class, new InsetsDeserializer());
        sm.addSerializer(Point.class, new PointSerializer());
        sm.addDeserializer(Point.class, new PointDeserializer());
        sm.addDeserializer(Point2D.class, new Point2DDeserializer());
        sm.addSerializer(Font.class, new FontSerializer());
        sm.addDeserializer(Font.class, new FontDeserializer());
        sm.addSerializer(AttributeSet.class, new AttributeSetSerializer());
        sm.addDeserializer(AttributeSet.class, new AttributeSetDeserializer());
        sm.addSerializer(Palette.class, new PaletteSerializer());
        sm.addDeserializer(Palette.class, new PaletteDeserializer());
        return sm;
    }

    /**
     * Get a mapper configured with all serializers/deserializers available in this package.
     * @return mapper
     */
    public static ObjectMapper allMapper() {
        ObjectMapper m = new ObjectMapper();
        m.registerModule(allModule());
        return m;
    }
    
    /**
     * Get a writer with all serializers/deserializers available in this package.
     * @return writer
     */
    public static ObjectWriter writerWithDefaultPrettyPrinter() {
        return allMapper().writerWithDefaultPrettyPrinter();
    }

    /**
     * Get a module configured with java.awt serializers/deserializers available in this package.
     * @return module
     */
    public static SimpleModule awtModule() {
        SimpleModule sm = new SimpleModule();
        sm.addSerializer(Color.class, new ColorSerializer());
        sm.addDeserializer(Color.class, new ColorDeserializer());
        sm.addSerializer(Rectangle.class, new RectangleSerializer());
        sm.addDeserializer(Rectangle.class, new RectangleDeserializer());
        sm.addSerializer(Rectangle2D.class, new Rectangle2DSerializer());
        sm.addDeserializer(Insets.class, new InsetsDeserializer());
        sm.addSerializer(Point.class, new PointSerializer());
        sm.addDeserializer(Point.class, new PointDeserializer());
        sm.addDeserializer(Point2D.Double.class, new Point2DDeserializer());
        sm.addSerializer(Font.class, new FontSerializer());
        sm.addDeserializer(Font.class, new FontDeserializer());
        return sm;
    }

    /**
     * Get a mapper configured with java.awt serializers/deserializers available in this package.
     * @return mapper
     */
    public static ObjectMapper awtMapper() {
        ObjectMapper m = new ObjectMapper();
        m.registerModule(awtModule());
        return m;
    }
    
}
