/**
 * UpdatingComposite.java 
 * Created on Aug 25, 2015
 */
package com.googlecode.blaisemath.graphics.core;

/*
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2009 - 2017 Elisha Peterson
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
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import static com.googlecode.blaisemath.util.Preconditions.checkNotNull;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Encapsulates a set of graphics as a composite, along with elements used
 * to create/update the graphics. This class is not thread-safe.
 * 
 * @param <T> type of source object to be updated
 * @param <G> graphics canvas type 
 * 
 * @author Elisha
 */
public class UpdatingGraphicComposite<G,T> {

    /** Contains the graphic elements */
    private final GraphicComposite<G> composite = new GraphicComposite<G>();
    /** Index for the graphics, based on source object */
    private final BiMap<T, Graphic<G>> index = HashBiMap.create();
    /** Item bounding boxes */
    private final Map<T, Rectangle2D> bounds = Maps.newLinkedHashMap();
    /** Creates/updates the graphics */
    private GraphicUpdater<G,T> updater;

    /**
     * Creates the composite with the given updater
     * @param updater updater
     */
    public UpdatingGraphicComposite(GraphicUpdater<G,T> updater) {
        this.updater = checkNotNull(updater);
    }
    
    /**
     * Creates the composite with the given updater
     * @param <G> canvas type
     * @param <T> object type
     * @param updater updater
     * @return new instance
     */
    public static <G,T> UpdatingGraphicComposite<G,T> create(GraphicUpdater<G,T> updater) {
        return new UpdatingGraphicComposite<G,T>(updater);
    }
    
    /**
     * Gets the current set of bounding boxes.
     * @return bounding boxes
     */
    public Map<T, Rectangle2D> getObjectBounds() {
        return bounds;
    }
    
    /**
     * Updates the objects behind the graphics.
     * @param data the new data objects
     * @param locs bounding boxes for the objects
     */
    public void setObjects(Iterable<T> data, Function<T,Rectangle2D> locs) {
        removeObjectsNotIn(data);
        updateBoundingBoxes(data, locs);
        rebuildGraphics();
    }
    
    private void removeObjectsNotIn(Iterable<T> data) {
        Set<Graphic<G>> toRemove = Sets.newHashSet(composite.getGraphics());
        for (T t : data) {
            if (index.containsKey(t)) {
                toRemove.remove(index.get(t));
            }
        }
        composite.removeGraphics(toRemove);
        
        Collection<T> dataColl = data instanceof Collection ? (Collection) data : Lists.newArrayList(data);
        index.keySet().retainAll(dataColl);
        bounds.keySet().retainAll(dataColl);
    }
    
    private void updateBoundingBoxes(Iterable<T> data, Function<T,Rectangle2D> locs) {
        for (T t : data) {
            bounds.put(t, locs.apply(t));
        }
    }
    
    private void rebuildGraphics() {
        for (Entry<T,Rectangle2D> en : bounds.entrySet()) {
            T obj = en.getKey();
            Graphic<G> existing = index.get(obj);
            Rectangle2D loc = en.getValue();
            if (loc == null && existing != null) {
                composite.removeGraphic(existing);
                index.remove(obj);
            } else if (loc != null) {
                Graphic gfc = updater.update(obj, loc, existing);
                index.put(obj, gfc);
                if (existing != gfc) {
                    composite.removeGraphic(existing);
                    composite.addGraphic(gfc);
                }
            }
        }
        composite.fireGraphicChanged();
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    public GraphicComposite<G> getGraphic() {
        return composite;
    }

    public GraphicUpdater<G,T> getUpdater() {
        return updater;
    }

    public void setUpdater(GraphicUpdater<G,T> updater) {
        if (this.updater != updater) {
            this.updater = updater;
            rebuildGraphics();
        }
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="LOOKUPS">
    
    /**
     * Get the object corresponding to the given graphic.
     * @param gfc the graphic
     * @return object, if present, null otherwise
     */
    public T objectOf(Graphic<G> gfc) {
        return index.inverse().get(gfc);
    }

    /**
     * Get the graphic corresponding to the given object.
     * @param obj the object
     * @return graphic, if present, null otherwise
     */
    public Graphic<G> graphicOf(T obj) {
        return index.get(obj);
    }

    //</editor-fold>
    
}
