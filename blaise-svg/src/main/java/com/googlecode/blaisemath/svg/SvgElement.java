package com.googlecode.blaisemath.svg;

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

import com.google.common.collect.Maps;
import com.googlecode.blaisemath.json.AttributeSetDeserializer;
import com.googlecode.blaisemath.json.AttributeSetSerializer;
import com.googlecode.blaisemath.style.AttributeSet;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import java.util.Map;

/**
 * Common interface for SVG types.
 * 
 * @author Elisha Peterson
 */
public abstract class SvgElement {
    
    public static final String ID_ATTR = "id";

    private final String tag;
    protected String id;
    protected String value;
    private AttributeSet style = null;
    private Map<QName, Object> otherAttr;

    protected SvgElement() {
        this.tag = null;
    }
    
    protected SvgElement(String tag) {
        this.tag = tag;
    }
    
    //region PROPERTIES
    
    /**
     * Get the tag associated with the object.
     * @return tag
     */
    @XmlTransient
    public String getTag() {
        return tag;
    }
    
    /**
     * Get the id associated with the object.
     * @return id
     */
    @XmlAttribute
    public String getId() {
        return id;
    }
    
    /**
     * Set the id associated with the object.
     * @param id new id
     */
    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute
    @XmlJavaTypeAdapter(AttributeSetAdapter.class)
    public @Nullable AttributeSet getStyle() {
        return style;
    }

    public void setStyle(@Nullable AttributeSet style) {
        this.style = style;
    }

    @XmlAnyAttribute
    public Map<QName,Object> getOtherAttributes() {
        return otherAttr;
    }

    public void setOtherAttributes(Map<QName,Object> other) {
        this.otherAttr = other;
    }

    @XmlValue
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    //endregion
    
}
