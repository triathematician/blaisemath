package com.googlecode.blaisemath.svg.reader;

/*-
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2019 Elisha Peterson
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
import com.googlecode.blaisemath.util.Colors;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StyleReader {

    private static final Logger LOG = Logger.getLogger(StyleReader.class.getName());
    private static final String[] COLOR_KEYS = { Styles.FILL, Styles.STROKE };

    /**
     * Create style object instance from SVG style string.
     * @param style SVG style string
     * @return new style instance
     */
    public static AttributeSet fromString(String style) {
        AttributeSet res = new AttributeSetCoder().decode(style);
        updateColorFields(res);
        return res;
    }

    /**
     * Update attribute set so fields that are typically used for color
     * are decoded appropriately. Currently supports "fill" and "stroke", and
     * allows using strings for color names.
     * @param style attribute set
     */
    static void updateColorFields(AttributeSet style) {
        for (String s : COLOR_KEYS) {
            if (style.get(s) instanceof String) {
                String ss = ((String) style.get(s)).trim();
                if (!"null".equalsIgnoreCase(ss) && !"none".equalsIgnoreCase(ss)) {
                    try {
                        Color color = Colors.decode((String) style.get(s));
                        if (color != null) {
                            style.put(s, color);
                        }
                    } catch (IllegalArgumentException x) {
                        LOG.log(Level.FINE, "Invalid color string: "+ss);
                    }
                }
            }
        }
    }

}
