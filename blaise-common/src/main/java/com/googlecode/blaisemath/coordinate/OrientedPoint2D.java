package com.googlecode.blaisemath.coordinate;

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

import com.googlecode.blaisemath.coordinate.Point2DBean;
import java.awt.geom.Point2D;
import java.util.Objects;

/**
 * A point with an orientation.
 * @author Elisha
 */
public class OrientedPoint2D extends Point2DBean {
    
    /** The orientation of the point */
    public double angle = 0;

    public OrientedPoint2D() {
    }
    
    public OrientedPoint2D(Point2D pt) {
        super(pt.getX(), pt.getY());
        if (pt instanceof OrientedPoint2D) {
            angle = ((OrientedPoint2D)pt).angle;
        }
    }

    public OrientedPoint2D(double x, double y) {
        super(x, y);
    }

    @Override
    public String toString() {
        return "OrientedPoint2D["+x+", "+y+"; "+angle+"]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, angle);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OrientedPoint2D)) {
            return false;
        }
        OrientedPoint2D opt = (OrientedPoint2D) obj;
        return opt.x == x && opt.y == y && opt.angle == angle;
    }
    
    /**
     * Update angle to be in direction of given second point
     * @param p2 second point
     * @return this
     */
    public OrientedPoint2D inDirectionOf(Point2D p2) {
        setAngle(Math.atan2(p2.getY() - getY(), p2.getX() - getX()));
        return this;
    }
    
    /**
     * Update angle to be in direction of given second point
     * @param p2 second point
     * @return this
     */
    public OrientedPoint2D inOppositeDirectionOf(Point2D p2) {
        setAngle(Math.atan2(-p2.getY() + getY(), -p2.getX() + getX()));
        return this;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
    
}
