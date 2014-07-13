/*
 * PointFormatters.java
 * Created Jan 30, 2011
 */

package com.googlecode.blaisemath.coordinate;

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

import java.awt.geom.Point2D;

/**
 * Utilities for formatting Euclidean points as strings.
 * 
 * @author Elisha Peterson
 */
public final class PointFormatters { 
    
    // utility class - no instantiation
    private PointFormatters() {
    }

    /**
     * Formats a point with n decimal places
     * @param p the point to format
     * @param n number of decimal places
     * @return formatted point, e.g. (2.1,-3.0)
     */
    public static String formatPoint(Point2D p, int n) {
        return String.format("(%."+n+"f, %."+n+"f)", p.getX(), p.getY());
    }

}
