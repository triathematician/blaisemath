/**
 * AttributeGraphicUpdater.java
 * Created on Mar 23, 2015
 */
package com.googlecode.blaisemath.graphics.core;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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

import com.google.common.base.Function;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.util.swing.ContextMenuInitializer;
import java.awt.geom.Rectangle2D;
import javax.annotation.Nonnull;

/**
 * A graphic updater that operates on a per-item basis, and provides built in
 * functions for setting graphic styles, and for initializing graphic context menus.
 * @param <V> type of object being updated
 * @author petereb1
 */
public abstract class AttributeGraphicUpdater<V> implements GraphicUpdater<V> {

    /** Generates attributes from base object. */
    private Function<V,AttributeSet> attr;
    /** Initializes context menus */
    private ContextMenuInitializer<V> menu;

    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    public Function<V, AttributeSet> getAttributer() {
        return attr;
    }

    public void setAttributer(Function<V, AttributeSet> attr) {
        this.attr = attr;
    }

    public ContextMenuInitializer<V> getMenuInitializer() {
        return menu;
    }

    public void setMenuInitializer(ContextMenuInitializer<V> menu) {
        this.menu = menu;
    }
        
    //</editor-fold>
    
    /**
     * Create a new graphic for the given object. The
     * {@link #update(java.lang.Object, java.awt.geom.Rectangle2D, com.googlecode.blaisemath.graphics.core.Graphic)}
     * method will first create an {@link AttributeSet} associated with the object,
     * provided to this method as the {@code style} parameter, and after creating
     * the graphic will register a {@link ContextMenuInitializer} if set.
     * 
     * @param e the object represented by the graphic
     * @param attr attributes for the graphic, as created by the attributer function
     * @param bounds desired bounding box for the graphic
     * @return graphic
     */
    public abstract Graphic create(V e, AttributeSet attr, Rectangle2D bounds);
    
    /**
     * Update an existing graphic for the given object.
     * @param e the object represented by the graphic
     * @param attr attributes for the graphic, as created by the attributer function
     * @param bounds desired bounding box for the graphic
     * @param existing the existing graphic (guaranteed to be non-null)
     */
    public abstract void update(V e, AttributeSet attr, Rectangle2D bounds, @Nonnull Graphic existing);

    @Override
    public Graphic update(V e, Rectangle2D bounds, Graphic existing) {
        AttributeSet as = attr == null ? new AttributeSet() : attr.apply(e);
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
