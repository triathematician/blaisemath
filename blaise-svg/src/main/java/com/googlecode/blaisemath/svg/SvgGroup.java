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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.googlecode.blaisemath.svg.io.SvgElementDeserializer;
import com.googlecode.blaisemath.svg.io.SvgElementSerializer;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * SVG group object.
 * 
 * @author Elisha Peterson
 */
@JacksonXmlRootElement(localName="g")
public class SvgGroup extends SvgElement {
    
    private List<SvgElement> obj = Lists.newArrayList();

    public SvgGroup() {
        super("g");
    }
   
    /** 
     * Create group with initial list of elements
     * @param elements elements to add
     * @return group
     */
    public static SvgGroup create(SvgElement... elements) {
        SvgGroup res = new SvgGroup();
        for (SvgElement el : elements) {
            res.addElement(el);
        }
        return res;
    }

    //region PROPERTIES

    @JsonSerialize(contentUsing = SvgElementSerializer.class)
    public List<SvgElement> getElements() {
        return obj;
    }

    @JsonDeserialize(contentUsing = SvgElementDeserializer.class)
    public void setElements(List<SvgElement> obj) {
        this.obj = obj;
    }
    
    public void addElement(SvgElement obj) {
        this.obj.add(requireNonNull(obj));
    }
    
    //endregion

    public SvgElement getObjectById(String id) {
        for (SvgElement ms : obj) {
            if (Objects.equal(ms.getId(), id)) {
                return ms;
            }
        }
        return null;
    }
    
}
