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
 * SVG-compatible image, with size parameters and a reference URL. Makes no validity checks for the image reference.
 * @author Elisha Peterson
 */
@XmlRootElement(name="image")
public final class SvgImage extends SvgElement {

    @XmlAttribute
    public float x = 0f;
    @XmlAttribute
    public float y = 0f;
    @XmlAttribute
    public float width = 0f;
    @XmlAttribute
    public float height = 0f;
    @XmlAttribute(namespace="http://www.w3.org/1999/xlink")
    public String href = "";

    public static SvgImage create(float x, float y, String ref) {
        return create(x, y, 0f, 0f, ref);
    }

    public static SvgImage create(float x, float y, float width, float height, String ref) {
        SvgImage res = new SvgImage();
        res.x = x;
        res.y = y;
        res.width = width;
        res.height = height;
        res.href = ref;
        return res;
    }

}
