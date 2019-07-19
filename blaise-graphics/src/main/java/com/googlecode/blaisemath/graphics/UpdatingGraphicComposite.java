package com.googlecode.blaisemath.graphics;

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
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Encapsulates a set of graphics as a composite, along with elements used
 * to create/update the graphics.
 * 
 * @author Elisha Peterson
 * @param <T> type of object represented by the composite
 */
@Beta
public class UpdatingGraphicComposite<T, G> {

    /** Contains the graphic elements */
    private final GraphicComposite<G> composite = new GraphicComposite<>();
    /** Index for the graphics, based on source object */
    private final BiMap<T, Graphic<G>> index = HashBiMap.create();
    /** Creates/updates the graphics */
    private final GraphicUpdater<T,G> updater;

    public UpdatingGraphicComposite(GraphicUpdater<T, G> updater) {
        this.updater = requireNonNull(updater);
    }

    public static <T, G> UpdatingGraphicComposite<T, G> create(GraphicUpdater<T, G> updater) {
        return new UpdatingGraphicComposite<>(updater);
    }
    
    //region PROPERTIES

    public GraphicComposite<G> getGraphic() {
        return composite;
    }

    public GraphicUpdater<T, G> getUpdater() {
        return updater;
    }

    public void setObjects(Iterable<T> data, Function<T, @Nullable Rectangle2D> locMap) {
        Set<Graphic<G>> toRemove = Sets.newHashSet(composite.getGraphics());
        for (T t : data) {
            if (index.containsKey(t)) {
                toRemove.remove(index.get(t));
            }
        }
        composite.removeGraphics(toRemove);
        index.keySet().retainAll(data instanceof Collection ? (Collection<T>) data : Lists.newArrayList(data));

        updateItemGraphics(locMap);
    }

    private void updateItemGraphics(Function<T, @Nullable Rectangle2D> locMap) {
        for (T obj : index.keySet()) {
            Graphic<G> existing = index.get(obj);
            Rectangle2D loc = locMap.apply(obj);
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

    //endregion
    
    //region LOOKUPS
    
    public T objectOf(Graphic<G> gfc) {
        return index.inverse().get(gfc);
    }

    public Graphic<G> graphicOf(T obj) {
        return index.get(obj);
    }

    //endregion
    
}
