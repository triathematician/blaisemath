/**
 * SVGGroup.java
 * Created Sep 26, 2014
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

import com.google.common.collect.Lists;
import java.util.List;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 *   SVG group object.
 * </p>
 * @author elisha
 */
@XmlRootElement(name="g")
public final class SVGGroup extends SVGObject {
    
    private List<SVGObject> obj = Lists.newArrayList();

    public SVGGroup(String tag) {
        super(tag);
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
    @XmlElementRef
    public List<SVGObject> getObj() {
        return obj;
    }

    public void setObj(List<SVGObject> obj) {
        this.obj = obj;
    }
    
    //</editor-fold>
    
}
