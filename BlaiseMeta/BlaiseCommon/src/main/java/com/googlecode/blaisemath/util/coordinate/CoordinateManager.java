/*
 * CoordinateManager.java
 * Created Oct 5, 2011
 */
package com.googlecode.blaisemath.util.coordinate;

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

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

/**
 * <p>
 * Tracks locations of a collection of objects in a thread-safe manner.
 * Maintains a cache of prior locations, so that if some of the objects are removed,
 * this class "remembers" their prior locations. Listeners may register to be notified
 * when any of the coordinates within the manager change, or when any objects are
 * added to or removed from the manager.
 * </p>
 * <p>
 * The object is thread safe, so the points in the manager can be read from or written to
 * by multiple threads. Thread safety involves managing access to three interdependent
 * state variables, representing the cached locations, the objects that are "active" and
 * the objects that are "inactive". It is fine to iterate over these sets from any thread,
 * although they may change during iteration.
 * </p>
 *
 * @param <S> type of source object
 * @param <C> type of point
 *
 * @author Elisha Peterson
 * 
 * @todo review thread safety of this class
 */
@ThreadSafe
public class CoordinateManager<S, C> {
    
    /** Max size of the cache */
    private final int maxCacheSize;
    
    /** Map with current objects and locations (stores the data) */
    @GuardedBy("this")
    private final ConcurrentMap<S, C> map = Maps.newConcurrentMap();
    /** Active objects. This value may be set. */
    @GuardedBy("this")
    private Set<S> active = Sets.newConcurrentHashSet();
    /** Cached objects */
    @GuardedBy("this")
    private final Set<S> inactive = Sets.newConcurrentHashSet();

    /** Listeners that will receive updates. */
    private final List<CoordinateListener> listeners = Lists.newCopyOnWriteArrayList();
    
    private CoordinateManager(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }
    
    //<editor-fold defaultstate="collapsed" desc="STATIC FACTORY METHOD">
    
    /**
     * Create and return new instance of coordinate manager.
     * @param maxCacheSize maximum # of active & inactive points to include
     */
    public static <S,C> CoordinateManager<S,C> create(int maxCacheSize) {
        return new CoordinateManager<S,C>(maxCacheSize);
    }
    
    //</editor-fold>

    /**
     * Return objects currently tracked by the manager.
     * @return objects
     */
    public Set<S> getActive() {
        return Collections.unmodifiableSet(active);
    }

    /**
     * Returns cached objects.
     * @return cached objects
     */
    public Set<S> getInactive() {
        return Collections.unmodifiableSet(inactive);
    }

    /**
     * Tests to see if all provided items are contained in either current
     * locations or cached locations.
     * @param obj objects to test
     * @return true if all are tracked, false otherwise
     */
    public boolean locatesAll(Collection<? extends S> obj) {
        return map.keySet().containsAll(obj);
    }

    /**
     * Returns copy of map with active locations. This method blocks on the entire
     * cache, since it uses both state variables.
     * @return object locations
     */
    public synchronized Map<S, C> getActiveLocationCopy() {
        Map<S, C> res = Maps.newHashMap();
        for (S s : active) {
            res.put(s, map.get(s));
        }
        return res;
    }
    
    /**
     * Retrieve location of given set of objects, whether active or inactive.
     * @param obj objects to retrieve
     * @return map of locations
     */
    public <T extends S> Map<S,C> getLocationCopy(Set<T> obj) {
        synchronized(map) {
            Map<S, C> res = Maps.newHashMap();
            for (S s : obj) {
                res.put(s, map.get(s));
            }
            return res;
        }
    }

    /**
     * Returns copy of map with inactive locations. This method blocks on the entire
     * cache, since it uses both state variables.
     * @return object locations
     */
    public synchronized Map<S, C> getInactiveLocationCopy() {
        Map<S, C> res = Maps.newHashMap();
        for (S s : inactive) {
            res.put(s, map.get(s));
        }
        return res;
    }

    //
    // MUTATORS
    //

    /**
     * Adds a single additional location to the manager. Use {@link #putAll(java.util.Map)}
     * wherever possible as it will be more efficient.
     * @param s source object
     * @param c coordinate
     */
    public void put(S s, C c) {
        putAll(Collections.singletonMap(s, c));
    }

    /**
     * Adds additional locations to the manager. Blocks while the map is being
     * updated, since it may change the active and cached object sets.
     * Propagates the updated coordinates to interested listeners (on the invoking thread).
     * @param coords new coordinates
     */
    public void putAll(Map<S,? extends C> coords) {
        synchronized (this) {
            map.putAll(coords);
            active.addAll(coords.keySet());
            inactive.removeAll(coords.keySet());
        }
        fireCoordinatesChanged(CoordinateChangeEvent.createAddEvent(this, coords));
    }

    /**
     * Replaces the current set of objects with specified objects, and caches the rest.
     * Propagates the updated coordinates to interested listeners (on the invoking thread).
     * @param coords new coordinates
     */
    public void setCoordinateMap(Map<S,? extends C> coords) {
        Set<S> toCache;
        synchronized(this) {
            toCache = Sets.difference(map.keySet(), coords.keySet()).immutableCopy();
            map.putAll(coords);
            active = Sets.newConcurrentHashSet(coords.keySet());
            inactive.removeAll(coords.keySet());
            inactive.addAll(toCache);
            checkCache();
        }
        fireCoordinatesChanged(CoordinateChangeEvent.createAddRemoveEvent(this, coords, toCache));
    }

    /**
     * Removes objects from the manager without caching their locations.
     * Propagates the updated coordinates to interested listeners (on the invoking thread).
     * @param obj objects to remove
     */
    public void forget(Set<? extends S> obj) {
        Set<S> removed = new HashSet<S>();
        synchronized (map) {
            for (S k : obj) {
                if (map.remove(k) != null) {
                    removed.add(k);
                }
            }
        }
        fireCoordinatesChanged(CoordinateChangeEvent.createRemoveEvent(this, removed));
    }

    /**
     * Makes specified objects inactive, possibly removing them from memory.
     * Propagates the updated coordinates to interested listeners (on the invoking thread).
     * @param obj objects to removeObjects
     */
    public <T extends S> void deactivate(Set<T> obj) {
        Set<T> removed;
        synchronized (this) {
            removed = Sets.intersection(obj, active).immutableCopy();
            active.removeAll(removed);
            inactive.addAll(removed);
            checkCache();
        }
        fireCoordinatesChanged(CoordinateChangeEvent.createRemoveEvent(this, removed));
    }

    /**
     * Call to restore locations from the cache.
     * @param obj objects to restore
     * @return true if cache was changed
     */
    public <T extends S> boolean reactivate(Set<T> obj) {
        Map<S,C> restoreMap = Maps.newHashMap();
        synchronized (this) {
            Set<T> restored = Sets.intersection(obj, inactive);
            for (T t : restored) {
                restoreMap.put(t, map.get(t));
            }
            active.addAll(restored);
            inactive.removeAll(restored);
        }
        fireCoordinatesChanged(CoordinateChangeEvent.createAddEvent(this, restoreMap));
        return !restoreMap.isEmpty();
    }

    /** 
     * Call to ensure appropriate size of cache. Should always be called within
     * a synchronization block.
     */
    private void checkCache() {
        int n = inactive.size() - maxCacheSize;
        if (n > 0) {
            Set<S> remove = Sets.newHashSet(Iterables.limit(inactive, n));
            inactive.removeAll(remove);
            map.keySet().removeAll(remove);
        }
    }


    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLING">
    //
    // EVENT HANDLING
    //

    /**
     * Fire update, from the thread that invoked the change.
     * The collections in the event are not guarded by {@code this}; they are either
     * are either provided as arguments to {@code this}, or are immutable lists.
     * @param evt the event to fire
     */
    protected final void fireCoordinatesChanged(CoordinateChangeEvent<S,C> evt) {
        if ((evt.getAdded() == null || evt.getAdded().isEmpty()) 
                && (evt.getRemoved() == null || evt.getRemoved().isEmpty())) {
            return;
        }
        for (CoordinateListener cl : listeners) {
            cl.coordinatesChanged(evt);
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
