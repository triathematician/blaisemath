package com.googlecode.blaisemath.coordinate

import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.google.common.collect.Sets
import com.googlecode.blaisemath.annotation.InvokedFromThread
import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.util.*
import java.util.concurrent.ConcurrentMap
import java.util.function.Consumer
import java.util.stream.Collectors

/*-
* #%L
* blaise-common
* --
* Copyright (C) 2014 - 2021 Elisha Peterson
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
*/ /**
 * Tracks locations of a collection of objects in a thread-safe manner.
 * Maintains a cache of prior locations, so that if some of the objects are removed,
 * this class "remembers" their prior locations. Listeners may register to be notified
 * when any of the coordinates within the manager change, or when any objects are
 * added to or removed from the manager.
 *
 *
 * The object is thread safe, so the points in the manager can be read from or written to
 * by multiple threads. Thread safety involves managing access to three interdependent
 * state variables, representing the cached locations, the objects that are "active" and
 * the objects that are "inactive". It is fine to iterate over these sets from any thread,
 * although they may change during iteration.
 *
 *
 * Care should be taken with event handlers to ensure thread safety. Listeners
 * registering for [CoordinateChangeEvent]s are notified of the change from
 * the thread that makes the change. Collections passed with the event will be
 * either immutable copies, or references passed to this object as parameters to
 * a mutator method.
 *
 * @param <S> type of source object
 * @param <C> type of point
 *
 * @author Elisha Peterson
</C></S> */
class CoordinateManager<S, C> private constructor(
        /** Max size of the cache  */
        private val maxCacheSize: Int
) {
    /** Map with current objects and locations (stores the data).  */
    private val map: ConcurrentMap<S?, C?>? = Maps.newConcurrentMap()

    /** Active objects. This value may be set.  */
    private var active: MutableSet<S?>? = Sets.newConcurrentHashSet()

    /** Cached objects.  */
    private val inactive: MutableSet<S?>? = Sets.newConcurrentHashSet()

    /** Listeners that will receive updates.  */
    private val listeners: MutableList<CoordinateListener<*, *>?>? = Lists.newCopyOnWriteArrayList()

    //endregion
    //region PROPERTIES/QUERIES
    fun getMaxCacheSize(): Int {
        return maxCacheSize
    }

    /**
     * Return objects currently tracked by the manager.
     * @return objects
     */
    fun getActive(): MutableSet<S?>? {
        return Collections.unmodifiableSet(active)
    }

    /**
     * Returns cached objects.
     * @return cached objects
     */
    fun getInactive(): MutableSet<S?>? {
        return Collections.unmodifiableSet(inactive)
    }

    /**
     * Retrieve location of a single point, whether active or inactive.
     * @param obj object to retrieve
     * @return location
     */
    fun getLocation(obj: S?): C? {
        return map.get(obj)
    }

    /**
     * Tests to see if provided item has a location.
     * @param obj object to test
     * @return true if location is tracked
     */
    fun locates(obj: S?): Boolean {
        return map.keys.contains(obj)
    }

    /**
     * Tests to see if all provided items are contained in either current
     * locations or cached locations.
     * @param objs objects to test
     * @return true if all are tracked, false otherwise
     */
    fun locatesAll(objs: MutableCollection<out S?>?): Boolean {
        return map.keys.containsAll(objs)
    }

    /**
     * Returns copy of map with active locations. This method blocks on the entire
     * cache, since it uses both state variables.
     * @return object locations
     */
    @Synchronized
    fun getActiveLocationCopy(): MutableMap<S?, C?>? {
        return getLocationCopy<S?>(active)
    }

    /**
     * Returns copy of map with inactive locations. This method blocks on the entire
     * cache, since it uses both state variables.
     * @return object locations
     */
    @Synchronized
    fun getInactiveLocationCopy(): MutableMap<S?, C?>? {
        return getLocationCopy<S?>(inactive)
    }

    /**
     * Retrieve location of given set of objects, whether active or inactive.
     * @param <T> type of object in provided set
     * @param obj objects to retrieve
     * @return map of locations
    </T> */
    fun <T : S?> getLocationCopy(obj: MutableSet<T?>?): MutableMap<S?, C?>? {
        synchronized(map) { return obj.stream().collect(Collectors.toMap({ s: T? -> s }) { key: T? -> map.get(key) }) }
    }
    //endregion
    //region MUTATORS
    /**
     * Adds a single additional location to the manager. Use [.putAll]
     * wherever possible as it will be more efficient.
     * @param s source object
     * @param c coordinate
     */
    fun put(s: S?, c: C?) {
        putAll(Collections.singletonMap(s, c))
    }

    /**
     * Adds additional locations to the manager. Blocks while the map is being
     * updated, since it may change the active and cached object sets.
     * Propagates the updated coordinates to interested listeners (on the invoking thread).
     * @param map new coordinates
     */
    fun putAll(map: MutableMap<S?, out C?>?) {
        val copy: MutableMap<S?, C?>? = Maps.newHashMap(map)
        synchronized(this) {
            this.map.putAll(copy)
            active.addAll(copy.keys)
            inactive.removeAll(copy.keys)
        }
        fireCoordinatesChanged(CoordinateChangeEvent.Companion.createAddEvent(this, copy))
    }

    /**
     * Replaces the current set of objects with specified objects, and caches the rest.
     * Propagates the updated coordinates to interested listeners (on the invoking thread).
     * @param map new coordinates
     */
    fun setCoordinateMap(map: MutableMap<S?, out C?>?) {
        val coordCopy: MutableMap<S?, C?>? = Maps.newHashMap(map)
        var toCache: MutableSet<S?>?
        synchronized(this) {
            toCache = Sets.difference(this.map.keys, coordCopy.keys).immutableCopy()
            this.map.putAll(coordCopy)
            active = Sets.newConcurrentHashSet(coordCopy.keys)
            inactive.removeAll(coordCopy.keys)
            inactive.addAll(toCache)
            checkCache()
        }
        fireCoordinatesChanged(CoordinateChangeEvent.Companion.createAddRemoveEvent(this, coordCopy, toCache))
    }

    /**
     * Removes objects from the manager without caching their locations.
     * Propagates the updated coordinates to interested listeners (on the invoking thread).
     * @param obj objects to remove
     */
    fun forget(obj: MutableSet<out S?>?) {
        val removed: MutableSet<S?> = HashSet()
        synchronized(map) {
            obj.stream().filter { k: S? -> map.remove(k) != null }
                    .forEach { e: E? -> removed.add(e) }
        }
        fireCoordinatesChanged(CoordinateChangeEvent.Companion.createRemoveEvent(this, removed))
    }

    /**
     * Makes specified objects inactive, possibly removing them from memory.
     * Propagates the updated coordinates to interested listeners (on the invoking thread).
     * @param <T> type of object in provided set
     * @param obj objects to removeObjects
    </T> */
    fun <T : S?> deactivate(obj: MutableSet<T?>?) {
        var removed: MutableSet<T?>?
        synchronized(this) {
            removed = Sets.intersection(obj, active).immutableCopy()
            active.removeAll(removed)
            inactive.addAll(removed)
            checkCache()
        }
        fireCoordinatesChanged(CoordinateChangeEvent.Companion.createRemoveEvent(this, removed))
    }

    /**
     * Call to restore locations from the cache and make the given objects active again.
     * @param <T> type of object in provided set
     * @param obj objects to restore
     * @return true if cache was changed
    </T> */
    fun <T : S?> reactivate(obj: MutableSet<T?>?): Boolean {
        val restoreMap: MutableMap<S?, C?> = Maps.newHashMap()
        synchronized(this) {
            val restored: MutableSet<T?>? = Sets.intersection(obj, inactive)
            restored.forEach(Consumer { t: T? -> restoreMap[t] = map.get(t) })
            active.addAll(restored)
            inactive.removeAll(restored)
        }
        fireCoordinatesChanged(CoordinateChangeEvent.Companion.createAddEvent(this, restoreMap))
        return !restoreMap.isEmpty()
    }

    /**
     * Call to ensure appropriate size of cache. Should always be called within
     * a synchronization block.
     */
    private fun checkCache() {
        val n = inactive.size - maxCacheSize
        if (n > 0) {
            val remove: MutableSet<S?>? = Sets.newHashSet(Iterables.limit(inactive, n))
            inactive.removeAll(remove)
            map.keys.removeAll(remove)
        }
    }
    //endregion
    //region EVENTS
    /**
     * Fire update, from the thread that invoked the change.
     * The collections in the event are either provided as arguments to
     * `this`, or are immutable lists, and therefore may be used freely
     * from any thread.
     *
     * @param evt the event to fire
     */
    @InvokedFromThread("unknown")
    protected fun fireCoordinatesChanged(evt: CoordinateChangeEvent<S?, C?>?) {
        val added = evt.getAdded()
        val removed = evt.getRemoved()
        if ((added == null || added.isEmpty()) && (removed == null || removed.isEmpty())) {
            return
        }
        listeners.forEach(Consumer { cl: CoordinateListener<*, *>? -> cl.coordinatesChanged(evt) })
    }

    fun addCoordinateListener(cl: CoordinateListener<*, *>?) {
        Objects.requireNonNull(cl)
        listeners.add(cl)
    }

    fun removeCoordinateListener(cl: CoordinateListener<*, *>?) {
        Objects.requireNonNull(cl)
        listeners.remove(cl)
    } //endregion

    companion object {
        //region FACTORY METHOD
        /**
         * Create and return new instance of coordinate manager.
         * @param <S> type of source object
         * @param <C> type of point
         * @param maxCacheSize maximum # of active and inactive points to include
         * @return newly created coordinate manager.
        </C></S> */
        fun <S, C> create(maxCacheSize: Int): CoordinateManager<S?, C?>? {
            return CoordinateManager(maxCacheSize)
        }
    }
}