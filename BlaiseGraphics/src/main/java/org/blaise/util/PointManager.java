/*
 * PointManager.java
 * Created Oct 5, 2011
 */
package org.blaise.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

/**
 * <p>
 * Used to track the locations of a collection of objects in a thread-safe manner.
 * Maintains a cache of prior locations, so that if some of the objects are removed,
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
public class PointManager<Src, Coord> implements Delegator<Src, Coord> {

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
     * Returns copy of map with object locations
     * @return object locations
     */
    public synchronized Map<Src, Coord> getLocationMap() {
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
    public synchronized void add(Src s, Coord c) {
        add(Collections.singletonMap(s, c));
    }

    /**
     * Updates a single location in the manager
     * @param s source object
     * @param c coordinate
     */
    public synchronized void update(Src s, Coord c) {
        add(s, c);
    }

    /**
     * Adds additional locations to the manager
     * @param coords new coordinates
     */
    public synchronized void add(Map<Src, Coord> coords) {
        Map<Src,Coord> changed = new HashMap<Src,Coord>();
        for (Src k : coords.keySet()) {
            Coord c = coords.get(k);
            if (!c.equals(map.get(k))) {
                changed.put(k, c);
            }
        }
        if (!changed.isEmpty()) {
            map.putAll(changed);
            fireNodeUpdate("add", changed);
        }
    }

    /**
     * Updates existing locations in the manager
     * @param coords new coordinates
     */
    public synchronized void update(Map<Src, Coord> coords) {
        add(coords);
    }

    /**
     * Replaces the current set of objects with specified objects, and caches the rest.
     * @param coords new coordinates
     */
    public synchronized void replace(Map<Src, Coord> coords) {
        Map<Src, Coord> cached = new HashMap<Src, Coord>();
        for (Src s : map.keySet()) {
            if (!coords.containsKey(s)) {
                cached.put(s, map.get(s));
            }
        }
        for (Src s : cached.keySet()) {
            map.remove(s);
        }
        if (!cached.isEmpty()) {
            cache.putAll(cached);
            // TODO - check cache size
            fireNodesRemoved(cached.keySet());
        }
        add(coords);
    }

    /**
     * Removes specified objects to cache
     * @param obj objects to remove
     */
    public synchronized void cache(Set<Src> obj) {
        Map<Src,Coord> cached = new HashMap<Src,Coord>();
        for (Src k : obj) {
            if (map.containsKey(k)) {
                cached.put(k, map.remove(k));
            }
        }
        if (!cached.isEmpty()) {
            cache.putAll(cached);
            // TODO - check cache size
            fireNodesRemoved(cached.keySet());
        }
    }

    /**
     * Removes objects from the manager without caching their locations
     * @param obj objects to remove
     */
    public synchronized void remove(Set<Src> obj) {
        Set<Src> removed = new HashSet<Src>();
        for (Src k : obj) {
            if (map.remove(k) != null) {
                removed.add(k);
            }
        }
        if (!removed.isEmpty()) {
            fireNodesRemoved(removed);
        }
    }

    /**
     * Notify listeners that the specified coordinates have changed.
     * @param key used as property in generated event
     * @param changed new coords
     */
    private void fireNodeUpdate(String key, Map<Src, Coord> changed) {
        pcs.firePropertyChange(key, null, changed);
    }

    /**
     * Notify listeners that the specified coordinates have been removed.
     * @param obj objects whose coords were removed
     */
    private void fireNodesRemoved(Set<Src> obj) {
        pcs.firePropertyChange("remove", null, obj);
    }


    //<editor-fold defaultstate="collapsed" desc="PROPERTY CHANGE LISTENING">
    //
    // PROPERTY CHANGE LISTENING
    //

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    //</editor-fold>

}
