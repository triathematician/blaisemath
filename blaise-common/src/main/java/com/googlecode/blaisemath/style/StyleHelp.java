package com.googlecode.blaisemath.style;

/*-
 * #%L
 * blaise-common
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

import com.google.common.annotations.Beta;
import com.google.common.collect.Maps;
import java.awt.Color;
import java.util.Map;
import java.util.Properties;

/**
 * Utilities for accessing values within styles.
 * @author Elisha Peterson
 */
@Beta
public final class StyleHelp {

    /**
     * Get the first non-null color within a style, or default if none found.
     * @param style style to search
     * @param def default color
     * @param keys keys to lookup
     * @return first non-null color, or default
     */
    public static Color firstColor(AttributeSet style, Color def, String... keys) {
        for (String k : keys) {
            Color c = style.getColor(k);
            if (c != null) {
                return c;
            }
        }
        return def;
    }

    /**
     * Get the first non-null color within a style, or default if none found.
     * @param style style to search
     * @param def default color
     * @param keys keys to lookup
     * @return first non-null color, or default
     */
    public static Float firstFloat(AttributeSet style, Float def, String... keys) {
        for (String k : keys) {
            Float c = style.getFloat(k);
            if (c != null) {
                return c;
            }
        }
        return def;
    }

    /** 
     * Gets subset of style starting with the given prefix, assuming a "dot notation".
     * If the prefix is "a.b" for instance, the result will include any parameters "a.b.x",
     * any parameters "a.x", and any parameters "x" as just "x".
     * 
     * @param style base style object, with general parameters
     * @param prefix style prefix to lookup
     * @param defStyle default style parameters
     * @return constructed style
     */
    public static AttributeSet cascadingStyle(AttributeSet style, String prefix, AttributeSet defStyle) {
        AttributeSet res = new AttributeSet();
        addStylesIfAbsent(res, style, prefix);
        for (String k : defStyle.getAllAttributes()) {
            if (!res.contains(k)) {
                res.put(k, defStyle.get(k));
            }
        }
        return res;
    }
    
    /**
     * Read a collection of styles defined within a properties file. Searches among provided prefixes only.
     * @param props properties to search
     * @param prefixes prefixes to search with
     * @return indexed collection of styles
     */
    public static Map<String, AttributeSet> readStyles(Properties props, String... prefixes) {
        Map<String, AttributeSet> res = Maps.newLinkedHashMap();
        for (String key : props.stringPropertyNames()) {
            for (String p : prefixes) {
                if (!key.startsWith(p)) {
                    continue;
                }
                AttributeSet sty = readStyle(props.getProperty(key));
                if (sty != null) {
                    res.put(key, sty);
                    break;
                }
            }
        }
        return res;
    }

    static AttributeSet readStyle(String property) {
        return new AttributeSetCoder().decode(property);
    }
    
    private static void addStylesIfAbsent(AttributeSet result, AttributeSet style, String prefix) {
        // add prefix content
        for (String k : style.getAllAttributes()) {
            if (k.startsWith(prefix+'.')) {
                String suffix = k.substring(prefix.length() + 1);
                if (suffix.length() > 0 && !suffix.contains(".") && !result.contains(suffix)) {
                    result.put(suffix, style.get(k));
                }
            }
        }
        
        // add parent content
        if (prefix.contains(".")) {
            String parentPrefix = prefix.substring(0, prefix.lastIndexOf('.'));
            addStylesIfAbsent(result, style, parentPrefix);
        }
    }

}
