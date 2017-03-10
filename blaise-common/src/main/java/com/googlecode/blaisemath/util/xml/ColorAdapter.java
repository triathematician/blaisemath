/*
 * ColorAdapter.java
 * Created on May 7, 2013
 */

package com.googlecode.blaisemath.util.xml;

/*
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2017 Elisha Peterson
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
import com.google.common.base.Strings;
import com.googlecode.blaisemath.util.Colors;
import java.awt.Color;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Adapter converting colors to/from hex strings. Supports #AARRGGBB and #RRGGBB notations.
 * Uses {@link Colors#stringConverter()} to perform the conversion, but provides
 * additional flexibility for handling nulls.
 *
 * @see Color#decode(java.lang.String)
 *
 * @author Elisha Peterson
 */
public final class ColorAdapter extends XmlAdapter<String, Color> {
    
    private static final Converter<Color, String> CONV = Colors.stringConverter();

    @Override
    public Color unmarshal(String v) {
        if (Strings.isNullOrEmpty(v) || "null".equals(v)) {
            return null;
        }
        return CONV.reverse().convert(v);
    }

    @Override
    public String marshal(Color c) {
        if (c == null) {
            return "null";
        }
        return CONV.convert(c);
    }
}
