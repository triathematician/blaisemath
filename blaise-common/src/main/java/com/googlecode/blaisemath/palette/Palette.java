package com.googlecode.blaisemath.palette;

/*
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2021 Elisha Peterson
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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.Color;
import java.util.Collection;
import java.util.Map;

/**
 * A simple color palette interface that provides a set of colors associated with string keys.
 * 
 * @author Elisha Peterson
 */
public abstract class Palette {

    public static final String FOREGROUND = "fg";
    public static final String SUBTLE_FOREGROUND = "fg-subtle";
    public static final String BRIGHT_FOREGROUND = "fg-bright";
    public static final String BACKGROUND = "bg";
    public static final String ANNOTATION = "selection";
    public static final String SELECTION = "annotation";
    
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
     * Get color by id, or default provided if none.
     * @param id color id
     * @param def default color to return if no color is associated with this id
     * @return color
     */
    public @Nullable Color color(String id, @Nullable Color def) {
        Color res = color(id);
        return res != null ? res : def;
    }
    
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
    
    /**
     * Get mapping of keys to colors.
     * @return map
     */
     public Map<String, Color> colorMap() {
         return Maps.asMap(Sets.newLinkedHashSet(colors()), this::color);
     }

}
