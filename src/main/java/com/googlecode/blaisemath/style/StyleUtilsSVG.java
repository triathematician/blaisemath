/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.blaisemath.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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


import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Maps;
import com.googlecode.blaisemath.style.AttributeSet;
import java.awt.Color;
import java.beans.IntrospectionException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;

/**
 * Convert styles to/from SVG.
 * @author Elisha
 */
public class StyleUtilsSVG {
    
    // utility class
    private StyleUtilsSVG() {
    }

    /** 
     * Convert style class to an SVG-compatible string.
     * @param style the style object
     * @return string representation of the style, in SVG format
     * @throws java.beans.IntrospectionException if introspection on the style class fails
     */    
    public static String convertStyleToSVG(@Nonnull AttributeSet style) throws IntrospectionException {
        checkNotNull(style);
        Map<String,String> props = Maps.newTreeMap();
        for (String s : style.getAttributes()) {
            Object styleValue = style.get(s);
            props.put(convertKeyToSVG(s), convertValueToSVG(styleValue));
        }
        return Joiner.on("; ").withKeyValueSeparator(":").join(props);
    }
    
    /** 
     * Convert property keys via CamelCase, like "fontSize", to SVG-like keys like "font-size".
     * @param key a property name
     * @return svg-style version of the key
     */
    public static String convertKeyToSVG(String key) {
        Pattern p = Pattern.compile("([a-z]+)([A-Z])");
        Matcher m = p.matcher(key);
        StringBuilder res = new StringBuilder();
        int last = 0;
        while (m.find()) {
            res.append(m.group(1)).append("-").append(m.group(2).toLowerCase());
            last = m.end();
        }
        res.append(key.substring(last));
        return res.toString();
    }
    
    /** 
     * Convert values to SVG value strings.
     * @param val a value object
     * @return string representation of the value
     * @throws UnsupportedOperationException if the value type is not convertible
     */
    public static String convertValueToSVG(Object val) {
        if (val instanceof Color) {
            Color c = (Color) val;
            String col = String.format("%8h", c.getRGB());
            if (col.startsWith("ff")) {
                return "#"+col.substring(2);
            } else {
                return "#"+col;
            }
        } else if (val instanceof Number) {
            return val.toString();
        } else if (val instanceof String) {
            return (String) val;
        } else {
            throw new UnsupportedOperationException("Unsupported value: " + val);
        }
    }

    
}
