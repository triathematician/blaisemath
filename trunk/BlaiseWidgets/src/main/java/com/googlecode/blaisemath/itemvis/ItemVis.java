/*
 * ItemVis.java
 * Created Apr 28, 2013
 */
package com.googlecode.blaisemath.itemvis;

/*
 * #%L
 * BlaiseWidgets
 * --
 * Copyright (C) 2012 - 2015 Elisha Peterson
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


import static com.google.common.base.Preconditions.*;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import com.googlecode.blaisemath.graphics.core.Graphic;

/**
 * <p>
 *  Visualization of a collection of items. Uses delegates to locate the items,
 *  and to construct and/or modify the items.
 * </p>
 * 
 * @author Elisha
 */
public final class ItemVis<X> {
    
    /** Collection of items being displayed */
    private Collection<X> items = Collections.EMPTY_LIST;
    /** Object used to create or update item graphics */
    private GraphicUpdater<X> itemGraphicUpdater = null;
    /** Object used to layout items */
    private Function<Collection<X>,Map<X,Rectangle2D>> itemLayout = null;
    
    /** Contains the resulting graphic objects */
    private final Map<X,Graphic> graphics = Maps.newLinkedHashMap();
    

    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
    public Collection<X> getItems() {
        return items;
    }

    /**
     * Set the collection of items
     * @param items items (must be non-null)
     */
    public void setItems(Collection<X> items) {
        checkNotNull(items);
        if (this.items != items) {
            this.items = items;
            updateGraphics();
        }
    }

    public GraphicUpdater<X> getItemGraphicUpdater() {
        return itemGraphicUpdater;
    }

    public void setItemGraphicUpdater(GraphicUpdater<X> itemGraphicUpdater) {
        if (this.itemGraphicUpdater != itemGraphicUpdater) {
            this.itemGraphicUpdater = itemGraphicUpdater;
            updateGraphics();
        }
    }

    public Function<Collection<X>, Map<X,Rectangle2D>> getItemLayout() {
        return itemLayout;
    }

    public void setItemLayout(Function<Collection<X>, Map<X,Rectangle2D>> itemLayout) {
        if (this.itemLayout != itemLayout) {
            this.itemLayout = itemLayout;
            updateGraphics();
        }
    }
    
    public Collection<Graphic> getItemGraphics() {
        return graphics.values();
    }
    
    //</editor-fold>
    
    
    /**
     * Updates the graphic objects represented by the set of items.
     */
    private void updateGraphics() {
        if (itemLayout == null || itemGraphicUpdater == null) {
            // unable to update graphics
            graphics.clear();
        } else {
            // apply layout
            Map<X,Rectangle2D> layout = itemLayout.apply(items);
            // for each item
            for (X it : items) {
                // get layout bounds
                Rectangle2D bounds = layout.get(it);
                checkNotNull(bounds);
                // get previous graphic, if it exists
                Graphic existingOrNull = graphics.get(it);
                // layout and put result in map
                Graphic nue = itemGraphicUpdater.update(it, layout.get(it), existingOrNull);
                graphics.put(it, nue);
            }
        }
    }
    
}
