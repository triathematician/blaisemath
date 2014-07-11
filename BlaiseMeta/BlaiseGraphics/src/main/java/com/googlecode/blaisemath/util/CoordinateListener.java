/*
 * CoordinateListener.java
 * Created on Aug 30, 2012
 */

package com.googlecode.blaisemath.util;

/*
 * #%L
 * BlaiseGraphics
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
 * Receives updates regarding the locations of a collection of objects.
 *
 * @author petereb1
 */
public interface CoordinateListener {

    /**
     * Called when coordinates/points are added.
     * @param evt description of what coordinates were added/removed/changed
     */
    void coordinatesChanged(CoordinateChangeEvent evt);

}
