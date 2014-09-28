/**
 * SVGLine.java
 * Created Sep 26, 2014
 */

package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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
import java.awt.geom.Line2D;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 *   SVG-compatible line.
 * </p>
 * @author elisha
 */
@XmlRootElement(name="line")
public final class SVGLine extends SVGElement {
    
    private static final LineConverter CONVERTER_INST = new LineConverter();
     
    private double x1;
    private double y1;
    private double x2;
    private double y2;

    public SVGLine() {
        this(0, 0, 0, 0);
    }

    public SVGLine(double x1, double y1, double x2, double y2) {
        super("line");
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    @XmlAttribute
    public double getX1() {
        return x1;
    }

    public void setX1(double x1) {
        this.x1 = x1;
    }

    @XmlAttribute
    public double getY1() {
        return y1;
    }

    public void setY1(double y1) {
        this.y1 = y1;
    }

    @XmlAttribute
    public double getX2() {
        return x2;
    }

    public void setX2(double x2) {
        this.x2 = x2;
    }

    @XmlAttribute
    public double getY2() {
        return y2;
    }

    public void setY2(double y2) {
        this.y2 = y2;
    }
    
    //</editor-fold>

    public static Converter<SVGLine, Line2D> shapeConverter() {
        return CONVERTER_INST;
    }
    
    private static final class LineConverter extends Converter<SVGLine, Line2D> {
        protected SVGLine doBackward(Line2D r) {
            return new SVGLine(r.getX1(), r.getY1(), r.getX2(), r.getY2());
        }

        protected Line2D doForward(SVGLine r) {
            return new Line2D.Double(r.x1, r.y1, r.x2, r.y2);
        }
    }
    
}
