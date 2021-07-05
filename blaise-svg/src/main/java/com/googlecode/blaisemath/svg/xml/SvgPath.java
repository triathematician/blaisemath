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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * SVG path object.
 * @author Elisha Peterson
 */
@XmlRootElement(name="path")
public final class SvgPath extends SvgElement {

    @XmlAttribute(name="d")
    public String pathStr = "";
    
    public static SvgPath create(String pathStr) {
        SvgPath res = new SvgPath();
        res.pathStr = pathStr;
        return res;
    }

}
