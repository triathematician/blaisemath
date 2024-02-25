package com.googlecode.blaisemath.svg.xml;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2014 - 2024 Elisha Peterson
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
import java.util.Objects;

/**
 * SVG-compatible circle.
 * @author Elisha Peterson
 */
@XmlRootElement(name="circle")
public final class SvgCircle extends SvgElement {

    @XmlAttribute
    public float cx = 0f;
    @XmlAttribute
    public float cy = 0f;
    @XmlAttribute
    public float r = 0f;

    public static SvgCircle create(double cx, double cy, double r) {
        return create((float) cx, (float) cy, (float) r);
    }

    public static SvgCircle create(float cx, float cy, float r) {
        SvgCircle res = new SvgCircle();
        res.cx = cx;
        res.cy = cy;
        res.r = r;
        return res;
    }
}
