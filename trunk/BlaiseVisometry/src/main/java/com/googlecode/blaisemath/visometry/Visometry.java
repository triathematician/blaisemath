/*
 * Visometry.java
 * Created on Sep 14, 2007, 7:42:38 AM
 */
package com.googlecode.blaisemath.visometry;

/*
 * #%L
 * BlaiseVisometry
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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

import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;

/**
 * <p>
 *   Provides the essential methods for translating
 *   between a visual window on the computer screen and a particular coordinate system.
 * </p>
 * 
 * @param <C> the underlying coordinate system in use
 *
 * @author Elisha Peterson
 */
public interface Visometry<C> {

    /**
     * Returns bounds of the screen viewable part of the visometry.
     * @return bounds for the visometry
     */
    RectangularShape getWindowBounds();

    /**
     * Sets bounds of the visometry.
     * @param newBounds new bounds for the screen
     */
    void setWindowBounds(RectangularShape newBounds);

    /**
     * Returns the minimum visible coordinate.
     */
    C getMinPointVisible();

    /**
     * Returns the maximum visible coordinate.
     */
    C getMaxPointVisible();


    /**
     * Computes a transformation for passing between the window and the local
     * geometry. Should be called whenever the window resizes or the parameters
     * of the visometry change.
     */
    void computeTransformation();

    /**
     * Converts a local coordinate to a window coordinate.
     * @param coordinate the local coordinate
     * @return a point within the window
     */
    Point2D.Double toWindow(C coordinate);

    /**
     * Converts a window point to a local coordinate.
     * @param point the window point
     * @return an underlying coordinate
     */
    C toLocal(Point2D point);

    //
    // EVENT HANDLING
    //
    
    /** 
     * Add a change listener.
     * @param l a change listener
     */
    void addChangeListener(javax.swing.event.ChangeListener l);

    /** 
     * Remove a change listener.
     * @param l a change listener
     */
    void removeChangeListener(javax.swing.event.ChangeListener l);
}
