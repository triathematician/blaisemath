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

import static com.google.common.base.Preconditions.checkArgument;
import java.awt.geom.Ellipse2D;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 *   SVG-compatible circle.
 * </p>
 * @author elisha
 */
@XmlRootElement(name="circle")
public final class SVGCircle extends SVGObject {
    
    private double cx;
    private double cy;
    private double r;

    public SVGCircle() {
        this(0, 0, 0);
    }

    public SVGCircle(double cx, double cy, double r) {
        super("circle");
        this.cx = cx;
        this.cy = cy;
        this.r = r;
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

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }
    
    //</editor-fold>

    
    public static class Adapter implements SVGAdapter<SVGCircle, Ellipse2D> {
        public SVGCircle toSVG(Ellipse2D r) {
            checkArgument(r.getWidth() == r.getHeight(), "Ellipse must have width=height");
            return new SVGCircle(r.getCenterX(), r.getCenterY(), r.getWidth()/2);
        }

        public Ellipse2D toGraphics(SVGCircle r) {
            return new Ellipse2D.Double(r.cx-r.r, r.cy-r.r, 2*r.r, 2*r.r);
        }
    }
    
}
