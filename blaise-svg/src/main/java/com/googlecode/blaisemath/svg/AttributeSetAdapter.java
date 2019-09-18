package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseSVG
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
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

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.awt.*;
import java.util.logging.Logger;

/**
 * Converts attribute sets to/from strings for use in style sets.
 * @author elisha
 */
public final class AttributeSetAdapter extends XmlAdapter<String, AttributeSet> {

    private static final String[] COLOR_KEYS = { Styles.FILL, Styles.STROKE };

    private static final AttributeSetCoder CODER = new AttributeSetCoder();

    @Override
    public AttributeSet unmarshal(String v) {
        return v == null ? null : CODER.decode(v);
    }

    @Override
    public String marshal(AttributeSet v) {
        return v == null ? null : CODER.encode(v);
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
                Color color = Colors.decode((String) style.get(s));
                if (color != null) {
                    style.put(s, color);
                }
            }
        }
    }

}