/*
 * PointManager.java
 * Created Oct 5, 2011
 */
package org.blaise.util;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * <p>
 * Used to track the locations of a collection of objects in a thread-safe manner.
 * Maintains a cache of prior locations, so that if some of the objects are removed,
 * this class "remembers" their prior locations.
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
public class PointManager<Src, Coord> implements Delegator<Src, Coord>, IndexedPointBean<Coord> {
        
    /** Provides default locations */
    private Delegator<Src, Coord> initialPointer;
    
    /** Map with current objects and locations (stores the data) */
    private final Map<Src, Coord> map = Collections.synchronizedMap(new HashMap<Src, Coord>());
    
    /** Array of current objects */
    private transient Src[] objects;
    /** Array of current locations */
    private transient Coord[] locs;
    /** Indices of objects in array */
    private transient Map<Src,Integer> index;
    
    /** Cached locations */
    private final Map<Src, Coord> cache = Collections.synchronizedMap(new HashMap<Src, Coord>());
    
    /**
     * Constructs instance without an initial position object.
     * This object MUST be set for this manager to properly handle new points.
     */
    public PointManager() {
        this(Collections.EMPTY_MAP);
    }
    
    /** 
     * Constructs an instance of the point manager, given an object describing initial positions
     * @param initialPointer the initial point locator
     */
    public PointManager(Delegator<Src, Coord> initialPointer) {
        this.initialPointer = initialPointer;
    }
    
    /**
     * Constructs an instance of the point manager, given a list of objects and an array of coordinates
     * @param objects the source objects
     * @param coords the object locations
     */
    public PointManager(Src[] objects, Coord[] coords) {
        for (int i = 0; i < objects.length; i++) {
            map.put(objects[i], coords[i]);
        }
        update();
    }
    
    /**
     * Construct an instance of the point manager given a coordinate map
     * @param map map assigning coordinates to objects
     */
    public PointManager(Map<Src,Coord> map) {
        map.putAll(map);
        update();
    }
    
    /** Update point positions, object array, location array, and index. */
    private synchronized void update() {
        int n = map.size();
        if (n == 0) {
            objects = (Src[]) new Object[0];
            locs = (Coord[]) new Object[0];
            index = new HashMap<Src,Integer>();
        } else {
            Entry<Src,Coord> en = map.entrySet().iterator().next();
            if (objects == null || objects.length != n) {
                objects = (Src[]) Array.newInstance(en.getKey().getClass(), n);
            } if (locs == null || locs.length != n) {
                locs = (Coord[]) Array.newInstance(en.getValue().getClass(), n);
            }
            index = new HashMap<Src,Integer>();
            int i = 0;
            for (Src s : map.keySet()) {
                objects[i] = s;
                locs[i] = map.get(s);
                if (locs[i] == null && initialPointer != null) {
                    map.put(s, locs[i] = initialPointer.of(s));
                }
                index.put(s, i);
                i++;
            }
        }
        pcs.firePropertyChange("locationMap", null, getLocationMap());
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    /**
     * Return object that generates initial points
     * @return pointer
     */
    public Delegator<Src, Coord> getInitialPointDelegate() {
        return initialPointer;
    }

    /**
     * Sets object that generates initial points
     * @param initialPointer 
     */
    public synchronized void setInitialPointDelegate(Delegator<Src, Coord> initialPointer) {
        this.initialPointer = initialPointer;
        update();
    }
    
    /**
     * Returns unordered object set
     * @return object list
     */
    public synchronized Set<Src> getObjects() {
        return map.keySet();
    }
    
    /**
     * Return ordered list of objects
     * @return ordered objects
     */
    public synchronized Src[] getObjectArray() {
        return objects;
    }
    
    /** 
     * Sets the objects being handled. Anything in locationMap that is not in
     * the provided list is returned to the cache.
     * @param obj new list of objects
     */
    public synchronized void setObjects(Set<? extends Src> obj) {
        Set<Src> curObjects = map.keySet();
        if (curObjects == null) {
            curObjects = Collections.EMPTY_SET;
        }
        if (obj == null) {
            obj = Collections.EMPTY_SET;
        }
        
        // easy option when the set of objects did not change
        if (curObjects.containsAll(obj) && obj.containsAll(curObjects)) {
            return;
        } 

        // store objects that we need to cache
        Set<Src> toCache = new HashSet<Src>();
        for (Src o : curObjects) {
            if (!obj.contains(o)) {
                toCache.add(o);
            }
        }
        for (Src o : toCache) {
            cache.put(o, map.remove(o));
        }
        resizeCache();

        // find new objects and add them to cs
        // use cached locationMap if possible; otherwise, add near the origin
        for (Src o : obj) {
            if (!curObjects.contains(o)) {
                map.put(o, cache.containsKey(o) ? cache.get(o)
                        : initialPointer.of(o));
            }
        }

        // update node list
        update();
    }
    
    /**
     * Returns location containing positions of source objects.
     */
    public synchronized Map<Src, Coord> getLocationMap() {
        return map;
    }
    
    /** 
     * Updates positions of specified nodes (adds all to current position map).
     * The key set here does not necessarily correspond to the "active" nodes,
     * but will update those provided.
     */
    public synchronized void setLocationMap(Map<Src, Coord> pos) {
        map.putAll(pos);
        update();
    }
    
    /** 
     * Return location array for specified nodes 
     * @return locationMap
     */
    public synchronized Coord[] getLocationArray() { 
        return locs; 
    }

    /**
     * Update all locations via an array that matches current ordering.
     * @param pos 
     */
    public synchronized void setLocationArray(Coord[] pos) {
        for (int i = 0; i < Math.min(pos.length, objects.length); i++) {
            map.put(objects[i], pos[i]);
        }
        update();
    }
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="IndexedPointBean & Delegator METHODS">
    //
    // IndexedPointBean & Delegator METHODS
    //

    public synchronized Coord of(Src src) {
        return map.get(src);
    }

    public synchronized Coord getPoint(int i) {
        return locs[i];
    }

    public synchronized void setPoint(int i, Coord p) {
        map.put(objects[i], p);
        update();
    }

    public synchronized int getPointCount() {
        return map.size();
    }

    /**
     * Returns the index of the point corresponding to the specified window point and drag location.
     * By default, this method returns -1. Subclasses that use {@code PointManager} for
     * dragging should override this method.
     */
    public int indexOf(Point2D point, Coord dragPoint) {
        return -1;
    }
    
    //</editor-fold>
    
    
    /** Maximum cache size */
    private static final int MAX_CACHE = 5000;
    
    /** Ensure the cache doesn't get too big */
    private void resizeCache() {
        int nRemove = cache.size() - MAX_CACHE;
        if (nRemove > 0) {
            List<Src> toRemove = new ArrayList<Src>();
            for (Src o : cache.keySet()) {
                toRemove.add(o);
                if (toRemove.size() >= nRemove)
                    break;
            }
            System.err.println("Removing " + nRemove + " elements from node location cache: " + toRemove);
            for (Src o : toRemove)
                cache.remove(o);
            System.err.println("... new size: " + cache.size());
        }
    }
 
    // PROPERTY CHANGE LISTENING
    
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
    
    
    
    
    
}
