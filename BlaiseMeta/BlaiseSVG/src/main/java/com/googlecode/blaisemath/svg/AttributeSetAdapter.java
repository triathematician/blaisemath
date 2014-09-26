/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.blaisemath.svg;

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
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.util.ColorAdapter;
import java.awt.Color;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Convert an {@link AttributeSet} to/from a string. The string is intended to be
 * compatible with html/css, but some features of the {@code AttributeSet} cannot
 * be encoded this way, so the operation is not invertible.
 * 
 * @author Elisha
 */
public final class AttributeSetAdapter extends XmlAdapter<String, AttributeSet> {
    
    public static final AttributeSetAdapter INST = new AttributeSetAdapter();
    public static final AttributeValueAdapter VALUE_ADAPTER = new AttributeValueAdapter();

    @Override
    public AttributeSet unmarshal(String str) {
        if (Strings.isNullOrEmpty(str)) {
            return new AttributeSet();
        }
        AttributeSet res = new AttributeSet();
        Map<String, String> vals = Splitter.on(";").trimResults().withKeyValueSeparator(":").split(str);
        for (String key : vals.keySet()) {
            String sval = vals.get(key);
            Object val = VALUE_ADAPTER.unmarshal(sval);
            res.put(key, val);
        }
        return res;
    }

    @Override
    public String marshal(AttributeSet style) {
        if (style == null) {
            return null;
        }
        Map<String,String> props = Maps.newTreeMap();
        for (String s : style.getAttributes()) {
            Object styleValue = style.get(s);
            try {
                props.put(s, VALUE_ADAPTER.marshal(styleValue));
            } catch (UnsupportedOperationException x) {
                Logger.getLogger(AttributeSetAdapter.class.getName()).log(Level.WARNING,
                        "Cannot convert value {0} to string.", styleValue);
            }
        }
        return Joiner.on("; ").withKeyValueSeparator(":").join(props);
    }
    
    
    /** 
     * Converts {@link AttributeSet} values to/from strings. If the value cannot
     * be handled, marshalling will generate {@link UnsupportedOperationException}s.
     */
    public static class AttributeValueAdapter extends XmlAdapter<String, Object> {
        @Override
        public Object unmarshal(String sval) {
            if (sval.matches("#[0-9a-fA-f]{6}")
                    || sval.matches("#[0-9a-fA-f]{8}")) {
                return ColorAdapter.INST.unmarshal(sval);
            }
            try {
                return Integer.valueOf(sval);
            } catch (NumberFormatException x) {
                // wasn't an integer, try the next thing
            }
            try {
                return Double.valueOf(sval);
            } catch (NumberFormatException x) {
                // wasn't a double, try the next thing
            }
            return sval;
        }

        @Override
        public String marshal(Object val) {
            if (val instanceof Color) {
                return ColorAdapter.INST.marshal((Color) val);
            } else if (val instanceof Number) {
                return val.toString();
            } else if (val instanceof String) {
                return (String) val;
            } else {
                throw new UnsupportedOperationException("Unsupported value: " + val);
            }
        }
    }
    
}
