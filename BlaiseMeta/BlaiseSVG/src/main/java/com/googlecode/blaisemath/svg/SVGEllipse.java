/**
 * SVGCircle.java
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

import java.awt.geom.Ellipse2D;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 *   SVG-compatible ellipse.
 * </p>
 * @author elisha
 */
@XmlRootElement(name="ellipse")
public final class SVGEllipse extends SVGObject {
    
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

    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    public double getCx() {
        return cx;
    }

    public void setCx(double cx) {
        this.cx = cx;
    }

    public double getCy() {
        return cy;
    }

    public void setCy(double cy) {
        this.cy = cy;
    }

    public double getRx() {
        return rx;
    }

    public void setRx(double rx) {
        this.rx = rx;
    }

    public double getRy() {
        return ry;
    }

    public void setRy(double ry) {
        this.ry = ry;
    }
    
    //</editor-fold>

    
    public static class Adapter implements SVGAdapter<SVGEllipse, Ellipse2D> {
        public SVGEllipse toSVG(Ellipse2D r) {
            return new SVGEllipse(r.getCenterX(), r.getCenterY(), r.getWidth()/2, r.getHeight()/2);
        }

        public Ellipse2D toGraphics(SVGEllipse r) {
            return new Ellipse2D.Double(r.cx-r.rx, r.cy-r.ry, 2*r.rx, 2*r.ry);
        }
    }
    
}
