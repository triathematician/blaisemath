/**
 * GraphicUtils.java
 * Created Jul 11, 2014
 */
package com.googlecode.blaisemath.graphics;

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


import com.google.common.base.Predicate;
import com.googlecode.blaisemath.style.context.StyleHintSet;

/**
 * Utility class for working with {@link Graphic}s.
 * @author Elisha
 */
public class GraphicUtils {
    
    /** Filter that can be applied to pass only visible graphics */
    private static final Predicate<Graphic> VISIBLE_FILTER = new Predicate<Graphic>(){
        @Override
        public boolean apply(Graphic input) { 
            return !StyleHintSet.isInvisible(input.getStyleHints());
        }
    };
    
    /** Filter that can be applied to pass only visible graphics */
    private static final Predicate<Graphic> FUNCTIONAL_FILTER = new Predicate<Graphic>(){
        @Override
        public boolean apply(Graphic input) { 
            return StyleHintSet.isFunctional(input.getStyleHints());
        }
    };
    
    // utility class
    private GraphicUtils() {
    }
    
    /**
     * Return visibility filter for graphics.
     * @return visible filter
     */
    public static Predicate<Graphic> visibleFilter() {
        return VISIBLE_FILTER;
    }
    
    /**
     * Return functional filter for graphics.
     * @return functional filter
     */
    public static Predicate<Graphic> functionalFilter() {
        return FUNCTIONAL_FILTER;
    }

    /**
     * Return true if graphic is currently invisible
     * @param gr the graphic
     * @return true if hidden
     */
    public static boolean isInvisible(Graphic gr) {
        return !visibleFilter().apply(gr);
    }

    /**
     * Return true if graphic is currently functional
     * @param gr the graphic
     * @return true if invisible
     */
    public static boolean isFunctional(Graphic gr) {
        return functionalFilter().apply(gr);
    }
    
}
