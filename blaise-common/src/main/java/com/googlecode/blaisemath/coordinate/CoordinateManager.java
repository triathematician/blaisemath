package com.googlecode.blaisemath.coordinate;

/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2019 Elisha Peterson
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

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import com.googlecode.blaisemath.annotation.InvokedFromThread;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import static java.util.stream.Collectors.toMap;

/**
 * Tracks locations of a collection of objects in a thread-safe manner.
 * Maintains a cache of prior locations, so that if some of the objects are removed,
 * this class "remembers" their prior locations. Listeners may register to be notified
 * when any of the coordinates within the manager change, or when any objects are
 * added to or removed from the manager.
 * <p>
 * The object is thread safe, so the points in the manager can be read from or written to
 * by multiple threads. Thread safety involves managing access to three interdependent
 * state variables, representing the cached locations, the objects that are "active" and
 * the objects that are "inactive". It is fine to iterate over these sets from any thread,
 * although they may change during iteration.
 * <p>
 * Care should be taken with event handlers to ensure thread safety. Listeners
 * registering for {@link CoordinateChangeEvent}s are notified of the change from
 * the thread that makes the change. Collections passed with the event will be
 * either immutable copies, or references passed to this object as parameters to
 * a mutator method.
 *
 * @param <S> type of source object
 * @param <C> type of point
 *
 * @author Elisha Peterson
 */
public final class CoordinateManager<S, C> {
    
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
    
    //region FACTORY METHOD
    
    /**
     * Create and return new instance of coordinate manager.
     * @param <S> type of source object
     * @param <C> type of point
     * @param maxCacheSize maximum # of active and inactive points to include
     * @return newly created coordinate manager.
     */
    public static <S,C> CoordinateManager<S,C> create(int maxCacheSize) {
        return new CoordinateManager<>(maxCacheSize);
    }
    
    //endregion

    //region PROPERTIES/QUERIES
    
    public int getMaxCacheSize() {
        return maxCacheSize;
    }

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
     * Retrieve location of a single point, whether active or inactive.
     * @param obj object to retrieve
     * @return location
     */
    public C getLocation(S obj) {
        return map.get(obj);
    }

    /**
     * Tests to see if provided item has a location.
     * @param obj object to test
     * @return true if location is tracked
     */
    public boolean locates(S obj) {
        return map.keySet().contains(obj);
    }

    /**
     * Tests to see if all provided items are contained in either current
     * locations or cached locations.
     * @param objs objects to test
     * @return true if all are tracked, false otherwise
     */
    public boolean locatesAll(Collection<? extends S> objs) {
        return map.keySet().containsAll(objs);
    }

    /**
     * Returns copy of map with active locations. This method blocks on the entire
     * cache, since it uses both state variables.
     * @return object locations
     */
    public synchronized Map<S, C> getActiveLocationCopy() {
        return getLocationCopy(active);
    }

    /**
     * Returns copy of map with inactive locations. This method blocks on the entire
     * cache, since it uses both state variables.
     * @return object locations
     */
    public synchronized Map<S, C> getInactiveLocationCopy() {
        return getLocationCopy(inactive);
    }
    
    /**
     * Retrieve location of given set of objects, whether active or inactive.
     * @param <T> type of object in provided set
     * @param obj objects to retrieve
     * @return map of locations
     */
    public <T extends S> Map<S,C> getLocationCopy(Set<T> obj) {
        synchronized(map) {
            return obj.stream().collect(toMap(s -> s, map::get));
        }
    }
    
    //endregion
    
    //region MUTATORS

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
     * @param map new coordinates
     */
    public void putAll(Map<S,? extends C> map) {
        Map<S,C> copy = Maps.newHashMap(map);
        synchronized (this) {
            this.map.putAll(copy);
            active.addAll(copy.keySet());
            inactive.removeAll(copy.keySet());
        }
        fireCoordinatesChanged(CoordinateChangeEvent.createAddEvent(this, copy));
    }

    /**
     * Replaces the current set of objects with specified objects, and caches the rest.
     * Propagates the updated coordinates to interested listeners (on the invoking thread).
     * @param map new coordinates
     */
    public void setCoordinateMap(Map<S,? extends C> map) {
        Map<S,C> coordCopy = Maps.newHashMap(map);
        Set<S> toCache;
        synchronized(this) {
            toCache = Sets.difference(this.map.keySet(), coordCopy.keySet()).immutableCopy();
            this.map.putAll(coordCopy);
            active = Sets.newConcurrentHashSet(coordCopy.keySet());
            inactive.removeAll(coordCopy.keySet());
            inactive.addAll(toCache);
            checkCache();
        }
        fireCoordinatesChanged(CoordinateChangeEvent.createAddRemoveEvent(this, coordCopy, toCache));
    }

    /**
     * Removes objects from the manager without caching their locations.
     * Propagates the updated coordinates to interested listeners (on the invoking thread).
     * @param obj objects to remove
     */
    public void forget(Set<? extends S> obj) {
        Set<S> removed = new HashSet<>();
        synchronized (map) {
            obj.stream().filter(k -> map.remove(k) != null)
                    .forEach(removed::add);
        }
        fireCoordinatesChanged(CoordinateChangeEvent.createRemoveEvent(this, removed));
    }

    /**
     * Makes specified objects inactive, possibly removing them from memory.
     * Propagates the updated coordinates to interested listeners (on the invoking thread).
     * @param <T> type of object in provided set
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
     * Call to restore locations from the cache and make the given objects active again.
     * @param <T> type of object in provided set
     * @param obj objects to restore
     * @return true if cache was changed
     */
    public <T extends S> boolean reactivate(Set<T> obj) {
        Map<S,C> restoreMap = Maps.newHashMap();
        synchronized (this) {
            Set<T> restored = Sets.intersection(obj, inactive);
            restored.forEach(t -> restoreMap.put(t, map.get(t)));
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
    
    //endregion

    //region EVENTS

    /**
     * Fire update, from the thread that invoked the change.
     * The collections in the event are either provided as arguments to
     * {@code this}, or are immutable lists, and therefore may be used freely
     * from any thread.
     * 
     * @param evt the event to fire
     */
    @InvokedFromThread("unknown")
    protected final void fireCoordinatesChanged(CoordinateChangeEvent<S,C> evt) {
        Map<S, C> added = evt.getAdded();
        Set<S> removed = evt.getRemoved();
        if ((added == null || added.isEmpty()) && (removed == null || removed.isEmpty())) {
            return;
        }
        listeners.forEach(cl -> cl.coordinatesChanged(evt));
    }

    public final void addCoordinateListener(CoordinateListener cl) {
        requireNonNull(cl);
        listeners.add(cl);
    }

    public final void removeCoordinateListener(CoordinateListener cl) {
        requireNonNull(cl);
        listeners.remove(cl);
    }

    //endregion

}
