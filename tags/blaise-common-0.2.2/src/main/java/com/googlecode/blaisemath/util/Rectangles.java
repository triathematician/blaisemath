/**
 * Rectangles.java
 * Created on Mar 4, 2015
 */
package com.googlecode.blaisemath.util;

/*
 * #%L
 * BlaiseCommon
 * --
 * Copyright (C) 2014 - 2015 Elisha Peterson
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

import com.google.common.collect.Iterables;
import java.awt.geom.Rectangle2D;
import javax.annotation.Nullable;

/**
 * Utility class for working with rectangles.
 * @author petereb1
 */
public class Rectangles {
    
    // utility class
    private Rectangles() {
    }

    /**
     * Return rectangle that is bounding box of all provided.
     * @param rects rectangles
     * @return smallest box enclosing provided rectangles, null if argument is empty
     */
    @Nullable
    public static Rectangle2D boundingBox(Iterable<? extends Rectangle2D> rects) {
        if (Iterables.isEmpty(rects)) {
            return null;
        }
        Rectangle2D res = null;
        for (Rectangle2D r : rects) {
            if (res == null) {
                res = r;
            } else {
                res = res.createUnion(r);
            }
        }
        return res;
    }
    
}
