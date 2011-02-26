/*
 * StyleMap.java
 * Created Jan 8, 2011
 */

package old.other;

import java.util.TreeMap;

/**
 * This class provides basic style key/object pairings with inheritance.
 * The interface is similar to <code>Map</code>.
 * @author elisha
 */
public class StyleMap {

    /** Parent style key (may be null) */
    StyleMap parent = null;
    /** Maintains key map */
    TreeMap<String,Object> custom = new TreeMap<String,Object>();

    /**
     * Construct style key via several key-value pairings
     * @param keyValues argument list: should have even length,
     *      and all even indices should be style key strings.
     * @throw IllegalArgumentException if even-indices are not strings, or any values in array are null
     */
    public StyleMap(Object... keyValues) { this(null, (Object[]) keyValues); }

    /**
     * Construct style key via several key-value pairings, and a parent key
     * @param parent the parent key
     * @param keyValues argument list: should have even length,
     *      and all even indices should be style key strings.
     * @throw IllegalArgumentException if even-indices are not strings, or any values in array are null
     */
    public StyleMap(StyleMap parent, Object... keyValues) {
        this.parent = parent;
        for (int i = 0; i < keyValues.length; i+=2) {
            if (keyValues[i] == null || keyValues[i+1] == null || !(keyValues[i] instanceof String))
                throw new IllegalArgumentException();
            custom.put((String) keyValues[i], keyValues[i+1]);
        }
    }

    /**
     * @param key the style key
     * @return true if specified key is supported, false otherwise
     */
    public boolean containsKey(String key) {
        return custom.containsKey(key) || (parent != null && parent.containsKey(key));
    }

    /**
     * Lookup a style key in this map, or in the parent map.
     * @param key the style key
     * @return the object corresponding to the specified key, or null if there is no such key
     */
    public Object get(String key) {
        return custom.containsKey(key) ? custom.get(key)
                : parent == null ? null
                : parent.get(key);
    }

    /**
     * Lookup a style key in this map if it's there; otherwise use provided default map.
     * @param key the style key
     * @param defer style map to defer to if this map does not contain the key
     * @return object corresponding to key, or null if there is none
     */
     public Object get(String key, StyleMap defer) {
         return containsKey(key) ? get(key) : defer.get(key);
     }

}
