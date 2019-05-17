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

import static com.google.common.base.Preconditions.checkArgument;

/**
 * SVG-compatible circle.
 *
 * @author Elisha Peterson
 */
@JacksonXmlRootElement(localName="circle")
public final class SvgCircle extends SvgElement {
    
    private static final CircleConverter CONVERTER_INST = new CircleConverter();
    
    private double cx;
    private double cy;
    private double r;

    public SvgCircle() {
        this(0, 0, 0);
    }

    public SvgCircle(double cx, double cy, double r) {
        super("circle");
        this.cx = cx;
        this.cy = cy;
        this.r = r;
    }

    public static Converter<SvgCircle, Ellipse2D> shapeConverter() {
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
    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }
    
    //endregion

    
    private static final class CircleConverter extends Converter<SvgCircle, Ellipse2D> {
        @Override
        protected SvgCircle doBackward(Ellipse2D r) {
            checkArgument(r.getWidth() == r.getHeight(), "Ellipse must have width=height");
            return new SvgCircle(r.getCenterX(), r.getCenterY(), r.getWidth()/2);
        }

        @Override
        protected Ellipse2D doForward(SvgCircle r) {
            return new Ellipse2D.Double(r.cx-r.r, r.cy-r.r, 2*r.r, 2*r.r);
        }
    }
    
}
