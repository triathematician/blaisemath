/*
 * PointManager.java
 * Created Oct 5, 2011
 */
package org.bm.util;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    private Delegator<Src, Coord> initialPointer = null;
    
    /** List of objects */
    private final List<Src> objects = Collections.synchronizedList(new ArrayList<Src>());
    /** Map with current locations */
    private final Map<Src, Coord> locationMap = Collections.synchronizedMap(new HashMap<Src, Coord>());
    /** Array of current locations, with indices matching those of the objects */
    private transient Coord[] locationArray;
    
    /** Cached locationMap */
    final Map<Src, Coord> cache = Collections.synchronizedMap(new HashMap<Src, Coord>());
    
    /** 
     * Constructs an instance of the point manager, given an object describing initial positions
     * @param pointer the initial point locator
     */
    public PointManager(Delegator<Src, Coord> initialPointer) {
        this.initialPointer = initialPointer;
    }
    
    /**
     * Constructs an instance of the point manager, given a list of objects and an array of coordinates
     * @param objects the source objects
     * @param coords the object locations
     */
    public PointManager(List<Src> objects, Coord[] coords) {
        this.objects.addAll(objects);
        this.locationArray = coords;
    }
    
    /**
     * Construct an instance of the point manager given a coordinate map
     * @param map map assigning coordinates to objects
     */
    public PointManager(Map<Src,Coord> map) {
        locationMap.putAll(map);
    }
    
    //
    // INTERFACE METHODS
    //

    public synchronized Coord getPoint(int i) {
        return locationArray[i];
    }

    public synchronized void setPoint(int i, Coord p) {
        locationArray[i] = p;
        locationMap.put(objects.get(i), p);
    }

    public synchronized int getPointCount() {
        return objects.size();
    }

    public synchronized Coord of(Src src) {
        return locationMap.get(src);
    }

    /**
     * Returns the index of the point corresponding to the specified window point and drag location.
     * By default, this method returns -1. Subclasses that use {@code PointManager} for
     * dragging should override this method.
     */
    public int indexOf(Point2D point, Coord dragPoint) {
        return -1;
    }
    
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
        locationMap.clear();
        setObjects(new ArrayList(objects));
    }
    
    
    /**
     * Returns list of objects
     * @return object list
     */
    public synchronized List<? extends Src> getObjects() {
        return objects;
    }
    
    /** 
     * Sets the objects being handled. Anything in locationMap that is not in
     * the provided list is returned to the cache.
     * @param obj new list of objects
     */
    public synchronized void setObjects(List<? extends Src> obj) {
        Set<Src> curObjects = locationMap.keySet();
        
        // easy option when the set of objects did not change... just update the order
        if (curObjects.containsAll(obj) && obj.containsAll(curObjects)) {
            objects.clear();
            objects.addAll(obj);
            updateArray(locationArray == null || locationArray.length != obj.size());
            return;
        }

        // store objects that we need to cache
        Set<Src> toCache = new HashSet<Src>();
        for (Src o : curObjects)
            if (!obj.contains(o))
                toCache.add(o);
        for (Src o : toCache)
            cache.put(o, locationMap.remove(o));
        resizeCache();

        // find new objects and add them to cs
        // use cached locationMap if possible; otherwise, add near the origin
        for (Src o : obj)
            if (!curObjects.contains(o))
                locationMap.put(o, cache.containsKey(o) ? cache.get(o)
                        : initialPointer.of(o));

        // update node list
        objects.clear();
        objects.addAll(obj);
        updateArray(true);
    }
    
    /**
     * Returns location containing positions of source objects.
     */
    public synchronized Map<Src, Coord> getLocationMap() {
        return locationMap;
    }
    
    /** 
     * Updates positions of specified nodes (adds all to current position map).
     * The key set here does not necessarily correspond to the "active" nodes,
     * but will update
     */
    public synchronized void setLocationMap(Map<Src, Coord> pos) {
        locationMap.putAll(pos);
        updateArray(false);
    }
    
    /** 
     * Return location array for specified nodes 
     * @return locationMap
     */
    public synchronized Coord[] getLocationArray() { 
        return locationArray; 
    }

    public synchronized void setLocationArray(Coord[] pos) {
        for (int i = 0; i < Math.min(pos.length, objects.size()); i++)
            locationMap.put(objects.get(i), pos[i]);
        updateArray(false);
    }
    
    

    /** Updates the array of locationMap */
    private synchronized void updateArray(boolean nodeUpdate) {
        if (nodeUpdate)
            locationArray = (Coord[]) new Object[objects.size()];
        for (int i = 0; i < locationArray.length; i++)
            locationArray[i] = locationMap.get(objects.get(i));
    }

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
}
