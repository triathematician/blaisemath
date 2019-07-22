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

import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * SVG rectangle object.
 *
 * @author Elisha Peterson
 */
@JacksonXmlRootElement(localName="rect")
public final class SvgRectangle extends SvgElement {
    
    private static final RectangleConverter CONVERTER_INST = new RectangleConverter();
    
    private double x;
    private double y;
    private double width;
    private double height;
    private double rx;
    private double ry;

    public SvgRectangle() {
        this(0, 0, 0, 0, 0, 0);
    }

    public SvgRectangle(double x, double y, double width, double height) {
        this(x, y, width, height, 0, 0);
    }
    
    public SvgRectangle(double x, double y, double width, double height, double rx, double ry) {
        super("rect");
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rx = rx;
        this.ry = ry;
    }

    /**
     * Get converter that translates an {@link SvgRectangle} to/from a {@link RectangularShape}.
     * @return converter instance
     */
    public static Converter<SvgRectangle, RectangularShape> shapeConverter() {
        return CONVERTER_INST;
    }

    //region PROPERTIES

    @JacksonXmlProperty(isAttribute = true)
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }
    
    @JacksonXmlProperty(isAttribute = true)
    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @JacksonXmlProperty(isAttribute = true)
    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    @JacksonXmlProperty(isAttribute = true)
    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
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

    /** Handles conversion of rectangle to/from a rectangular shape */
    private static final class RectangleConverter extends Converter<SvgRectangle, RectangularShape> {
        @Override
        public SvgRectangle doBackward(RectangularShape r) {
            checkArgument(r instanceof RoundRectangle2D || r instanceof Rectangle2D,
                    "Invalid shape: "+r);
            if (r instanceof RoundRectangle2D) {
                RoundRectangle2D rr = (RoundRectangle2D) r;
                return new SvgRectangle(rr.getX(), rr.getY(), rr.getWidth(), rr.getHeight(), rr.getArcWidth(), rr.getArcHeight());
            } else if (r instanceof Rectangle2D) {
                return new SvgRectangle(r.getX(), r.getY(), r.getWidth(), r.getHeight());
            } else {
                throw new IllegalStateException();
            }
        }

        @Override
        public RectangularShape doForward(SvgRectangle r) {
            if (r.rx == 0 && r.ry == 0) {
                return new Rectangle2D.Double(r.x, r.y, r.width, r.height);
            } else {
                return new RoundRectangle2D.Double(r.x, r.y, r.width, r.height, r.rx, r.ry);
            }
        }
    }
    
}