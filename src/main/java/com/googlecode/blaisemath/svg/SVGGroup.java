/**
 * SVGGroup.java
 * Created Sep 26, 2014
 */

package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2014 - 2015 Elisha Peterson
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
import java.util.List;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * <p>
 *   SVG group object.
 * </p>
 * @author elisha
 */
@XmlRootElement(name="g")
@XmlSeeAlso({
    SVGCircle.class, SVGEllipse.class, SVGImage.class, SVGLine.class, SVGPath.class,
    SVGPolygon.class, SVGPolyline.class, SVGRectangle.class, SVGText.class
})
public class SVGGroup extends SVGElement {
    
    private List<SVGElement> obj = Lists.newArrayList();

    public SVGGroup() {
        super("g");
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
    @XmlElementRef
    public List<SVGElement> getElements() {
        return obj;
    }

    public void setElements(List<SVGElement> obj) {
        this.obj = obj;
    }
    
    public void addElement(SVGElement obj) {
        this.obj.add(obj);
    }
    
    //</editor-fold>

    public SVGElement getObjectById(String id) {
        for (SVGElement ms : obj) {
            if (Objects.equal(ms.getId(), id)) {
                return ms;
            }
        }
        return null;
    }
    
}
