package com.googlecode.blaisemath.style;

/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2020 Elisha Peterson
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

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Provides a collection of key-value pairs for style elements, similar to what
 * one finds in CSS style attributes. Values are allowed to be null.
 * 
 * @author Elisha Peterson
 */
public class AttributeSet {
    
    /** Constant representing the empty attribute set */
    public static final AttributeSet EMPTY = ImmutableAttributeSet.copyOf(new AttributeSet());
    
    /** The parent attribute set */
    protected @Nullable AttributeSet parent = null;
    /** The map of style key/value pairs. May contain null values. */
    protected final Map<String, Object> attributeMap = Maps.newHashMap();
    
    private final ChangeEvent changeEvent = new ChangeEvent(this);
    private final EventListenerList listenerList = new EventListenerList();

    @Override
    public String toString() {
        return "{ " + Joiner.on("; ").withKeyValueSeparator(":").useForNull("").join(attributeMap) + " }";
    }

    //region FACTORY METHODS
    
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
    public static AttributeSet withParent(@Nullable AttributeSet parent) {
        AttributeSet res = new AttributeSet();
        res.parent = parent;
        return res;
    }
    
    /** 
     * Create copy of attribute set, with all values copies as well.
     * @param set to copy
     * @return copy
     */
    public static AttributeSet copyOf(AttributeSet set) {
        AttributeSet res = withParent(set.getParent().orElse(null));
        set.getAttributeMap().forEach((k, v) -> res.put(k, copyValue(v)));
        return res;
    }
    
    /** 
     * Create flat copy of attribute set (including all parent attributes), with all values copies as well.
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
    @SuppressWarnings("unchecked")
    private static <P> P copyValue(P val) {
        if (val instanceof Point2D) {
            return (P) ((Point2D) val).clone();
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
    public static AttributeSet of(String k1, @Nullable Object v1) {
        return create(Collections.singletonMap(k1, v1));
    }
    
    /**
     * Generate attribute set with given key/value pairs.
     * @param k1 first key
     * @param v1 first value
     * @param k2 second key
     * @param v2 second value
     * @return created set
     */
    public static AttributeSet of(String k1, @Nullable Object v1, String k2, @Nullable Object v2) {
        return of(k1, v1).and(k2, v2);
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
    public static AttributeSet of(String k1, @Nullable Object v1, String k2, @Nullable Object v2, 
            String k3, @Nullable Object v3) {
        return of(k1, v1).and(k2, v2).and(k3, v3);
    }
    
    /**
     * Generate attribute set with given key/value pairs.
     * @param k1 first key
     * @param v1 first value
     * @param k2 second key
     * @param v2 second value
     * @param k3 third key
     * @param v3 third value
     * @param k4 fourth key
     * @param v4 fourth value
     * @return created set
     */
    public static AttributeSet of(String k1, @Nullable Object v1, String k2, @Nullable Object v2, 
            String k3, @Nullable Object v3, String k4, @Nullable Object v4) {
        return of(k1, v1).and(k2, v2).and(k3, v3).and(k4, v4);
    }
    
    /**
     * Generate attribute set with given key/value pairs.
     * @param k1 first key
     * @param v1 first value
     * @param k2 second key
     * @param v2 second value
     * @param k3 third key
     * @param v3 third value
     * @param k4 fourth key
     * @param v4 fourth value
     * @param k5 fifth key
     * @param v5 fifth value
     * @return created set
     */
    public static AttributeSet of(String k1, @Nullable Object v1, String k2, @Nullable Object v2, 
            String k3, @Nullable Object v3, String k4, @Nullable Object v4,
            String k5, @Nullable Object v5) {
        return of(k1, v1).and(k2, v2).and(k3, v3).and(k4, v4).and(k5, v5);
    }
    
    //endregion
    
    //region BUILDER METHODS
    
    /**
     * Builder pattern for setting a key/value pair.
     * @param key the key
     * @param val the value
     * @return this object
     */
    public AttributeSet and(String key, @Nullable Object val) {
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
     * @param par parent to use for copy
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
     * Creates a copy of the attribute set, including all parent attributes.
     * @return copy
     */
    public AttributeSet flatCopy() {
        return flatCopyOf(this);
    }
    
    //endregion

    //region ATTRIBUTE GETS
    
    public Optional<AttributeSet> getParent() {
        return Optional.ofNullable(parent);
    }
    
    /**
     * Get the set of attributes known in this set.
     * @return attribute keys
     */
    public Set<String> getAttributes() {
        return Sets.newHashSet(attributeMap.keySet());
    }
    
    /**
     * Get this attributes, and all parent attributes.
     * @return attribute keys
     */
    public Set<String> getAllAttributes() {
        if (parent != null) {
            return Sets.newHashSet(Sets.union(attributeMap.keySet(), parent.getAllAttributes()));
        } else {
            return Sets.newHashSet(getAttributes());
        }
    }
    
    /**
     * Gets a filtered set view of attributes.
     * @param filter attribute name filter
     * @return attribute keys
     */
    public Set<String> getAttributes(Predicate<String> filter) {
        return Sets.newHashSet(Sets.filter(getAllAttributes(), filter::test));
    }

    /**
     * Return attributes of the given type, whether in this set or the parent set.
     * @param type attribute type
     * @return attribute keys
     */
    public Set<String> getAllAttributes(Class<?> type) {
        Map<String, Object> filtered = Maps.filterValues(attributeMap, type::isInstance);
        if (parent != null) {
            return Sets.newHashSet(Sets.union(filtered.keySet(), parent.getAllAttributes(type)));
        } else {
            return Sets.newHashSet(filtered.keySet());
        }
    }
    
    /**
     * Get copy of the attributes and the values in this set as a map.
     * @return attribute map
     */
    public Map<String, Object> getAttributeMap() {
        return Maps.newHashMap(attributeMap);
    }
    
    /**
     * Return true if this set or its ancestors contain the given key.
     * @param key attribute key
     * @return true if attribute is accessible from this set
     */
    public boolean contains(String key) {
        return attributeMap.containsKey(key)
                || (parent != null && parent.contains(key));
    }
    
    /**
     * Get the given attribute. Return null if not found.
     * @param key the key
     * @return value of the found attribute, either contained in this set or its parent,
     *      or null if there is none
     */
    public @Nullable Object get(String key) {
        return getOrDefault(key, null);
    }
    
    /**
     * Get the given attribute, or return the given default value if not found.
     * Will return "null" if this class has an explicit entry with a null value
     * for the attribute.
     * @param key the key
     * @param def default value to return
     * @return value of the found attribute, either contained in this set or its parent,
     *      or the default value if there is none
     */
    public @Nullable Object getOrDefault(String key, @Nullable Object def) {
        if (attributeMap.containsKey(key)) {
            return attributeMap.get(key);
        } else if (parent != null) {
            return parent.getOrDefault(key, def);
        } else {
            return def;
        }
    }

    //endregion

    //region ATTRIBUTE MUTATORS

    /**
     * Add the given attribute to this attribute set, returning the old value.
     * @param key the key
     * @param value the attribute value (may be null)
     * @return the old value
     */
    public @Nullable Object put(String key, @Nullable Object value) {
        Object res = attributeMap.put(key, value);
        if (!Objects.equals(res, value)) {
            fireStateChanged();
        }
        return res;
    }

    /**
     * Adds a value, only if the key is not already present.
     * @param key the key
     * @param value the attribute value (may be null)
     */
    public void putIfAbsent(String key, @Nullable Object value) {
        if (!attributeMap.containsKey(key)) {
            put(key, value);
        }
    }
    
    /**
     * Set all of the attributes in the provided map.
     * @param attr map of attributes to set
     */
    public void putAll(Map<String, ?> attr) {
        Map<String, Object> old = getAttributeMap();
        attributeMap.putAll(attr);
        if (!attributeMap.equals(old)) {
            fireStateChanged();
        }
    }
    
    /**
     * Remove attribute with the given key.
     * @param key the key
     * @return the removed value, null if none
     */
    public @Nullable Object remove(String key) {
        if (attributeMap.containsKey(key)) {
            Object res = attributeMap.remove(key);
            fireStateChanged();
            return res;
        }
        return null;
    }
    
    //endregion
    
    //region TYPED ACCESSORS

    /**
     * Get the string value associated with the key
     * @param key key
     * @return string value, or null if there is none
     */
    public @Nullable String getString(String key) {
        return getString(key, null);
    }

    /**
     * Get the string value associated with the key.
     * @param key key
     * @param def default value
     * @return string value, or def if there is none
     */
    public @Nullable String getString(String key, String def) {
        return AttributeSetCoder.convertValue(get(key), String.class, def);
    }

    /**
     * Get the boolean value associated with the key
     * @param key key
     * @return boolean value, or null if there is none
     * @throws UnsupportedOperationException if attribute is present but not a boolean
     */
    public @Nullable Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    /**
     * Get the boolean value associated with the key.
     * @param key key
     * @param def default value
     * @return boolean value, or def if there is none
     * @throws UnsupportedOperationException if attribute is present but not a boolean
     */
    public @Nullable Boolean getBoolean(String key, @Nullable Boolean def) {
        return AttributeSetCoder.convertValue(get(key), Boolean.class, def);
    }
    
    /**
     * Retrieve given attribute as an integer.
     * @param key attribute key
     * @return integer, or null if not present
     * @throws UnsupportedOperationException if attribute is present but not a integer
     */
    public @Nullable Integer getInteger(String key) {
        return getInteger(key, null);
    }

    /**
     * Get the integer value associated with the key.
     * @param key key
     * @param def default value
     * @return integer value, or def if there is none
     * @throws UnsupportedOperationException if attribute is present but not an integer
     */
    public @Nullable Integer getInteger(String key, @Nullable Integer def) {
        return AttributeSetCoder.convertValue(get(key), Integer.class, def);
    }

    /**
     * Retrieve given attribute as a float.
     * @param key attribute key
     * @return float, or null if key is not present or value is not convertible to a float
     */
    public @Nullable Float getFloat(String key) {
        return getFloat(key, null);
    }

    /**
     * Get the float value associated with the key.
     * @param key key
     * @param def default value
     * @return float value, or def if key is not present or value is not convertible to a float
     */
    public @Nullable Float getFloat(String key, @Nullable Float def) {
        return AttributeSetCoder.convertValue(get(key), Float.class, def);
    }

    /**
     * Retrieve given attribute as a double.
     * @param key attribute key
     * @return double, or null if key is not present or value is not convertible to a double
     */
    public @Nullable Double getDouble(String key) {
        return getDouble(key, null);
    }

    /**
     * Get the double value associated with the key.
     * @param key key
     * @param def default value
     * @return integer value, or def if key is not present or value is not convertible to a double
     */
    public @Nullable Double getDouble(String key, @Nullable Double def) {
        return AttributeSetCoder.convertValue(get(key), Double.class, def);
    }
    
    /**
     * Retrieve given attribute as a color.
     * @param key attribute key
     * @return color, or null if key is not present or value is not convertible to a color
     */
    public @Nullable Color getColor(String key) {
        return getColor(key, null);
    }

    /**
     * Get the color value associated with the key.
     * @param key key
     * @param def default value
     * @return color value, or def if key is not present or value is not convertible to a color
     */
    public @Nullable Color getColor(String key, @Nullable Color def) {
        return AttributeSetCoder.convertValue(get(key), Color.class, def);
    }

    /**
     * Retrieve given attribute as a point.
     * @param key attribute key
     * @return point, or null if key is not present or value is not convertible to a point
     */
    public @Nullable Point getPoint(String key) {
        return getPoint(key, null);
    }

    /**
     * Get the point value associated with the key.
     * @param key key
     * @param def default value
     * @return point value, or def if key is not present or value is not convertible to a point
     */
    public @Nullable Point getPoint(String key, @Nullable Point def) {
        return AttributeSetCoder.convertValue(get(key), Point.class, def);
    }

    /**
     * Retrieve given attribute as a point.
     * @param key attribute key
     * @return point, or null if key is not present or value is not convertible to a point
     */
    public @Nullable Point2D getPoint2D(String key) {
        return getPoint2D(key, null);
    }

    /**
     * Get the point value associated with the key.
     * @param key key
     * @param def default value
     * @return point value, or def if key is not present or value is not convertible to a point
     */
    public @Nullable Point2D getPoint2D(String key, @Nullable Point2D def) {
        return AttributeSetCoder.convertValue(get(key), Point2D.class, def);
    }
    
    //endregion
    
    //region EVENTS

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
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
        }
    }
    
    //endregion
    
}
