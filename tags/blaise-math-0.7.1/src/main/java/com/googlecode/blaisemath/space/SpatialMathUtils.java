/**
 * SpatialMathUtils.java
 * Created on Jul 29, 2009
 */

package com.googlecode.blaisemath.space;

/*
 * #%L
 * BlaiseMath
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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

/**
 * <p>
 *   Static methods for use on elements of space (vectors).
 *   All methods work with <code>Point3D</code>s.
 * </p>
 *
 * @author Elisha Peterson
 */
public class SpatialMathUtils {

    /** Non-instantiable class. */
    private SpatialMathUtils() {
    }

    /**
     * Computes and returns triple product of 3 3-vectors
     * @param p1
     * @param p2
     * @param p3
     * @return 
     */
    public static double tripleProduct(Point3D p1, Point3D p2, Point3D p3) {
        return p1.crossProduct(p2).dotProduct(p3);
    }

}
