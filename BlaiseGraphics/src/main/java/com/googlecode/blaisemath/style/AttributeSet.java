/**
 * AttributeSet.java
 * Created Jul 31, 2014
 */
package com.googlecode.blaisemath.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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


import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * Provides a map of key-value pairs providing style elements, similar to what
 * one finds in CSS style attributes.
 * 
 * @author Elisha
 */
public class AttributeSet {
    
    private static final Logger LOG = Logger.getLogger(AttributeSet.class.getName());
    
    /** Constant representing the empty attribute set */
    public static final AttributeSet EMPTY = ImmutableAttributeSet.copyOf(new AttributeSet());
    
    /** The parent attribute set */
    protected Optional<AttributeSet> parent = Optional.absent();
    /** The map of style key/value pairs */
    protected final Map<String,Object> attributeMap = Maps.newHashMap();
    
    private final transient ChangeEvent changeEvent = new ChangeEvent(this);
    private final transient EventListenerList listenerList = new EventListenerList();

    @Override
    public String toString() {
        return "AttributeSet " + attributeMap;
    }

    //<editor-fold defaultstate="collapsed" desc="FACTORY METHODS">
    
    /**
     * Create new attribute set with elements of given map.
     * @param map key-value map
     * @return new attribute set
     */
    public static AttributeSet create(Map<String, ?> map) {
        AttributeSet res = new AttributeSet();
        res.putAll(map);
        return res;
    }
    
    /** 
     * Create new attribute set with given parent.
     * @param parent the parent
     * @return new attribute set
     */
    public static AttributeSet createWithParent(@Nullable AttributeSet parent) {
        AttributeSet res = new AttributeSet();
        res.parent = Optional.fromNullable(parent);
        return res;
    }
    
    /** 
     * Create copy of attribute set, with all values copies as well.
     * @param set to copy
     * @return copy
     */
    public static AttributeSet copyOf(AttributeSet set) {
        AttributeSet res = createWithParent(set.getParent());
        for (String k : set.getAttributeMap().keySet()) {
            res.put(k, copyValue(set.get(k)));
        }
        return res;
    }
    
    /** 
     * Create flat copy of attribute set, with all values copies as well.
     * The resulting set has no parent attribute set.
     * @param set to copy
     * @return copy
     */
    public static AttributeSet flatCopyOf(AttributeSet set) {
        AttributeSet res = new AttributeSet();
        for (String k : set.getAllAttributes()) {
            res.put(k, copyValue(set.get(k)));
        }
        return res;
    }

    /**
     * Create a partial copy of the attribute set, with only those values matching
     * the given keys.
     * @param sty style to copy from
     * @param keys keys to copy
     * @return copied style
     */
    public static AttributeSet copy(AttributeSet sty, String... keys) {
        AttributeSet res = new AttributeSet();
        for (String k : keys) {
            if (sty.contains(k)) {
                res.put(k, sty.get(k));
            }
        }
        return res;
    }
    
    /**
     * Copies a value in an attribute set, returning a new instance if the value
     * is not an immutable object.
     * @param <P> value type
     * @param val value to copy
     * @return new value instance
     */
    private static <P> P copyValue(P val) {
        if (val instanceof Point2D) {
            return (P) ((Point2D)val).clone();
        } else {
            return val;
        }
    }
    
    /**
     * Generate attribute set with given key/value pair
     * @param k1 the key
     * @param v1 the value
     * @return created set
     */
    public static AttributeSet of(String k1, Object v1) {
        return create(ImmutableMap.of(k1, v1));
    }
    
    /**
     * Generate attribute set with given key/value pairs.
     * @param k1 first key
     * @param v1 first value
     * @param k2 second key
     * @param v2 second value
     * @return created set
     */
    public static AttributeSet of(String k1, Object v1, String k2, Object v2) {
        return create(ImmutableMap.of(k1, v1, k2, v2));
    }
    
    /**
     * Generate attribute set with given key/value pairs.
     * @param k1 first key
     * @param v1 first value
     * @param k2 second key
     * @param v2 second value
     * @param k3 third key
     * @param v3 third value
     * @return created set
     */
    public static AttributeSet of(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        return create(ImmutableMap.of(k1, v1, k2, v2, k3, v3));
    }
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="BUILDER METHODS">
    
    /**
     * Builder pattern for setting a key/value pair
     * @param key the key
     * @param val the value
     * @return this object
     */
    public AttributeSet and(String key, Object val) {
        put(key, val);
        return this;
    }

    /**
     * Copies the attribute set as an unmodifiable object, which will throw errors
     * if any of its get/put methods are accessed.
     * @return immutable set with all the attributes of this one
     */
    public AttributeSet immutable() {
        return ImmutableAttributeSet.immutableCopyOf(this);
    }

    /**
     * Copies the attribute set as an unmodifiable object, which will throw errors
     * if any of its get/put methods are accessed.
     * @param parent parent to use for copy
     * @return immutable set with all the attributes of this one
     */
    public AttributeSet immutableWithParent(AttributeSet par) {
        return ImmutableAttributeSet.immutableCopyOf(this, par);
    }

    /**
     * Creates a copy of the attribute set.
     * @return copy
     */
    public AttributeSet copy() {
        return copyOf(this);
    }

    /**
     * Creates a copy of the attribute set, including all parent attributes
     * @return copy
     */
    public AttributeSet flatCopy() {
        return flatCopyOf(this);
    }
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
    @Nullable
    public AttributeSet getParent() {
        return parent.orNull();
    }
    
    //</editor-fold>
    

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
     * Get the attributes and the values in this set as a map
     * @return attribute map
     */
    public Map<String,Object> getAttributeMap() {
        return Maps.newHashMap(attributeMap);
    }
    
    /**
     * Get this attributes, and all parent attributes.
     * @return attribute keys
     */
    public Set<String> getAllAttributes() {
        if (parent.isPresent()) {
            return Sets.union(attributeMap.keySet(), parent.get().getAllAttributes());
        } else {
            return getAttributes();
        }
    }

    /**
     * Return attributes of the given type, whether in this set or the parent set.
     * @param type attribute type
     * @return attribute keys
     */
    public Set<String> getAllAttributes(Class type) {
        Map<String, Object> filtered = Maps.filterValues(attributeMap, Predicates.instanceOf(type));
        if (parent.isPresent()) {
            return Sets.union(filtered.keySet(), parent.get().getAllAttributes(type));
        } else {
            return filtered.keySet();
        }
    }
    
    /**
     * Return true if this set or its ancestors contain the given key.
     * @param key attribute key
     * @return true if attribute is accessible from this set
     */
    public boolean contains(String key) {
        return attributeMap.containsKey(key)
                || (parent.isPresent() && parent.get().contains(key));
    }
    
    /**
     * Get the given attribute.
     * @param key the key
     * @return value of the found attribute, either contained in this set or its parent,
     *      or null if there is none
     */
    @Nullable
    public Object get(String key) {
        if (attributeMap.containsKey(key)) {
            return attributeMap.get(key);
        } else if (parent.isPresent()) {
            return parent.get().get(key);
        } else {
            return null;
        }
    }

    /**
     * Get the given attribute.
     * @param key the key
     * @param value the attribute value (may be null)
     * @return the old value
     */
    @Nullable
    public Object put(String key, @Nullable Object value) {
        Object res = attributeMap.put(key, value);
        fireStateChanged();
        return res;
    }
    
    /**
     * Set all of the attributes in the provided map.
     * @param attr map of attributes to set
     * @param <T> the type of values in the map
     */
    public <T> void putAll(Map<String,T> attr) {
        attributeMap.putAll(attr);
        fireStateChanged();
    }
    
    /**
     * Remove attribute with the given key.
     * @param key the key
     * @return the removed value, null if none
     */
    public Object remove(String key) {
        Object res = attributeMap.remove(key);
        fireStateChanged();
        return res;
    }
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="TYPED ACCESSORS">
    
    @Nullable
    private <C> C getTyped(String key, Class<C> cls, @Nullable C def) {
        try {
            return cls.cast(contains(key) ? (C) get(key) : def);
        } catch (ClassCastException x) {
            LOG.log(Level.WARNING, "Cast from "+get(key)+" to "+cls+" failed.", x);
            throw x;
        }
    }

    public String getString(String key) {
        return getTyped(key, String.class, null);
    }

    public String getString(String key, String def) {
        return getTyped(key, String.class, def);
    }
    
    /**
     * Retrieve given attribute as a color.
     * @param key attribute key
     * @return color, or null if not present
     * @throws ClassCastException if attribute is present but not a color
     */
    @Nullable
    public Color getColor(String key) {
        return getTyped(key, Color.class, null);
    }
    
    @Nullable
    public Color getColor(String key, @Nullable Color def) {
        return getTyped(key, Color.class, def);
    }
    
    @Nullable
    public Point2D getPoint(String key) {
        return getTyped(key, Point2D.class, null);
    }
    
    @Nullable
    public Point2D getPoint(String key, @Nullable Point2D def) {
        return getTyped(key, Point2D.class, def);
    }

    /**
     * Get the boolean value associated with the key
     * @param key key
     * @return boolean value, or null if there is none
     */
    @Nullable
    public Boolean getBoolean(String key) {
        return getTyped(key, Boolean.class, null);
    }


    /**
     * Get the boolean value associated with the key.
     * @param key key
     * @param def default value
     * @return boolean value, or def if there is none
     */
    @Nullable
    public Boolean getBoolean(String key, @Nullable Boolean def) {
        try {
            return getTyped(key, Boolean.class, def);
        } catch (ClassCastException x) {
            return get(key) instanceof String ? Boolean.valueOf((String) get(key)) : false;
        }
    }

    /**
     * Retrieve given attribute as a float.
     * @param key attribute key
     * @return float, or null if not present
     * @throws ClassCastException if attribute is present but not a float
     */
    @Nullable
    public Float getFloat(String key) {
        return getFloat(key, null);
    }

    @Nullable
    public Float getFloat(String key, @Nullable Float def) {
        if (contains(key)) {
            Number n = (Number) get(key);
            return n == null ? null
                    : n instanceof Float ? (Float) n
                    : n.floatValue();
        } else {
            return def;
        }
    }
    
    @Nullable
    public Integer getInteger(String key) {
        return getInteger(key, null);
    }

    @Nullable
    public Integer getInteger(String key, @Nullable Integer def) {
        if (contains(key)) {
            Number n = (Number) get(key);
            return n == null ? null
                    : n instanceof Integer ? (Integer) n
                    : n.intValue();
        } else {
            return def;
        }
    }
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLING">
    //
    // EVENT HANDLING
    //

    public void addChangeListener(ChangeListener l) { 
        listenerList.add(ChangeListener.class, l);
    }
    
    public void removeChangeListener(ChangeListener l) { 
        listenerList.remove(ChangeListener.class, l); 
    }

    /** Notify interested listeners of an (unspecified) change in the plottable. */
    public void fireStateChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null) {
                    return;
                }
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
        }
    }
    
    //</editor-fold>
    
}
