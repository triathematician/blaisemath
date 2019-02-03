/**
 * SVGCircle.java
 * Created Sep 26, 2014
 */

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
import java.awt.geom.Ellipse2D;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 *   SVG-compatible ellipse.
 * </p>
 * @author elisha
 */
@XmlRootElement(name="ellipse")
public final class SVGEllipse extends SVGElement {
    
    private static final EllipseConverter CONVERTER_INST = new EllipseConverter();
    
    private double cx;
    private double cy;
    private double rx;
    private double ry;

    public SVGEllipse() {
        this(0, 0, 0, 0);
    }

    public SVGEllipse(double cx, double cy, double rx, double ry) {
        super("ellipse");
        this.cx = cx;
        this.cy = cy;
        this.rx = rx;
        this.ry = ry;
    }

    public static Converter<SVGEllipse, Ellipse2D> shapeConverter() {
        return CONVERTER_INST;
    }

    //region PROPERTIES
    //
    // PROPERTY PATTERNS
    //

    @XmlAttribute
    public double getCx() {
        return cx;
    }

    public void setCx(double cx) {
        this.cx = cx;
    }

    @XmlAttribute
    public double getCy() {
        return cy;
    }

    public void setCy(double cy) {
        this.cy = cy;
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
    
    //endregion

    
    private static final class EllipseConverter extends Converter<SVGEllipse, Ellipse2D> {
        @Override
        protected SVGEllipse doBackward(Ellipse2D r) {
            return new SVGEllipse(r.getCenterX(), r.getCenterY(), r.getWidth()/2, r.getHeight()/2);
        }

        @Override
        protected Ellipse2D doForward(SVGEllipse r) {
            return new Ellipse2D.Double(r.cx-r.rx, r.cy-r.ry, 2*r.rx, 2*r.ry);
        }
    }
    
}
