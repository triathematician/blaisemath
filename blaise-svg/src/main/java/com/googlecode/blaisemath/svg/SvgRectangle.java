/**
 * SVGRectangle.java
 * Created Sep 26, 2014
 */

package com.googlecode.blaisemath.svg;

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

import com.google.common.base.Converter;
import static com.google.common.base.Preconditions.checkArgument;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 *   SVG rectangle object.
 * </p>
 * @author elisha
 */
@XmlRootElement(name="rect")
public final class SVGRectangle extends SVGElement {
    
    private static final RectangleConverter CONVERTER_INST = new RectangleConverter();
    
    private double x;
    private double y;
    private double width;
    private double height;
    private double rx;
    private double ry;

    public SVGRectangle() {
        this(0, 0, 0, 0, 0, 0);
    }

    public SVGRectangle(double x, double y, double width, double height) {
        this(x, y, width, height, 0, 0);
    }
    
    public SVGRectangle(double x, double y, double width, double height, double rx, double ry) {
        super("rect");
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rx = rx;
        this.ry = ry;
    }

    /**
     * Get converter that translates an {@link SVGRectangle} to/from a {@link RectangularShape}.
     * @return converter instance
     */
    public static Converter<SVGRectangle, RectangularShape> shapeConverter() {
        return CONVERTER_INST;
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
    @XmlAttribute
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }
    
    @XmlAttribute
    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
    
    @XmlAttribute
    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    @XmlAttribute
    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @XmlAttribute
    public double getRx() {
        return rx;
    }

    public void setRx(double rx) {
        this.rx = rx;
    }

    @XmlAttribute
    public double getRy() {
        return ry;
    }

    public void setRy(double ry) {
        this.ry = ry;
    }
    
    //</editor-fold>

    /** Handles conversion of rectangle to/from a rectangular shape */
    private static final class RectangleConverter extends Converter<SVGRectangle, RectangularShape> {
        @Override
        public SVGRectangle doBackward(RectangularShape r) {
            checkArgument(r instanceof RoundRectangle2D || r instanceof Rectangle2D,
                    "Invalid shape: "+r);
            if (r instanceof RoundRectangle2D) {
                RoundRectangle2D rr = (RoundRectangle2D) r;
                return new SVGRectangle(rr.getX(), rr.getY(), rr.getWidth(), rr.getHeight(), rr.getArcWidth(), rr.getArcHeight());
            } else if (r instanceof Rectangle2D) {
                return new SVGRectangle(r.getX(), r.getY(), r.getWidth(), r.getHeight());
            } else {
                throw new IllegalStateException();
            }
        }

        @Override
        public RectangularShape doForward(SVGRectangle r) {
            if (r.rx == 0 && r.ry == 0) {
                return new Rectangle2D.Double(r.x, r.y, r.width, r.height);
            } else {
                return new RoundRectangle2D.Double(r.x, r.y, r.width, r.height, r.rx, r.ry);
            }
        }
    }
    
}
