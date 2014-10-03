/**
 * AnchoredImage.java
 * Created on Sep 12, 2014
 */
package com.googlecode.blaisemath.util;

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

import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import javax.annotation.Nullable;

/**
 * An image anchored at a given location.
 * @author petereb1
 * 
 * @todo - how to handle conflicting width/height of image vs width/height here?
 */
public class AnchoredImage {
    
    private final Image image;
    private final String ref;
    private final double x;
    private final double y;
    private final Double width;
    private final Double height;

    public AnchoredImage(double x, double y, Image image, @Nullable String ref) {
        this(x, y, null, null, image, ref);
    }

    public AnchoredImage(double x, double y, Double width, Double height, Image image, @Nullable String ref) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
        this.ref = ref;
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    public String getReference() {
        return ref;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
    
    public Rectangle2D getBounds(ImageObserver io) {
        double iw = width == null ? image.getWidth(io) : width;
        double ih = height == null ? image.getHeight(io) : height;
        return new Rectangle2D.Double(x, y, iw, ih);
    }
    
    public Image getImage() {
        return image;
    }
    
    //</editor-fold>
    
}
