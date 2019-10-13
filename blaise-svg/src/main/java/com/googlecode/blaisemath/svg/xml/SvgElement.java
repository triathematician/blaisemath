package com.googlecode.blaisemath.svg.xml;

/*
 * #%L
 * BlaiseGraphics
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

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.AttributeSetCoder;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;
import java.util.Map;

/**
 * Common interface for SVG types.
 * 
 * @author Elisha Peterson
 */
public abstract class SvgElement {
    
    public static final String ID_ATTR = "id";

    @XmlAttribute
    public @Nullable String id = null;
    @XmlValue
    public @Nullable String value = null;
    @XmlAttribute
    public @Nullable String style = null;
    @XmlAnyAttribute
    public Map<QName, Object> otherAttr = Maps.newLinkedHashMap();

    /**
     * Add key-value style information to the element's style field.
     * @param key style key
     * @param value style value
     */
    public void addStyle(String key, Object value) {
        if (Strings.isNullOrEmpty(style)) {
            style = "";
        } else {
            style += "; ";
        }
        style += key + ": " + value;
    }

    /**
     * Add key-value style information to the element's style field.
     * @param styles map with key-value style information
     */
    public void addStyle(Map<String, ?> styles) {
        if (Strings.isNullOrEmpty(style)) {
            style = "";
        } else {
            style += "; ";
        }
        style +=  new AttributeSetCoder().encode(AttributeSet.create(styles));
    }
}
