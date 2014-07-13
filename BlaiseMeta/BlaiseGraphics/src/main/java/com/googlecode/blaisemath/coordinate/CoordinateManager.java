/*
 * CoordinateManager.java
 * Created Oct 5, 2011
 */
package com.googlecode.blaisemath.coordinate;

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

import com.google.common.base.Function;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Tracks locations of a collection of objects in a thread-safe manner.
 * Maintains a cacheObjects of prior locations, so that if some of the objects are removed,
 * this class "remembers" their prior locations. Listeners may register to be notified
 * when any of the coordinates within the manager change, or when any objects are
 * added to or removed from the manager.
 * </p>
 * <p>
 * Three constructors are provided, both of which require parameters ensuring that
 * objects have initial positions.
 * </p>
 * <p>
 * The object is thread safe, so the points in the manager can be read from or written to
 * by multiple threads.
 * </p>
 *
 * @param <S> type of source object
 * @param <C> type of point
 *
 * @author Elisha Peterson
 */
public class CoordinateManager<S, C> implements Function<S, C> {
    

    /** Map with current objects and locations (stores the data) */
    private final Map<S, C> map = Maps.newHashMap();
    /** Cached locations */
    private final Map<S, C> cache = Maps.newHashMap();

    /** Listeners that will receive updates */
    private final List<CoordinateListener> listeners = Collections.synchronizedList(new ArrayList<CoordinateListener>());
    
    
    //<editor-fold defaultstate="collapsed" desc="STATIC FACTORY METHOD">
    
    /**
     * Create and return new instance of coordinate manager.
     */
    public static <S,C> CoordinateManager<S,C> create() {
        return new CoordinateManager<S,C>();
    }
    
    //</editor-fold>
    

    public synchronized C apply(S src) {
        return map.get(src);
    }

    //
    // ACCESSORS
    //

    /**
     * Return objects currently tracked by the manager
     * @return objects
     */
    public synchronized Set<S> getObjects() {
        return map.keySet();
    }

    /**
     * Returns cached objects
     * @return cached objects
     */
    public synchronized Set<S> getCachedObjects() {
        return cache.keySet();
    }

    /**
     * Tests to see if all provided items are contained in either current
     * locations or cached locations.
     * @param obj objects to test
     * @return true if all are tracked, false otherwise
     */
    public synchronized boolean locatesAll(Collection<? extends S> obj) {
        for (S s : obj) {
            if (!map.containsKey(s) && !cache.containsKey(s)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns copy of map with object locations
     * @return object locations
     */
    public synchronized Map<S, C> getCoordinates() {
        return new HashMap<S, C>(map);
    }

    //
    // MUTATORS
    //

    /**
     * Adds a single additional location to the manager
     * @param s source object
     * @param c coordinate
     */
    public void put(S s, C c) {
        putAll(Collections.singletonMap(s, c));
    }

    /**
     * Adds additional locations to the manager. Sends out a property change event
     * to notify listeners if there are updates.
     * @param coords new coordinates
     */
    public synchronized void putAll(Map<S, ? extends C> coords) {
        map.putAll(coords);
        fireCoordinatesChanged(CoordinateChangeEvent.createAddEvent(this, coords));
    }

    /**
     * Replaces the current set of objects with specified objects, and caches the rest.
     * @param coords new coordinates
     */
    public synchronized void setCoordinateMap(Map<S, ? extends C> coords) {
        Map<S, C> cached = new HashMap<S, C>();
        for (S s : map.keySet()) {
            if (!coords.containsKey(s)) {
                cached.put(s, map.get(s));
            }
        }
        cache.putAll(cached);
        checkCache();
        map.keySet().removeAll(cached.keySet());
        map.putAll(coords);
        fireCoordinatesChanged(CoordinateChangeEvent.createAddRemoveEvent(this, coords, cached.keySet()));
    }

    /**
     * Removes objects from the manager without caching their locations
     * @param obj objects to remove
     */
    public synchronized void removeObjects(Set<? extends S> obj) {
        Set<S> removed = new HashSet<S>();
        for (S k : obj) {
            if (map.remove(k) != null) {
                removed.add(k);
            }
        }
        fireCoordinatesChanged(CoordinateChangeEvent.createRemoveEvent(this, removed));
    }

    /**
     * Removes specified objects to cacheObjects
     * @param obj objects to removeObjects
     */
    public synchronized void cacheObjects(Set<? extends S> obj) {
        Map<S,C> cached = new HashMap<S,C>();
        for (S k : obj) {
            if (map.containsKey(k)) {
                cached.put(k, map.remove(k));
            }
        }
        cache.putAll(cached);
        checkCache();
        fireCoordinatesChanged(CoordinateChangeEvent.createRemoveEvent(this, cached.keySet()));
    }
    
    /**
     * Get copy of all cached locations.
     * @return map of locations
     */
    public synchronized Map<S,C> getCachedLocations() {
        Map<S,C> res = new HashMap<S,C>();
        res.putAll(cache);
        return res;
    }
    
    /**
     * Retrieve location of given set of cached objects.
     * @param obj objects to retrieve
     * @return map of locations
     */
    public synchronized Map<S,C> getCachedLocations(Set<? extends S> obj) {
        Map<S,C> res = new HashMap<S,C>();
        for (S s : obj) {
            if (cache.containsKey(s)) {
                res.put(s, cache.get(s));
            }
        }
        return res;
    }

    /**
     * Call to restore locations from the cache.
     * @param obj objects to restore
     */
    public synchronized void restoreCached(Set<? extends S> obj) {
        Map<S,C> restored = new HashMap<S,C>();
        for (S k : obj) {
            C c = cache.remove(k);
            if (c != null && !map.containsKey(k)) {
                restored.put(k, c);
            }
        }
        putAll(restored);
    }

    /** Call to ensure appropriate size of cache */
    private void checkCache() {
        for (S m : map.keySet()) {
            cache.remove(m);
        }
    }


    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLING">
    //
    // EVENT HANDLING
    //

    protected final void fireCoordinatesChanged(CoordinateChangeEvent<S,C> evt) {
        synchronized(listeners) {
            for (CoordinateListener cl : listeners) {
                cl.coordinatesChanged(evt);
            }
        }
    }

    public final void addCoordinateListener(CoordinateListener cl) {
        checkNotNull(cl);
        listeners.add(cl);
    }

    public final void removeCoordinateListener(CoordinateListener cl) {
        checkNotNull(cl);
        listeners.remove(cl);
    }

    //</editor-fold>

}
