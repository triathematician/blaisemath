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

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.Converter;

import java.awt.geom.Ellipse2D;

/**
 * SVG-compatible ellipse.
 *
 * @author Elisha Peterson
 */
@JacksonXmlRootElement(localName="ellipse")
public final class SvgEllipse extends SvgElement {
    
    private static final EllipseConverter CONVERTER_INST = new EllipseConverter();
    
    private double cx;
    private double cy;
    private double rx;
    private double ry;

    public SvgEllipse() {
        this(0, 0, 0, 0);
    }

    public SvgEllipse(double cx, double cy, double rx, double ry) {
        super("ellipse");
        this.cx = cx;
        this.cy = cy;
        this.rx = rx;
        this.ry = ry;
    }

    public static Converter<SvgEllipse, Ellipse2D> shapeConverter() {
        return CONVERTER_INST;
    }

    //region PROPERTIES

    @JacksonXmlProperty(isAttribute = true)
    public double getCx() {
        return cx;
    }

    public void setCx(double cx) {
        this.cx = cx;
    }

    @JacksonXmlProperty(isAttribute = true)
    public double getCy() {
        return cy;
    }

    public void setCy(double cy) {
        this.cy = cy;
    }

    @JacksonXmlProperty(isAttribute = true)
    public double getRx() {
        return rx;
    }

    public void setRx(double rx) {
        this.rx = rx;
    }

    @JacksonXmlProperty(isAttribute = true)
    public double getRy() {
        return ry;
    }

    public void setRy(double ry) {
        this.ry = ry;
    }
    
    //endregion

    
    private static final class EllipseConverter extends Converter<SvgEllipse, Ellipse2D> {
        @Override
        protected SvgEllipse doBackward(Ellipse2D r) {
            return new SvgEllipse(r.getCenterX(), r.getCenterY(), r.getWidth()/2, r.getHeight()/2);
        }

        @Override
        protected Ellipse2D doForward(SvgEllipse r) {
            return new Ellipse2D.Double(r.cx-r.rx, r.cy-r.ry, 2*r.rx, 2*r.ry);
        }
    }
    
}
