package com.googlecode.blaisemath.style;

/*
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
import com.googlecode.blaisemath.util.Colors;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import static java.util.stream.Collectors.toMap;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author petereb1
 */
public class AttributeSetCoderTest {
    
    private final AttributeSetCoder inst = new AttributeSetCoder();
    private final AttributeSetCoder typedInst = new AttributeSetCoder(
            Arrays.asList(Boolean.class, Integer.class, Float.class, Double.class,
                    Point.class, Rectangle.class, Font.class, Color.class, Marker.class).stream()
                    .collect(toMap(Class::getSimpleName, c -> c)));
    
    @Test
    public void testEncode() throws Exception {
        System.out.println("testEncode");
        
        assertEquals("fill:#ff0000; stroke:#00ff00", inst.encode(AttributeSet.of("fill", Color.red, "stroke", Color.green)));
    }

    @Test
    public void testDecode() throws Exception {
        System.out.println("testDecode");
        
        AttributeSet as = inst.decode("fill:  #ff0000 ; stroke :#00ff00;");
        assertEquals(2, as.getAttributes().size());
        assertEquals(Color.red, as.get("fill"));
        assertEquals(Color.green, as.get("stroke"));
        
        assertEquals(Color.green, inst.decode("fill: red; fill: lime").getColor("fill"));
    }
    
    //<editor-fold defaultstate="collapsed" desc="VALUE CONVERSIONS">
    
    @Test
    public void testConvertNull() {
        System.out.println("testConvertNull");

        assertEquals("none", AttributeSetCoder.encodeValue(null));
        assertEquals(null, AttributeSetCoder.decodeValue("none", Object.class));
        try {
            AttributeSetCoder.decodeValue(null, Object.class);
            fail("Expected NPE");
        } catch (NullPointerException x) {
            // expected
        }
    }
    
    @Test
    public void testConvertString() {
        System.out.println("testConvertString");

        assertEquals("x", AttributeSetCoder.encodeValue("x"));
        assertEquals("x", AttributeSetCoder.decodeValue("x", Object.class));
    }
    
    @Test
    public void testConvertColor() {
        System.out.println("testConvertColor");

        assertEquals("#ff0000", AttributeSetCoder.encodeValue(Color.red));
        assertEquals("#ff000080", AttributeSetCoder.encodeValue(Colors.alpha(Color.red, 128)));
        assertEquals(Color.red, AttributeSetCoder.decodeValue("#ff0000", Object.class));
        assertEquals(Color.red, AttributeSetCoder.decodeValue("#f00", Object.class));
        assertEquals("red", AttributeSetCoder.decodeValue("red", Object.class));
        assertEquals(Color.red, AttributeSetCoder.decodeValue("red", Color.class));
    }
    
    @Test
    public void testConvertBoolean() {
        System.out.println("testConvertBoolean");

        assertEquals("true", AttributeSetCoder.encodeValue(true));
        assertEquals("true", AttributeSetCoder.encodeValue("true"));
        assertEquals(true, typedInst.decode("Boolean: true").get("Boolean"));
        assertEquals(false, typedInst.decode("Boolean: whatever").get("Boolean"));
    }
    
    @Test
    public void testConvertInteger() {
        System.out.println("testConvertInteger");

        assertEquals("4", AttributeSetCoder.encodeValue(4));
        assertEquals(4, AttributeSetCoder.decodeValue("4", Object.class));
        assertEquals(5, typedInst.decode("Integer: 5").get("Integer"));
    }
    
    @Test
    public void testConvertFloat() {
        System.out.println("testConvertFloat");

        assertEquals("4.0", AttributeSetCoder.encodeValue(4f));
        assertEquals(4.0, AttributeSetCoder.decodeValue("4.0", Object.class));
        assertEquals(4f, typedInst.decode("Float: 4").get("Float"));
    }
    
    @Test
    public void testConvertDouble() {
        System.out.println("testConvertDouble");

        assertEquals("4.0", AttributeSetCoder.encodeValue(4.0));
        assertEquals(4.0, AttributeSetCoder.decodeValue("4.0", Object.class));
        assertEquals(4.0, typedInst.decode("Double: 4").get("Double"));
    }
    
    @Test
    public void testConvertPoint() {
        System.out.println("testConvertPoint");

        assertEquals("(5.000000,6.000000)", AttributeSetCoder.encodeValue(new Point2D.Double(5,6)));
        assertEquals("(5,6)", AttributeSetCoder.encodeValue(new Point(5,6)));
        assertEquals(new Point2D.Double(5, 6), AttributeSetCoder.decodeValue("(5.0,6.0)", Object.class));
        assertEquals(new Point(5, 6), typedInst.decode("Point: (5,6)").get("Point"));
    }
    
    @Test
    public void testConvertRect() {
        System.out.println("testConvertRect");
        
        assertEquals("rectangle(5,6,7,8)", AttributeSetCoder.encodeValue(new Rectangle(5, 6, 7, 8)));
        assertEquals("rectangle2d(5.000000,6.000000,7.000000,8.000000)", AttributeSetCoder.encodeValue(new Rectangle2D.Double(5, 6, 7, 8)));
        assertEquals(new Rectangle2D.Double(5, 6, 7, 8), AttributeSetCoder.decodeValue("rectangle2d(5,6,7,8) ", Object.class));
        assertEquals(new Rectangle(5, 6, 7, 8), typedInst.decode("Rectangle: rectangle(5,6,7,8)").get("Rectangle"));
    }
    
    //</editor-fold>
    
    @Test
    public void testEncodeDecode1() {
        System.out.println("testEncodeDecode1");
        assertEquals("fill:#ffffff", inst.encode(AttributeSet.of("fill", Color.white)));
        assertEquals(ImmutableMap.of("fill", Color.white), inst.decode("fill:#ffffff").getAttributeMap());
        assertEquals(ImmutableMap.of("fill", Color.white), inst.decode("fill:#fff").getAttributeMap());

        assertEquals("fill:none", inst.encode(AttributeSet.of("fill", null)));
        assertEquals(nullMap("fill"), inst.decode("fill:none").getAttributeMap());
    }
    
    private static Map<String,Object> nullMap(String key) {
        return Collections.singletonMap(key, null);
    }

    @Test
    public void testEncodeDecode2() {
        System.out.println("testEncodeDecode2");
        AttributeSetCoder result = new AttributeSetCoder(
                ImmutableMap.<String,Class<?>>of("fill", String.class));

        assertEquals("fill:#ffffff", result.encode(AttributeSet.of("fill", Color.white)));
        assertEquals(ImmutableMap.of("fill", "#ffffff"), result.decode("fill:#ffffff").getAttributeMap());
    }
    
}
