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

import com.google.common.base.Converter;
import com.googlecode.blaisemath.graphics.svg.SvgPathCoder;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.logging.Logger;

/**
 * SVG path object.
 *
 * @author Elisha Peterson
 */
@XmlRootElement(name="path")
public final class SvgPath extends SvgElement {

    private static final Logger LOG = Logger.getLogger(SvgPath.class.getName());

    private static final PathConverter CONVERTER_INST = new PathConverter();
    
    private String pathStr;
    
    public SvgPath() {
        super("path");
    }
    
    public SvgPath(String pathStr) {
        super("path");
        this.pathStr = checkSvgPathStr(pathStr);
    }

    public SvgPath(Shape shape) {
        super("path");
        this.pathStr = new SvgPathCoder().encodeShapePath(shape);
    }

    //region PROPERTIES

    @XmlAttribute(name="d")
    public String getPathStr() {
        return pathStr;
    }

    public void setPathStr(String pathStr) {
        this.pathStr = checkSvgPathStr(pathStr);
    }
    
    //endregion

    /** Checks that the given string is a valid SVG path string, by first decoding it. */
    static String checkSvgPathStr(String svg) {
        new SvgPathCoder().decode(svg);
        return svg;
    }
    
    public static Converter<SvgPath, Path2D> shapeConverter() {
        return CONVERTER_INST;
    }
    
    private static final class PathConverter extends Converter<SvgPath, Path2D> {
        @Override
        protected Path2D doForward(SvgPath path) {
            return new SvgPathCoder().decode(path.pathStr);
        }

        @Override
        protected SvgPath doBackward(Path2D b) {
            return new SvgPath(b);
        }
    }
    
}
