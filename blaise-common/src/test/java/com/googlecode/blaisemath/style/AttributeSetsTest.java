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
import java.awt.Color;
import java.util.Collections;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author petereb1
 */
public class AttributeSetsTest {

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
