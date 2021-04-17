package com.googlecode.blaisemath.coordinate
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
 */

import com.google.errorprone.annotations.concurrent.GuardedBy
import com.googlecode.blaisemath.annotation.InvokedFromThread
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
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
 */
open class CoordinateManager<S, C>(val maxCacheSize: Int) {

    /** Map with current objects and locations (stores the data)  */
    @GuardedBy("this")
    private val map = ConcurrentHashMap<S, C>()
    /** Active objects. This value may be set.  */
    @GuardedBy("this")
    private var _active: Map<S, S> = ConcurrentHashMap<S, S>()
    val active
        get() = Collections.unmodifiableSet(_active.keys)
    /** Cached objects  */
    @GuardedBy("this")
    private var _inactive: Map<S, S> = ConcurrentHashMap<S, S>()
    val inactive
        get() = Collections.unmodifiableSet(_inactive.keys)
    /** Listeners that will receive updates.  */
    private val listeners: MutableList<CoordinateListener<S, C>> = CopyOnWriteArrayList()

    fun getLocation(obj: S) = map[obj]
    fun locates(obj: S) = obj in map.keys
    fun locatesAll(objs: Collection<S>) = map.keys.containsAll(objs)

    @get:Synchronized
    val activeLocationCopy: Map<S, C?>
        get() = getLocationCopy(active)
    @get:Synchronized
    val inactiveLocationCopy: Map<S, C?>
        get() = getLocationCopy(inactive)

    fun getLocationCopy(obj: Set<S>): Map<S, C?> {
        synchronized(map) {
            return map.filterKeys { it in obj }.toMap()
        }
    }

    //region MUTATORS

    fun put(s: S, c: C) = putAll(mapOf(s to c))
    fun putAll(map: Map<S, C>) {
        val copy = map.toMap()
        synchronized(this) {
            this.map.putAll(copy)
            active.addAll(copy.keys)
            inactive.removeAll(copy.keys)
        }
        fireCoordinatesChanged(CoordinateChangeEvent(this, added = copy))
    }

    /** Replace entire coordinate map. */
    fun setCoordinateMap(map: Map<S, C>) {
        val coordCopy = map.toMap()
        var toCache: Set<S>

        synchronized(this) {
            toCache = map.keys - coordCopy.keys
            this.map.putAll(coordCopy)
            _active = ConcurrentHashMap(coordCopy.keys.map { it to it }.toMap())
            inactive.removeAll(coordCopy.keys)
            inactive.addAll(toCache)
            checkCache()
        }
        fireCoordinatesChanged(CoordinateChangeEvent(this, coordCopy, toCache))
    }

    /** Remove objects, forgetting locations. */
    fun forget(obj: Set<S>) {
        val removed = mutableSetOf<S>()
        synchronized(map) {
            obj.filter { map.remove(it) != null }.forEach { removed.add(it) }
        }
        fireCoordinatesChanged(CoordinateChangeEvent(this, removed = removed))
    }

    /**
     * Makes specified objects inactive, possibly removing them from memory.
     * Propagates the updated coordinates to interested listeners (on the invoking thread).
     */
    fun deactivate(obj: Set<S>) {
        var removed = setOf<S>()
        synchronized(this) {
            removed = obj.intersect(active)
            active.removeAll(removed)
            inactive.addAll(removed)
            checkCache()
        }
        fireCoordinatesChanged(CoordinateChangeEvent(this, removed = removed))
    }

    /**
     * Restore locations from the cache and make the given objects active again.
     */
    fun reactivate(obj: Set<S>): Boolean {
        val restoreMap = mutableMapOf<S, C>()
        synchronized(this) {
            val restored = obj.intersect(inactive)
            restored.forEach { restoreMap[it] = map[it]!! }
            active.addAll(restored)
            inactive.removeAll(restored)
        }
        fireCoordinatesChanged(CoordinateChangeEvent(this, added = restoreMap))
        return restoreMap.isNotEmpty()
    }

    /** Call to ensure appropriate size of cache. Should always be called within a synchronization block. */
    private fun checkCache() {
        val n = inactive.size - maxCacheSize
        if (n > 0) {
            val remove = inactive.take(n)
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
     */
    @InvokedFromThread("unknown")
    protected fun fireCoordinatesChanged(evt: CoordinateChangeEvent<S, C>) {
        val added = evt.added
        val removed = evt.removed
        if ((added == null || added.isEmpty()) && (removed == null || removed.isEmpty())) {
            return
        }
        listeners.forEach { it.coordinatesChanged(evt) }
    }

    fun addCoordinateListener(cl: CoordinateListener<S, C>) = listeners.add(cl)
    fun removeCoordinateListener(cl: CoordinateListener<S, C>) = listeners.remove(cl)

    //endregion

}