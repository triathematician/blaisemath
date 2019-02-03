package com.googlecode.blaisemath.util.geom;

/*
 * #%L
 * BlaiseCommon
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

import java.awt.geom.Rectangle2D;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Utility class for working with rectangles.
 * @author Elisha Peterson
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
    public static @Nullable Rectangle2D boundingBox(Iterable<? extends Rectangle2D> rects) {
        Rectangle2D res = null;
        for (Rectangle2D r : rects) {
            res = res == null ? r : res.createUnion(r);
        }
        return res;
    }
    
}