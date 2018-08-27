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


import com.google.common.base.Converter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author petereb1
 */
public class AttributeSetCoderTest {
    
    private AttributeSetCoder inst = new AttributeSetCoder();
    
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
    }

    private static Converter<AttributeSet, String> CONV;
    private static Converter<String, AttributeSet> REV;
    private static Converter<Object, String> VAL_CONV;
    private static Converter<String, Object> VAL_REV;
    private static Converter<AttributeSet, String> TYPED;
    private static Converter<String, AttributeSet> TYPED_REV;

    @BeforeClass
    public static void setUpClass() throws Exception {
        CONV = AttributeSets.stringConverter();
        REV = CONV.reverse();
        VAL_CONV = AttributeSets.valueConverter();
        VAL_REV = VAL_CONV.reverse();
        
        Map<String,Class<?>> types = Maps.newHashMap();
        for (Class c : new Class[] { Boolean.class, 
            Integer.class, Float.class, Double.class, 
            Point.class, Rectangle.class, 
            Font.class, Color.class,
            Marker.class
        }) {
            types.put(c.getSimpleName(), c);
        }
        TYPED = AttributeSets.stringConverter(types);
        TYPED_REV = TYPED.reverse();
    }
    
    //<editor-fold defaultstate="collapsed" desc="VALUE CONVERSIONS">
    
    @Test
    public void testConvertNull() {
        System.out.println("testConvertNull");

        assertEquals(null, VAL_CONV.convert(null));
        assertEquals(null, VAL_REV.convert(null));
    }
    
    @Test
    public void testConvertString() {
        System.out.println("testConvertString");

        assertEquals("x", VAL_CONV.convert("x"));
        assertEquals("x", VAL_REV.convert("x"));
    }
    
    @Test
    public void testConvertBoolean() {
        System.out.println("testConvertBoolean");

        assertEquals("true", VAL_CONV.convert(true));
        assertEquals("true", VAL_REV.convert("true"));
        assertEquals(true, TYPED_REV.convert("Boolean: true").get("Boolean"));
        assertEquals(false, TYPED_REV.convert("Boolean: whatever").get("Boolean"));
    }
    
    @Test
    public void testConvertInteger() {
        System.out.println("testConvertInteger");

        assertEquals("4", VAL_CONV.convert(4));
        assertEquals(4, VAL_REV.convert("4"));
        assertEquals(5, TYPED_REV.convert("Integer: 5").get("Integer"));
    }
    
    @Test
    public void testConvertFloat() {
        System.out.println("testConvertFloat");

        assertEquals("4.0", VAL_CONV.convert(4f));
        assertEquals(4.0, VAL_REV.convert("4.0"));
        assertEquals(4f, TYPED_REV.convert("Float: 4").get("Float"));
    }
    
    @Test
    public void testConvertDouble() {
        System.out.println("testConvertDouble");

        assertEquals("4.0", VAL_CONV.convert(4.0));
        assertEquals(4.0, VAL_REV.convert("4.0"));
        assertEquals(4.0, TYPED_REV.convert("Double: 4").get("Double"));
    }
    
    @Test
    public void testConvertPoint() {
        System.out.println("testConvertPoint");

        assertEquals("!point[5.000000,6.000000]", VAL_CONV.convert(new Point2D.Double(5,6)));
        assertEquals("!point[5,6]", VAL_CONV.convert(new Point(5,6)));
        assertEquals(new Point2D.Double(5, 6), VAL_REV.convert("!point[5,6]"));
        assertEquals(new Point(5, 6), TYPED_REV.convert("Point: !point[5,6]").get("Point"));
    }
    
    @Test
    public void testConvertRect() {
        System.out.println("testConvertRect");
        
        assertEquals("!rectangle[x=5.000000,y=6.000000,w=7.000000,h=8.000000]", VAL_CONV.convert(new Rectangle(5, 6, 7, 8)));
        assertEquals("!rectangle[x=5.000000,y=6.000000,w=7.000000,h=8.000000]", VAL_CONV.convert(new Rectangle2D.Double(5, 6, 7, 8)));
        assertEquals(new Rectangle2D.Double(5, 6, 7, 8), VAL_REV.convert("!rectangle[x=5,y=6,w=7,h=8]"));
        assertEquals(new Rectangle(5, 6, 7, 8), TYPED_REV.convert("Rectangle: !rectangle[x=5,y=6,w=7,h=8]").get("Rectangle"));
    }
    
    //</editor-fold>
    
    /**
     * Test of stringConverter method, of class AttributeSets.
     */
    @Test
    public void testStringConverter_0args() {
        System.out.println("stringConverter");
        Converter<AttributeSet, String> result = AttributeSets.stringConverter();

        assertEquals("fill:#ffffff", result.convert(AttributeSet.of("fill", Color.white)));
        assertEquals(ImmutableMap.of("fill", Color.white), result.reverse().convert("fill:#ffffff").getAttributeMap());
        assertEquals(ImmutableMap.of("fill", Color.white), result.reverse().convert("fill:#fff").getAttributeMap());

        assertEquals("fill:none", result.convert(AttributeSet.of("fill", null)));
        assertEquals(nullMap("fill"), result.reverse().convert("fill:none").getAttributeMap());
    }
    
    private static Map<String,Object> nullMap(String key) {
        return Collections.singletonMap(key, null);
    }

    /**
     * Test of stringConverter method, of class AttributeSets.
     */
    @Test
    public void testStringConverter_Map() {
        System.out.println("stringConverter");
        Converter<AttributeSet, String> result = AttributeSets.stringConverter(
                ImmutableMap.<String,Class<?>>of("fill", String.class));

        assertEquals("fill:#ffffff", result.convert(AttributeSet.of("fill", Color.white)));
        assertEquals(ImmutableMap.of("fill", "#ffffff"), result.reverse().convert("fill:#ffffff").getAttributeMap());
    }

    /**
     * Test of valueConverter method, of class AttributeSets.
     */
    @Test
    public void testValueConverter() {
        System.out.println("valueConverter");
        Converter<Object, String> result = AttributeSets.valueConverter();

        assertEquals("#ffffff", result.convert(Color.white));
        assertEquals(Color.white, result.reverse().convert("#ffffff"));
        
        assertEquals(null, result.convert(null));
        assertEquals("none", result.reverse().convert("none"));
    }

    /**
     * Test of valueFromString method, of class AttributeSets.
     */
    @Test
    public void testValueFromString() {
        System.out.println("valueFromString");
        
        assertEquals(Color.white, AttributeSets.valueFromString("#ffffff"));
        assertEquals(null, AttributeSets.valueFromString("none"));
    }
    
}
