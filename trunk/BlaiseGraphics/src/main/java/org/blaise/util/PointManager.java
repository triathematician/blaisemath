/*
 * PointManager.java
 * Created Oct 5, 2011
 */
package org.blaise.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
     * Removes specified objects to cache
     * @param obj objects to remove
     */
    public synchronized void cache(Set<Src> obj) {
        Map<Src,Coord> changed = new HashMap<Src,Coord>();
        for (Src k : obj) {
            if (map.containsKey(k)) {
                changed.put(k, map.remove(k));
            }
        }
        if (!changed.isEmpty()) {
            map.putAll(changed);
            fireNodeUpdate("cache", changed);
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











//    
//    
//    /**
//     * Constructs instance without an initial position object.
//     * This object MUST be set for this manager to properly handle new points.
//     */
//    public PointManager() {
//        this(Collections.EMPTY_MAP);
//    }
//    
//    /**
//     * Construct an instance of the point manager given a coordinate map
//     * @param map map assigning coordinates to objects
//     */
//    public PointManager(Map<Src,Coord> map) {
//        putAll(map);
//        update();
//    }
//    
//    /** Update point positions, object array, location array, and index. */
//    private synchronized void update() {
//        int n = map.size();
//        if (n == 0) {
//            objects = (Src[]) new Object[0];
//            locs = (Coord[]) new Object[0];
//            index = new HashMap<Src,Integer>();
//        } else {
//            Entry<Src,Coord> en = map.entrySet().iterator().next();
//            if (objects == null || objects.length != n) {
//                Class<? extends Object> keyCls = en.getKey().getClass();
//                if (keyCls == Point.class) {
//                    keyCls = Point2D.class;
//                }
//                objects = (Src[]) Array.newInstance(keyCls, n);
//            } if (locs == null || locs.length != n) {
//                Class<? extends Object> valCls = en.getValue().getClass();
//                if (valCls == Point.class) {
//                    valCls = Point2D.class;
//                }
//                locs = (Coord[]) Array.newInstance(valCls, n);
//            }
//            index = new HashMap<Src,Integer>();
//            int i = 0;
//            for (Src s : map.keySet()) {
//                objects[i] = s;
//                try {
//                    locs[i] = map.get(s);
//                } catch (ArrayStoreException x) {
//                    System.out.println("x");
//                    System.out.println(locs[i]+" = "+map.get(s));
//                }
//                if (locs[i] == null && initialPointer != null) {
//                    map.put(s, locs[i] = initialPointer.of(s));
//                }
//                index.put(s, i);
//                i++;
//            }
//        }
//        pcs.firePropertyChange("locationMap", null, getLocationMap());
//    }
//    
//    
//    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
//    //
//    // PROPERTIES
//    //
//    
//    /**
//     * Returns unordered object set
//     * @return object list
//     */
//    public synchronized Set<Src> getObjects() {
//        return map.keySet();
//    }
//    
//    /** 
//     * Sets the objects being handled. Anything in locationMap that is not in
//     * the provided list is returned to the cache.
//     * @param obj new list of objects
//     */
//    public synchronized void setObjects(Set<? extends Src> obj) {
//        Set<Src> curObjects = map.keySet();
//        if (curObjects == null) {
//            curObjects = Collections.EMPTY_SET;
//        }
//        if (obj == null) {
//            obj = Collections.EMPTY_SET;
//        }
//        
//        // easy option when the set of objects did not change
//        if (curObjects.containsAll(obj) && obj.containsAll(curObjects)) {
//            return;
//        } 
//
//        // store objects that we need to cache
//        Set<Src> toCache = new HashSet<Src>();
//        for (Src o : curObjects) {
//            if (!obj.contains(o)) {
//                toCache.add(o);
//            }
//        }
//        for (Src o : toCache) {
//            cache.put(o, map.remove(o));
//        }
//        resizeCache();
//
//        // find new objects and add them to cs
//        // use cached locationMap if possible; otherwise, add near the origin
//        for (Src o : obj) {
//            if (!curObjects.contains(o)) {
//                map.put(o, cache.containsKey(o) ? cache.get(o)
//                        : initialPointer.of(o));
//            }
//        }
//
//        // update node list
//        update();
//    }
//    
//    /**
//     * Returns location containing positions of source objects.
//     */
//    public synchronized Map<Src, Coord> getLocationMap() {
//        return map;
//    }
//    
//    /** 
//     * Updates positions of specified nodes (adds all to current position map).
//     * The key set here does not necessarily correspond to the "active" nodes,
//     * but will update those provided.
//     * @param pos positions to update
//     */
//    public synchronized void setLocationMap(Map<Src, Coord> pos) {
//        map.putAll(pos);
//        update();
//    }
//    
//    //</editor-fold>
//    
//    
//    //<editor-fold defaultstate="collapsed" desc="IndexedPointBean & Delegator METHODS">
//    //
//    // IndexedPointBean & Delegator METHODS
//    //
//
//    public synchronized Coord of(Src src) {
//        return map.get(src);
//    }
//    
//    //</editor-fold>
//    
//    
//    /** Maximum cache size */
//    private static final int MAX_CACHE = 5000;
//    
//    /** Ensure the cache doesn't get too big */
//    private void resizeCache() {
//        int nRemove = cache.size() - MAX_CACHE;
//        if (nRemove > 0) {
//            List<Src> toRemove = new ArrayList<Src>();
//            for (Src o : cache.keySet()) {
//                toRemove.add(o);
//                if (toRemove.size() >= nRemove)
//                    break;
//            }
//            System.err.println("Removing " + nRemove + " elements from node location cache: " + toRemove);
//            for (Src o : toRemove)
//                cache.remove(o);
//            System.err.println("... new size: " + cache.size());
//        }
//    }
// 
//}
