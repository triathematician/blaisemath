package com.googlecode.blaisemath.graphics.core;

/*
 * #%L
 * BlaiseGraphics
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

import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.util.swing.ContextMenuInitializer;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.geom.Rectangle2D;
import java.util.function.Function;

/**
 * A graphic updater that operates on a per-item basis, and provides built in
 * functions for setting graphic styles, and for initializing graphic context menus.
 * @param <E> type of object being updated
 * @param <G> graphics canvas
 * @author Elisha Peterson
 */
public abstract class AttributeGraphicUpdater<E,G> implements GraphicUpdater<E,G> {

    /** Generates attributes from base object. */
    private @Nullable Function<E, AttributeSet> attributeMap;
    /** Initializes context menus */
    private ContextMenuInitializer<E> menu;

    //region PROPERTIES

    public @Nullable Function<E, AttributeSet> getAttributeMap() {
        return attributeMap;
    }

    public void setAttributeMap(@Nullable Function<E, AttributeSet> attr) {
        this.attributeMap = attr;
    }

    public ContextMenuInitializer<E> getMenuInitializer() {
        return menu;
    }

    public void setMenuInitializer(ContextMenuInitializer<E> menu) {
        this.menu = menu;
    }
        
    //endregion
    
    /**
     * Create a new graphic for the given object. The {@link #update(Object, Rectangle2D, Graphic)}
     * method will first create an {@link AttributeSet} associated with the object,
     * provided to this method as the {@code style} parameter, and after creating
     * the graphic will register a {@link ContextMenuInitializer} if set.
     * @param e the object represented by the graphic
     * @param attr attributes for the graphic, as created by the attribute function
     * @param bounds desired bounding box for the graphic
     * @return graphic
     */
    public abstract Graphic<G> create(E e, AttributeSet attr, Rectangle2D bounds);
    
    /**
     * Update an existing graphic for the given object.
     * @param e the object represented by the graphic
     * @param attr attributes for the graphic, as created by the attribute function
     * @param bounds desired bounding box for the graphic
     * @param existing the existing graphic (guaranteed to be non-null)
     */
    public abstract void update(E e, AttributeSet attr, Rectangle2D bounds, Graphic existing);

    @Override
    public Graphic<G> update(E e, Rectangle2D bounds, Graphic<G> existing) {
        AttributeSet as = attributeMap == null ? new AttributeSet() : attributeMap.apply(e);
        if (existing == null) {
            Graphic gfc = create(e, as, bounds);
            if (menu != null) {
                gfc.addContextMenuInitializer(menu);
            }
            return gfc;
        } else {
            update(e, as, bounds, existing);
            return existing;
        }
    }
    
}
