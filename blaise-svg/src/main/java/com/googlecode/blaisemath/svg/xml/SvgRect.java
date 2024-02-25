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

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * SVG rectangle object.
 * @author Elisha Peterson
 */
@XmlRootElement(name="rect")
public final class SvgRect extends SvgElement {

    @XmlAttribute
    public float x = 0f;
    @XmlAttribute
    public float y = 0f;
    @XmlAttribute
    public float width = 0f;
    @XmlAttribute
    public float height = 0f;
    @XmlAttribute
    public Float rx = null;
    @XmlAttribute
    public Float ry = null;

    public static SvgRect create(double x, double y, double width, double height) {
        return create((float) x, (float) y, (float) width, (float) height);
    }

    public static SvgRect create(float x, float y, float width, float height) {
        SvgRect res = new SvgRect();
        res.x = x;
        res.y = y;
        res.width = width;
        res.height = height;
        return res;
    }

    public static SvgRect create(double x, double y, double width, double height, double rx, double ry) {
        return create((float) x, (float) y, (float) width, (float) height, (float) rx, (float) ry);
    }

    public static SvgRect create(float x, float y, float width, float height, float rx, float ry) {
        SvgRect res = new SvgRect();
        res.x = x;
        res.y = y;
        res.width = width;
        res.height = height;
        res.rx = rx;
        res.ry = ry;
        return res;
    }
}
