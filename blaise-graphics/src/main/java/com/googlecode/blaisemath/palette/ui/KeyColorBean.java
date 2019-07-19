package com.googlecode.blaisemath.palette.ui;

/*
 * #%L
 * blaise-graphics
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


import com.googlecode.blaisemath.geom.Marker;
import java.awt.Color;
import javax.annotation.Nullable;

/**
 * Bean that tracks a key/name associated with a color (and optionally a marker).
 * 
 * @author Elisha Peterson
 */
public class KeyColorBean {
    
    private @Nullable String name;
    private @Nullable Color color;
    private @Nullable Marker marker;
    
    //region FACTORIES

    /**
     * Create bean with name and color
     * @param name style name
     * @param col color
     * @return style
     */
    public static KeyColorBean create(@Nullable String name, @Nullable Color col) {
        KeyColorBean res = new KeyColorBean();
        res.setName(name);
        res.setColor(col);
        return res;
    }

    /**
     * Get style for a color.
     * @param col color
     * @return style
     */
    public static KeyColorBean create(@Nullable Color col) {
        KeyColorBean res = new KeyColorBean();
        res.setColor(col);
        return res;
    }
    
    /**
     * Get style for a color.
     * @param mark marker
     * @return style
     */
    public static KeyColorBean create(@Nullable Marker mark) {
        KeyColorBean res = new KeyColorBean();
        res.setMarker(mark);
        return res;
    }
    
    /**
     * Creates copy of another scale.
     * @param style what to copy
     * @return copy
     */
    public static KeyColorBean copyOf(KeyColorBean style) {
        KeyColorBean res = new KeyColorBean();
        res.name = style.name;
        res.color = style.color;
        res.marker = style.marker;
        return res;
    }
    
    
//    /**
//     * Convert an attribute set to a simple style, using the keys {@link Styles.FILL}
//     * for color, {@link Styles.MARKER} for marker, and {@link Styles.ID} for name.
//     * @param as attribute set
//     * @return simple style
//     */
//    public static SimpleStyle create(AttributeSet as) {
//        Color c = getOrDefault(as, Styles.FILL, Color.class, Colors::decode, null);
//        Marker m = getOrDefault(as, Styles.MARKER, Marker.class, Markers2::toMarker, null);
//        String n = getOrDefault(as, Styles.ID, String.class, null, null);
//        SimpleStyle res = new SimpleStyle();
//        res.setName(n);
//        res.setColor(c);
//        res.setMarker(m);
//        return res;
//    }
    
    //endregion
    
    //region PROPERTIES

    public @Nullable String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    public @Nullable Color getColor() {
        return color;
    }

    public void setColor(@Nullable Color color) {
        this.color = color;
    }

    public @Nullable Marker getMarker() {
        return marker;
    }

    public void setMarker(@Nullable Marker marker) {
        this.marker = marker;
    }
    
    //endregion
}
