/*
 * GraphicUpdater.java
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


import java.awt.geom.Rectangle2D;
import com.googlecode.blaisemath.graphics.core.Graphic;

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
