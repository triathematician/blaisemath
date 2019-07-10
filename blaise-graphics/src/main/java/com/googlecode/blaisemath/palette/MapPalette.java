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

import com.google.common.collect.Maps;
import java.awt.Color;
import java.util.Collection;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import javax.annotation.Nullable;

/**
 * A mutable palette backed by a key-value map.
 * @author petereb1
 */
public class MapPalette extends MutablePalette {
    
    private @Nullable String name = null;
    private Map<String, Color> colors = Maps.newLinkedHashMap();
    
    public static MapPalette create(Map<String, Color> colors) {
        MapPalette res = new MapPalette();
        res.setColors(colors);
        return res;
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">

    @Override
    public @Nullable String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    public Map<String, Color> getColors() {
        return colors;
    }

    public void setColors(Map<String, Color> colors) {
        this.colors = Maps.newLinkedHashMap(requireNonNull(colors));
    }
    
    //</editor-fold>

    @Override
    public Color remove(String key) {
        return colors.remove(key);
    }

    @Override
    public void set(String key, Color value) {
        colors.put(key, value);
    }

    @Override
    public Collection<String> colors() {
        return colors.keySet();
    }

    @Override
    public Color color(String id) {
        return colors.get(id);
    }
    
}
