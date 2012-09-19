/*
 * CoordinateManager.java
 * Created Oct 5, 2011
 */
package org.blaise.util;

import java.util.*;

/**
 * <p>
 * Used to track the locations of a collection of objects in a thread-safe manner.
 * Maintains a cacheObjects of prior locations, so that if some of the objects are removed,
 * this class "remembers" their prior locations. Listeners may register to be notified
 * when any of the coordinates within the manager change, or when any objects are
 * added to or removed from the manager.
 * </p>
 * <p>
 * Three constructors are provided, both of which require parameters ensuring that
 * objects have initial positions.
 * </p>
 *
 * @param <Src> type of source object
 * @param <Coord> type of point
 *
 * @author Elisha Peterson
 */
public class CoordinateManager<Src, Coord> implements Delegator<Src, Coord> {

    /** Map with current objects and locations (stores the data) */
    private final Map<Src, Coord> map = new HashMap<Src, Coord>();
    /** Cached locations */
    private final Map<Src, Coord> cache = new HashMap<Src, Coord>();

    public synchronized Coord of(Src src) {
        return map.get(src);
    }

    //
    // ACCESSORS
    //

    /**
     * Return objects currently tracked by the manager
     * @return objects
     */
    public synchronized Set<Src> getObjects() {
        return map.keySet();
    }

    /**
     * Returns cached objects
     * @return cached objects
     */
    public synchronized Set<Src> getCachedObjects() {
        return cache.keySet();
    }

    /**
     * Tests to see if all provided items are contained in either current
     * locations or cached locations.
     * @param obj objects to test
     * @return true if all are tracked, false otherwise
     */
    public synchronized boolean locatesAll(Collection<? extends Src> obj) {
        for (Src s : obj) {
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
    public synchronized Map<Src, Coord> getCoordinates() {
        return new HashMap<Src, Coord>(map);
    }

    //
    // MUTATORS
    //

    /**
     * Adds a single additional location to the manager
     * @param s source object
     * @param c coordinate
     */
    public synchronized void put(Src s, Coord c) {
        putAll(Collections.singletonMap(s, c));
    }

    /**
     * Adds additional locations to the manager. Sends out a property change event
     * to notify listeners if there are updates.
     * @param coords new coordinates
     */
    public synchronized void putAll(Map<Src, ? extends Coord> coords) {
        map.putAll(coords);
        fireCoordinatesChanged(CoordinateChangeEvent.createAddEvent(this, coords));
    }

    /**
     * Replaces the current set of objects with specified objects, and caches the rest.
     * @param coords new coordinates
     */
    public synchronized void setCoordinateMap(Map<Src, ? extends Coord> coords) {
        Map<Src, Coord> cached = new HashMap<Src, Coord>();
        for (Src s : map.keySet()) {
            if (!coords.containsKey(s)) {
                cached.put(s, map.get(s));
            }
        }
        for (Src s : cached.keySet()) {
            map.remove(s);
        }
        cache.putAll(cached);
        checkCache();
        map.putAll(coords);
        fireCoordinatesChanged(CoordinateChangeEvent.createAddRemoveEvent(this, coords, cached.keySet()));
    }

    /**
     * Removes objects from the manager without caching their locations
     * @param obj objects to remove
     */
    public synchronized void removeObjects(Set<? extends Src> obj) {
        Set<Src> removed = new HashSet<Src>();
        for (Src k : obj) {
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
    public synchronized void cacheObjects(Set<? extends Src> obj) {
        Map<Src,Coord> cached = new HashMap<Src,Coord>();
        for (Src k : obj) {
            if (map.containsKey(k)) {
                cached.put(k, map.remove(k));
            }
        }
        cache.putAll(cached);
        checkCache();
        fireCoordinatesChanged(CoordinateChangeEvent.createRemoveEvent(this, cached.keySet()));
    }

    /**
     * Call to restore locations from the cache.
     * @param obj objects to restore
     */
    public synchronized void restoreCached(Set<? extends Src> obj) {
        Map<Src,Coord> restored = new HashMap<Src,Coord>();
        for (Src k : obj) {
            Coord c = cache.remove(k);
            if (c != null && !map.containsKey(k)) {
                restored.put(k, c);
            }
        }
        putAll(restored);
    }

    /** Call to ensure appropriate size of cache */
    private void checkCache() {
        for (Object m : map.keySet()) {
            cache.remove(m);
        }
    }


    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLING">
    //
    // EVENT HANDLING
    //

    private final List<CoordinateListener> listeners = new ArrayList<CoordinateListener>();

    protected final void fireCoordinatesChanged(CoordinateChangeEvent evt) {
        for (CoordinateListener cl : listeners) {
            cl.coordinatesChanged(evt);
        }
    }

    public final synchronized void addCoordinateListener(CoordinateListener cl) {
        listeners.add(cl);
    }

    public final synchronized void removeCoordinateListener(CoordinateListener cl) {
        listeners.remove(cl);
    }

    //</editor-fold>

}
