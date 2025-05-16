package com.googlecode.blaisemath.svg.xml;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2014 - 2025 Elisha Peterson
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
 * SVG-compatible line.
 * @author Elisha Peterson
 */
@XmlRootElement(name="line")
public final class SvgLine extends SvgElement {

    @XmlAttribute
    public float x1 = 0f;
    @XmlAttribute
    public float y1 = 0f;
    @XmlAttribute
    public float x2 = 0f;
    @XmlAttribute
    public float y2 = 0f;

    public static SvgLine create(double x1, double y1, double x2, double y2) {
        return create((float) x1, (float) y1, (float) x2, (float) y2);
    }

    public static SvgLine create(float x1, float y1, float x2, float y2) {
        SvgLine res = new SvgLine();
        res.x1 = x1;
        res.y1 = y1;
        res.x2 = x2;
        res.y2 = y2;
        return res;
    }
    
}
