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
 */
public class AnchoredImage {
    
    private final Image image;
    private final String ref;
    private final double x;
    private final double y;

    public AnchoredImage(Image image, @Nullable String ref, double x, double y) {
        this.image = image;
        this.ref = ref;
        this.x = x;
        this.y = y;
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //
    
    public Image getImage() {
        return image;
    }

    public String getReference() {
        return ref;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    
    public Rectangle2D getBounds(ImageObserver io) {
        return new Rectangle2D.Double(x, y, image.getWidth(io), image.getHeight(io));
    }
    
    //</editor-fold>
    
}
