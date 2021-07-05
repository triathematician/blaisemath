package com.googlecode.blaisemath.svg.xml;

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

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * SVG group object.
 * @author Elisha Peterson
 */
@XmlRootElement(name = "g")
@XmlSeeAlso({
        SvgCircle.class, SvgEllipse.class, SvgImage.class, SvgLine.class, SvgPath.class,
        SvgPolygon.class, SvgPolyline.class, SvgRect.class, SvgText.class
})
public class SvgGroup extends SvgElement {

    @XmlElementRef
    public List<SvgElement> elements = Lists.newArrayList();
   
    /** 
     * Create group with initial list of elements
     * @param elements elements to add
     * @return group
     */
    public static SvgGroup create(SvgElement... elements) {
        SvgGroup res = new SvgGroup();
        res.elements.addAll(Arrays.asList(elements));
        return res;
    }

    public SvgElement getObjectById(String id) {
        for (SvgElement ms : elements) {
            if (Objects.equal(ms.id, id)) {
                return ms;
            }
        }
        return null;
    }
    
}
