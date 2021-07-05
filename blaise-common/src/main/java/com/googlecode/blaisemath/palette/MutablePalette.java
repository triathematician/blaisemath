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

import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.Color;

/**
 * A color palette that can be edited.
 * @author Elisha Peterson
 */
public abstract class MutablePalette extends Palette {
    
    /**
     * Get the name of the palette.
     * @return name
     */
    public abstract String getName();

    /**
     * Remove a key from the palette.
     * @param key to remove
     * @return the color removed, if present, or null
     */
    public abstract @Nullable Color remove(String key);
    
    /**
     * Set or update a color in the palette.
     * @param key color's key
     * @param value color
     */
    public abstract void set(String key, Color value);
    
    /**
     * Sets a color and returns this.
     * @param key color's key
     * @param value color
     * @return this
     */
    public MutablePalette and(String key, Color value) {
        set(key, value);
        return this;
    }
    
}
