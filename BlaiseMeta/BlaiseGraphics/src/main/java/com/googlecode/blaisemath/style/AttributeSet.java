/**
 * StyleAttributes.java
 * Created Jul 31, 2014
 */
package com.googlecode.blaisemath.style;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Provides a map of key-value pairs providing style elements, similar to what
 * one finds in CSS style attributes.
 * 
 * @author Elisha
 */
public final class AttributeSet {
    
    /** The parent attribute set */
    @Nullable
    private final AttributeSet parent;
    /** The map of style key/value pairs */
    private final Map<String,Object> attributeMap = Maps.newHashMap();

    public AttributeSet() {
        this(null);
    }
    
    public AttributeSet(AttributeSet parent) {
        this.parent = parent;
    }

    //<editor-fold defaultstate="collapsed" desc="GENERIC ATTRIBUTE METHODS">
    //
    // GENERIC ATTRIBUTE METHODS
    //
    
    /**
     * Get the set of attributes known in this set.
     * @return attribute keys
     */
    public Set<String> getAttributes() {
        return attributeMap.keySet();
    }
    
    /**
     * Get this attributes, and all parent attributes.
     * @return attribute keys
     */
    public Set<String> getAllAttributes() {
        if (parent == null) {
            return getAttributes();
        } else {
            return Sets.union(attributeMap.keySet(), parent.getAllAttributes());
        }
    }
    
    /**
     * Get the given attribute.
     * @param key the key
     * @return value of the found attribute, either contained in this set or its parent,
     *      or null if there is none
     */
    public Object get(String key) {
        if (attributeMap.containsKey(key)) {
            return attributeMap.get(key);
        } else if (parent != null) {
            return parent.get(key);
        } else {
            return null;
        }
    }

    /**
     * Get the given attribute.
     * @param key the key
     * @param value the attribute value
     * @return the old value
     */
    public Object put(String key, Object value) {
        return attributeMap.put(key, value);
    }
    
    /**
     * Remove attribute with the given key.
     * @param key the key
     * @return the removed value, null if none
     */
    public Object remove(String key) {
        return attributeMap.remove(key);
    }
    
    //</editor-fold>
    
}
