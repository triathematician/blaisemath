package com.googlecode.blaisemath.palette;

/*
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
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

import java.awt.Color;
import java.util.Collection;
import javax.annotation.Nullable;

/**
 * A simple color palette interface that provides a set of colors associated with string keys.
 * 
 * @author petereb1
 */
public abstract class Palette {

    public static final String FOREGROUND = "fg";
    public static final String SUBTLE_FOREGROUND = "fg-subtle";
    public static final String BRIGHT_FOREGROUND = "fg-bright";
    public static final String BACKGROUND = "bg";
    public static final String ANNOTATION_BOX = "selection-box";
    public static final String SELECTION_BOX = "annotation-box";
    
    /**
     * Create a mutable copy of the palette.
     * @return copy
     */
    public MutablePalette mutableCopy() {
        return MapPalette.create(Palettes.colorMap(this));
    }

    /**
     * Get list of color keys available.
     * @return color keys
     */
    public abstract Collection<String> colors();
    
    /**
     * Get color by id.
     * @param id color id
     * @return color
     */
    public abstract @Nullable Color color(String id);
    
    /**
     * Get foreground color.
     * @return color
     */
    public @Nullable Color foreground() {
        return color(FOREGROUND);
    }
    
    /**
     * Get background color.
     * @return color
     */
    public @Nullable Color background() {
        return color(BACKGROUND);
    }

}
