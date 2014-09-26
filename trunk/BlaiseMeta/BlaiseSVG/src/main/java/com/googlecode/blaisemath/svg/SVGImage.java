/**
 * SVGImage.java
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

import com.googlecode.blaisemath.util.AnchoredImage;
import java.awt.Image;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 *   SVG-compatible image, with size parameters and a reference URL.
 * </p>
 * @author elisha
 */
@XmlRootElement(name="image")
public final class SVGImage extends SVGObject {
    
    private double x;
    private double y;
    private double width;
    private double height;
    private String imageRef = null;

    public SVGImage() {
        this(0, 0, 0, 0, "");
    }

    public SVGImage(double x, double y, double width, double height, String tag) {
        super("image");
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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
    
    @XmlAttribute(name="href", namespace="http://www.w3.org/1999/xlink")
    public String getImageRef() {
        return imageRef;
    }

    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
    }
    
    //</editor-fold>

    
    public static class Adapter implements SVGAdapter<SVGImage, AnchoredImage> {
        public SVGImage toSVG(AnchoredImage r) {
            return new SVGImage(r.getX(), r.getY(), r.getWidth(), r.getHeight(), r.getReference());
        }

        public AnchoredImage toGraphics(SVGImage r) {
            Image img = null; // todo - construct image
            return new AnchoredImage(r.x, r.y, r.width, r.height, img, r.imageRef);
        }
    }
}
