/**
 * AttributeSetAdapter.java
 * Created Sep 27, 2014
 */
package com.googlecode.blaisemath.util.xml;

/*
 * #%L
 * BlaiseSVG
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
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

import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.AttributeSetCoder;
import com.googlecode.blaisemath.style.Styles;
import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * <p>
 *   Allows representing {@link AttributeSet}s in XML as strings.
 * </p>
 * @see AttributeSets#stringConverter() 
 * @author elisha
 */
public final class AttributeSetAdapter extends XmlAdapter<String, AttributeSet> {
    private static final Logger LOG = Logger.getLogger(AttributeSetAdapter.class.getName());

    private static final AttributeSetCoder INST = new AttributeSetCoder();
    
    private static final String[] COLOR_KEYS = { Styles.FILL, Styles.STROKE };
    private static final String[] NUMBER_KEYS = { Styles.STROKE_WIDTH };
    
    @Override
    public AttributeSet unmarshal(String v) {
        AttributeSet res = INST.decode(v);
        if (res != null) {
            updateColorFields(res);
            updateNumberFields(res);
        }
        return res;
    }

    @Override
    public String marshal(AttributeSet v) {
        return INST.encode(v);
    }
    
    /**
     * Update attribute set so fields that are typically used for color
     * are decoded appropriately. Currently supports "fill" and "stroke", and
     * allows using strings for color names.
     * @param style attribute set
     */
    public static void updateColorFields(AttributeSet style) {
        for (String s : COLOR_KEYS) {
            if (style.get(s) instanceof String) {
                Color color = decodeColorString((String) style.get(s));
                if (color != null) {
                    style.put(s, color);
                }
            }
        }
    }
    
    /**
     * Update numeric fields ending in "px" to remove the suffix, and make
     * them numbers.
     * @param style attribute set
     */
    public static void updateNumberFields(AttributeSet style) {
        for (String s : NUMBER_KEYS) {
            Object value = style.get(s);
            if (value instanceof String && ((String) value).endsWith("px")) {
                String sval = (String) value;
                sval = sval.substring(0, sval.length()-2);
                try {
                    float f = Float.valueOf(sval);
                    style.put(s, f);
                } catch (NumberFormatException ex) {
                    LOG.log(Level.FINE, null, ex);
                }
            }
        }
    }

    private static Color decodeColorString(String s) {
        String name = s.toLowerCase();
        try {
            return (Color) Color.class.getField(name).get(null);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            LOG.log(Level.FINE, null, ex);
        }
        return null;
    }

}
