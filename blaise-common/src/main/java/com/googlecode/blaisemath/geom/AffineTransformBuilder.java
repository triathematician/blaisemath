package com.googlecode.blaisemath.geom;

/*
 * #%L
 * BlaiseSketch
 * --
 * Copyright (C) 2014 - 2019 Elisha Peterson
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
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Builder object for {@link AffineTransform}.
 * @author Elisha Peterson
 */
public final class AffineTransformBuilder {

    private static final Logger LOG = Logger.getLogger(AffineTransformBuilder.class.getName());
    
    private final AffineTransform res = new AffineTransform();
    
    //region FACTORIES
    
    /**
     * Create a transform that maps the "scaleFrom" rectangle into the "scaleTo" region.
     * @param scaleTo region to scale to
     * @param scaleFrom region to scale from
     * @return transform
     */
    public static AffineTransform transformingTo(Rectangle2D scaleTo, final Rectangle2D scaleFrom) {
        if (scaleTo.getWidth() == 0 || scaleTo.getHeight() == 0 || scaleFrom.getWidth() == 0 || scaleFrom.getHeight() == 0) {
            LOG.log(Level.FINE, "Scaling with zero area rectangles: {0}, {1}. Returning identity transform.", new Object[]{scaleFrom, scaleTo});
            return new AffineTransform();
        }
        double scaleX = scaleFrom.getWidth() / scaleTo.getWidth();
        double scaleY = scaleFrom.getHeight() / scaleTo.getHeight();
        double scale = Math.max(scaleX, scaleY);
        AffineTransform res = new AffineTransform();
        res.translate(scaleTo.getCenterX(), scaleTo.getCenterY());
        res.scale(1 / scale, 1 / scale);
        res.translate(-scaleFrom.getCenterX(), -scaleFrom.getCenterY());
        return res;
    }

    //endregion
    
    //region BUILDER PATTERNS
    
    /**
     * Concatenates this transform with a translation transformation.
     * @param dx x translation
     * @param dy y translation
     * @return builder
     */
    public AffineTransformBuilder translate(double dx, double dy) {
        res.translate(dx, dy);
        return this;
    }

    /**
     * Concatenates this transform with a scale transformation.
     * @param rx x scale
     * @param ry y scale
     * @return builder
     */
    public AffineTransformBuilder scale(double rx, double ry) {
        res.scale(rx, ry);
        return this;
    }

    /**
     * Concatenates this transform with a rotation transformation.
     * @param theta rotation amount
     * @return builder
     */
    public AffineTransformBuilder rotate(double theta) {
        res.rotate(theta);
        return this;
    }

    /**
     * Concatenates this transform with a rotation transformation about a given point.
     * @param theta rotation amount
     * @param anchorx anchor location x
     * @param anchory anchor location y
     * @return builder
     */
    public AffineTransformBuilder rotate(double theta, double anchorx, double anchory) {
        res.rotate(theta, anchorx, anchory);
        return this;
    }
    
    //endregion

    /**
     * Return the resulting transform.
     * @return transform
     */
    public AffineTransform build() {
        return res;
    }
    
}
