package com.googlecode.blaisemath.util.geom;

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

/**
 * Builder object for {@link AffineTransform}.
 * @author Elisha Peterson
 */
public final class AffineTransformBuilder {
    
    private final AffineTransform res = new AffineTransform();

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

    /**
     * Return the resulting transform.
     * @return transform
     */
    public AffineTransform build() {
        return res;
    }
    
}
