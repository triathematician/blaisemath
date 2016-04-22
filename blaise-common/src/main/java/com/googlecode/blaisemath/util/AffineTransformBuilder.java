/**
 * AffineTransformBuilder.java
 * Created Dec 14, 2014
 */
package com.googlecode.blaisemath.util;

/*
 * #%L
 * BlaiseSketch
 * --
 * Copyright (C) 2014 - 2016 Elisha Peterson
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
 * @author elisha
 */
public final class AffineTransformBuilder {
    
    private final AffineTransform res = new AffineTransform();

    public AffineTransformBuilder translate(double dx, double dy) {
        res.translate(dx, dy);
        return this;
    }
    
    public AffineTransformBuilder scale(double rx, double ry) {
        res.scale(rx, ry);
        return this;
    }
    
    public AffineTransform build() {
        return res;
    }
    
}
