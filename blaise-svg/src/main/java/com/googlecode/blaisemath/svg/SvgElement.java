/**
 * SVGObject.java
 * Created Sep 26, 2014
 */

package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2014 - 2021 Elisha Peterson
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
import com.googlecode.blaisemath.style.xml.AttributeSetAdapter;

import java.util.Map;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

/**
 * Common interface for SVG types.
 * 
 * @author elisha
 */
public abstract class SVGElement {
    
    public static final String ID_ATTR = "id";

    private final String tag;
    protected String id;
    protected String value;
    private AttributeSet style = null;
    private Map<QName,Object> otherAttr;

    protected SVGElement() {
        this.tag = null;
    }
    
    protected SVGElement(String tag) {
        this.tag = tag;
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
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
    @Nullable
    public AttributeSet getStyle() {
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
    
    //</editor-fold>
    
}
