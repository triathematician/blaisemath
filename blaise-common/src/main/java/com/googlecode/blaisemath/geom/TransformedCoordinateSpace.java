package com.googlecode.blaisemath.geom;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2014 - 2025 Elisha Peterson
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

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Provides methods for translating between graphic coordinate space and
 * window coordinate space.
 * 
 * @author Elisha Peterson
 */
public interface TransformedCoordinateSpace {

    /**
     * Convert window point location to graphic location.
     * @param winLoc window location
     * @return graphic coordinate system location
     */
    Point2D toGraphicCoordinate(Point2D winLoc);

    /**
     * Get the transform for the coordinate space
     * @return transform
     */
    @Nullable AffineTransform getTransform();

    /**
     * Get the inverse transform for the coordinate space
     * @return inverse transform
     */
    @Nullable AffineTransform getInverseTransform();

    /**
     * Set the transform used for drawing objects on the canvas.
     * @param at the transform
     * @throws IllegalArgumentException if the transform is non-null but not invertible
     */
    void setTransform(@Nullable AffineTransform at);
    
}
