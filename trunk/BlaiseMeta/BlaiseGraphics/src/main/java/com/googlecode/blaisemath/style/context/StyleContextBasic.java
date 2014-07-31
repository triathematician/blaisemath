/**
 * StyleContextDefault.java
 * Created Dec 8, 2012
 */

package com.googlecode.blaisemath.style.context;

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

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.Maps;
import com.googlecode.blaisemath.style.PathStyle;
import com.googlecode.blaisemath.style.PathStyleBasic;
import com.googlecode.blaisemath.style.PointStyle;
import com.googlecode.blaisemath.style.PointStyleBasic;
import com.googlecode.blaisemath.style.ShapeStyle;
import com.googlecode.blaisemath.style.ShapeStyleBasic;
import com.googlecode.blaisemath.style.Style;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.style.TextStyle;
import com.googlecode.blaisemath.style.TextStyleBasic;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/** 
 * Default instance of the style provider. This is an immutable class that
 * provides non-null, default styles.
 */
public final class StyleContextBasic implements StyleContext<Object> {
    
    private static final StyleContextBasic INST = new StyleContextBasic();
    
    private final ClassToInstanceMap<Style> defaultStyleMap;
    private final Map<Class,StyleModifier> modifierMap;

    // this class is only intended for use as a static singleton
    private StyleContextBasic() {
        defaultStyleMap = ImmutableClassToInstanceMap.<Style>builder()
                .put(PathStyle.class, Styles.defaultPathStyle())
                .put(ShapeStyle.class, Styles.defaultShapeStyle())
                .put(PointStyle.class, Styles.defaultPointStyle())
                .put(TextStyle.class, Styles.defaultTextStyle())
                .build();
        modifierMap = Maps.newHashMap();
        modifierMap.put(PathStyle.class, StyleModifiers.pathStyleModifier());
        modifierMap.put(PathStyleBasic.class, StyleModifiers.pathStyleModifier());
        modifierMap.put(ShapeStyle.class, StyleModifiers.shapeStyleModifier());
        modifierMap.put(ShapeStyleBasic.class, StyleModifiers.shapeStyleModifier());
        modifierMap.put(PointStyle.class, StyleModifiers.pointStyleModifier());
        modifierMap.put(PointStyleBasic.class, StyleModifiers.pointStyleModifier());
        modifierMap.put(TextStyle.class, StyleModifiers.textStyleModifier());
        modifierMap.put(TextStyleBasic.class, StyleModifiers.textStyleModifier());
    }
    
    /**
     * Return static singleton instance of this class.
     * @return instance
     */
    public static StyleContextBasic getInstance() {
        return INST;
    }

    @Override
    public <T extends Style> T getStyle(Class<T> cls, T style, Object src, StyleHintSet hints) {
        if (style != null) {
            return modifierMap.containsKey(style.getClass())
                    ? (T) modifierMap.get(style.getClass()).apply(style, hints)
                    : style;
        } else {
            for (Class c : defaultStyleMap.keySet()) {
                if (c == cls) {
                    Style sty = style == null ? defaultStyleMap.get(c) : style;
                    return (T) modifierMap.get(c).apply(sty, hints);
                }
            }
            Logger.getLogger(StyleContextBasic.class.getName()).log(Level.WARNING, 
                    "Styles of type {0} are not supported in this context.", cls);
            return null;
        }
    }

}
