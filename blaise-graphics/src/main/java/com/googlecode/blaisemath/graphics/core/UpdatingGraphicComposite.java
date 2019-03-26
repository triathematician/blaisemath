package com.googlecode.blaisemath.graphics.core;

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


import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import static com.googlecode.blaisemath.util.Preconditions.checkNotNull;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Set;

/**
 * Encapsulates a set of graphics as a composite, along with elements used
 * to create/update the graphics.
 * 
 * @author petereb1
 * @param <T> type of object represented by the composite
 */
@Beta
public class UpdatingGraphicComposite<T> {

    /** Contains the graphic elements */
    private final GraphicComposite<Graphics2D> composite = new GraphicComposite<Graphics2D>();
    /** Index for the graphics, based on source object */
    private final BiMap<T, Graphic<Graphics2D>> index = HashBiMap.create();
    /** Creates/updates the graphics */
    private GraphicUpdater<T> updater;

    public UpdatingGraphicComposite(GraphicUpdater<T> updater) {
        this.updater = checkNotNull(updater);
    }
    
    public static <T> UpdatingGraphicComposite<T> create(GraphicUpdater<T> updater) {
        return new UpdatingGraphicComposite<T>(updater);
    }

    public void setObjects(Iterable<T> data, Function<T,Rectangle2D> locs) {
        Set<Graphic<Graphics2D>> toRemove = Sets.newHashSet(composite.getGraphics());
        for (T t : data) {
            if (index.containsKey(t)) {
                toRemove.remove(index.get(t));
            }
        }
        composite.removeGraphics(toRemove);
        index.keySet().retainAll(data instanceof Collection ? (Collection) data : Lists.newArrayList(data));
        
        for (T obj : data) {
            Graphic<Graphics2D> existing = index.get(obj);
            Rectangle2D loc = locs.apply(obj);
            if (loc == null && existing != null) {
                composite.removeGraphic(existing);
                index.remove(obj);
            } else if (loc != null) {
                Graphic gfc = updater.update(obj, loc, existing);
                if (existing == null) {
                    index.put(obj, gfc);
                    composite.addGraphic(gfc);
                }
            }
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    public GraphicComposite<Graphics2D> getGraphic() {
        return composite;
    }

    public GraphicUpdater<T> getUpdater() {
        return updater;
    }

    public void setUpdater(GraphicUpdater<T> updater) {
        this.updater = updater;
        // TODO rebuild if different
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="LOOKUPS">
    
    public T objectOf(Graphic<Graphics2D> gfc) {
        return index.inverse().get(gfc);
    }

    public Graphic<Graphics2D> graphicOf(T obj) {
        return index.get(obj);
    }

    //</editor-fold>
    
}
