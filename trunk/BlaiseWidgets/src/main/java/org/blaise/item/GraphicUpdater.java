/*
 * GraphicUpdater.java
 * Created Apr 28, 2013
 */
package org.blaise.item;

import java.awt.geom.Rectangle2D;
import org.blaise.graphics.Graphic;

/**
 * Creates and/or modifies a graphic based on an item.
 * 
 * @author Elisha
 */
public interface GraphicUpdater<Item> {
   
    /**
     * Create or modify and return a graphic for the given item
     * @param item the item represented
     * @param bounds bounding box for the item
     * @param existingGraphic existing version of the graphic, may be null
     * @return new or modified graphic
     */
    Graphic update(Item item, Rectangle2D bounds, Graphic existingGraphic);
    
}
