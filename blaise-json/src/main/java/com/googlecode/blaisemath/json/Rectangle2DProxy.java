package com.googlecode.blaisemath.json;

/*-
 * #%L
 * blaise-json
 * --
 * Copyright (C) 2019 - 2021 Elisha Peterson
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

import java.awt.geom.Rectangle2D;

/**
 * Serializable proxy for a {@link Rectangle2D}.
 * @author Elisha Peterson
 */
public class Rectangle2DProxy {
    
    private double x;
    private double y;
    private double width;
    private double height;

    public Rectangle2DProxy() {
    }
    
    public Rectangle2DProxy(Rectangle2D rect) {
        this.x = rect.getX();
        this.y = rect.getY();
        this.width = rect.getWidth();
        this.height = rect.getHeight();
    }

    public Rectangle2D.Double toRectangle() {
        return new Rectangle2D.Double(x, y, width, height);
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    //</editor-fold>

}
