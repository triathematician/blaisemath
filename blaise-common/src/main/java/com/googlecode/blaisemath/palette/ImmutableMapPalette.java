package com.googlecode.blaisemath.palette;

/*
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2025 Elisha Peterson
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
import java.util.Collections;
import java.util.Map;
import static java.util.Objects.requireNonNull;

/**
 * A color palette based on an immutable map.
 * @author Elisha Peterson
 */
public class ImmutableMapPalette extends Palette {

    private final Map<String, Color> map;

    public ImmutableMapPalette(Map<String, Color> map) {
        this.map = Collections.unmodifiableMap(requireNonNull(map));
    }

    @Override
    public Collection<String> colors() {
        return map.keySet();
    }

    @Override
    public Color color(String id) {
        return map.get(id);
    }

}
