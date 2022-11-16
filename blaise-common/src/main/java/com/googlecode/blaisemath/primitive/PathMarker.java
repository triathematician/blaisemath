package com.googlecode.blaisemath.primitive;

/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2022 Elisha Peterson
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

import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/** 
 * Marker defined by a path.
 * @author Elisha Peterson
 */
public class PathMarker implements Marker {


    private final @Nullable String name;
    private final Path2D path;
    private final Rectangle2D bds;

    public PathMarker(@Nullable String name, Path2D path) {
        this.name = name;
        this.path = path;
        this.bds = path.getBounds2D();
    }
    
    @Override
    public String toString() {
        return name;
    }

    public @Nullable String getName() {
        return name;
    }

    public Path2D getPath() {
        return path;
    }

    @Override
    public Shape create(Point2D point, double orientation, float markerRadius) {
        AffineTransform at = new AffineTransform();
        double scale = 2 * markerRadius / Math.max(bds.getWidth(), bds.getHeight());
        at.translate(point.getX(), point.getY());
        at.scale(scale, scale);
        at.rotate(orientation);
        at.translate(-bds.getX() - .5 * bds.getWidth(), -bds.getY() - .5 * bds.getHeight());
        return path.createTransformedShape(at);
    }

}
