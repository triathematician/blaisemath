package com.googlecode.blaisemath.util;

/**
 * AnchoredString.java
 * Created Jul 31, 2014
 */


/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2014 - 2018 Elisha Peterson
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


import com.google.common.base.Objects;
import java.awt.geom.Point2D;

/**
 * A text string anchored at a particular point.
 * @author Elisha
 */
public final class AnchoredText extends Point2DBean {
    
    private String text;

    public AnchoredText() {
    }

    public AnchoredText(String text) {
        this(0, 0, text);
    }
    
    public AnchoredText(Point2D pt, String text) {
        setText(text);
        setLocation(pt);
    }
    
    public AnchoredText(double x, double y, String text) {
        setText(text);
        setLocation(x, y);
    }

    @Override
    public String toString() {
        return "AnchoredText["+x+", "+y+"; "+text+"]";
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    //</editor-fold>
    
}
