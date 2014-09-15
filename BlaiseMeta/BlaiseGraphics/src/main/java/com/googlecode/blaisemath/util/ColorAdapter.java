/*
 * ColorAdapter.java
 * Created on May 7, 2013
 */

package com.googlecode.blaisemath.util;

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

import com.google.common.base.Strings;
import java.awt.Color;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Adapter converting colors to/from hex strings. Supports #AARRGGBB and #RRGGBB notations.
 *
 * @see Color#decode(java.lang.String)
 *
 * @author Elisha Peterson
 */
public final class ColorAdapter extends XmlAdapter<String, Color> {

    public static final ColorAdapter INST = new ColorAdapter();
    
    @Override
    public Color unmarshal(String v) {
        if (Strings.isNullOrEmpty(v) || "null".equals(v)) {
            return null;
        }
        if (!v.startsWith("#")) {
            v = "#"+v;
        }
        if (v.length() == 7) {
            // #RRGGBB
            return Color.decode(v);
        } else if (v.length() == 9) {
            // #AARRGGBB
            int alpha = Integer.decode(v.substring(0,3));
            int rgb = Integer.decode("#"+v.substring(3));
            return new Color((alpha << 24) + rgb, true);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public String marshal(Color c) {
        if (c == null) {
            return "null";
        } else if (c.getAlpha() == 255) {
            return String.format("#%02x%02x%02x", c.getRed(),c.getGreen(),c.getBlue());
        } else {
            return "#"+Integer.toHexString(c.getRGB());
        }
    }
}