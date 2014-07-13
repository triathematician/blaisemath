/*
 * TextStyle.java
 * Created Jan 22, 2011
 */

package com.googlecode.blaisemath.style;

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

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.annotation.Nullable;

/**
 * Draws a string on a graphics canvas at a given location. Also provides a method
 * to compute the boundaries of that string.
 * 
 * @author Elisha
 */
public interface TextStyle extends Style {

    /** 
     * Compute and return the bounding box for a string drawn on the canvas.
     * @param anchor anchor point for string
     * @param text text of string
     * @param canvas graphics element to draw on
     * @return boundaries of the string for the current settings, or null indicating there are none
     */
    @Nullable
    Rectangle2D bounds(Point2D anchor, @Nullable String text, Graphics2D canvas);

    /**
     * Draws specified string on the graphics canvas with visibility options.
     * @param anchor anchor point for string
     * @param text text of string
     * @param canvas graphics element to draw on
     */
    void draw(Point2D anchor, @Nullable String text, Graphics2D canvas);
    
}
