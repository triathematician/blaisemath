package com.googlecode.blaisemath.style

import com.google.common.base.Joiner
import com.google.common.collect.Maps
import com.google.common.collect.Sets
import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.Color
import java.awt.Point
import java.awt.geom.Point2D
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Predicate
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import javax.swing.event.EventListenerList

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
*/ /**
 * Provides a collection of key-value pairs for style elements, similar to what
 * one finds in CSS style attributes. Values are allowed to be null.
 *
 * @author Elisha Peterson
 */
open class AttributeSet {
    /** The parent attribute set  */
    var parent: AttributeSet? = null

    /** The map of style key/value pairs. May contain null values.  */
    val attributeMap: MutableMap<String?, Any?>? = Maps.newHashMap()
    private val changeEvent: ChangeEvent? = ChangeEvent(this)
    private val listenerList: EventListenerList? = EventListenerList()
    override fun toString(): String {
        return "{ " + Joiner.on("; ").withKeyValueSeparator(":").useForNull("").join(attributeMap) + " }"
    }
    //endregion
    //region BUILDER METHODS
    /**
     * Builder pattern for setting a key/value pair.
     * @param key the key
     * @param val the value
     * @return this object
     */
    open fun and(key: String?, `val`: Any?): AttributeSet? {
        put(key, `val`)
        return this
    }

    /**
     * Copies the attribute set as an unmodifiable object, which will throw errors
     * if any of its get/put methods are accessed.
     * @return immutable set with all the attributes of this one
     */
    fun immutable(): AttributeSet? {
        return ImmutableAttributeSet.Companion.immutableCopyOf(this)
    }

    /**
     * Copies the attribute set as an unmodifiable object, which will throw errors
     * if any of its get/put methods are accessed.
     * @param par parent to use for copy
     * @return immutable set with all the attributes of this one
     */
    fun immutableWithParent(par: AttributeSet?): AttributeSet? {
        return ImmutableAttributeSet.Companion.immutableCopyOf(this, par)
    }

    /**
     * Creates a copy of the attribute set.
     * @return copy
     */
    fun copy(): AttributeSet? {
        return copyOf(this)
    }

    /**
     * Creates a copy of the attribute set, including all parent attributes.
     * @return copy
     */
    fun flatCopy(): AttributeSet? {
        return flatCopyOf(this)
    }

    //endregion
    //region ATTRIBUTE GETS
    fun getParent(): Optional<AttributeSet?>? {
        return Optional.ofNullable(parent)
    }

    /**
     * Get the set of attributes known in this set.
     * @return attribute keys
     */
    fun getAttributes(): MutableSet<String?>? {
        return Sets.newHashSet(attributeMap.keys)
    }

    /**
     * Get this attributes, and all parent attributes.
     * @return attribute keys
     */
    fun getAllAttributes(): MutableSet<String?>? {
        return if (parent != null) {
            Sets.newHashSet(Sets.union(attributeMap.keys, parent.getAllAttributes()))
        } else {
            Sets.newHashSet(getAttributes())
        }
    }

    /**
     * Gets a filtered set view of attributes.
     * @param filter attribute name filter
     * @return attribute keys
     */
    fun getAttributes(filter: Predicate<String?>?): MutableSet<String?>? {
        return Sets.newHashSet(Sets.filter(getAllAttributes()) { t: String? -> filter.test(t) })
    }

    /**
     * Return attributes of the given type, whether in this set or the parent set.
     * @param type attribute type
     * @return attribute keys
     */
    fun getAllAttributes(type: Class<*>?): MutableSet<String?>? {
        val filtered = Maps.filterValues(attributeMap) { obj: Any? -> type.isInstance(obj) }
        return if (parent != null) {
            Sets.newHashSet(Sets.union(filtered.keys, parent.getAllAttributes(type)))
        } else {
            Sets.newHashSet(filtered.keys)
        }
    }

    /**
     * Get copy of the attributes and the values in this set as a map.
     * @return attribute map
     */
    fun getAttributeMap(): MutableMap<String?, Any?>? {
        return Maps.newHashMap(attributeMap)
    }

    /**
     * Return true if this set or its ancestors contain the given key.
     * @param key attribute key
     * @return true if attribute is accessible from this set
     */
    operator fun contains(key: String?): Boolean {
        return (attributeMap.containsKey(key)
                || parent != null && parent.contains(key))
    }

    /**
     * Get the given attribute. Return null if not found.
     * @param key the key
     * @return value of the found attribute, either contained in this set or its parent,
     * or null if there is none
     */
    operator fun get(key: String?): Any? {
        return getOrDefault(key, null)
    }

    /**
     * Get the given attribute, or return the given default value if not found.
     * Will return "null" if this class has an explicit entry with a null value
     * for the attribute.
     * @param key the key
     * @param def default value to return
     * @return value of the found attribute, either contained in this set or its parent,
     * or the default value if there is none
     */
    fun getOrDefault(key: String?, def: Any?): Any? {
        return if (attributeMap.containsKey(key)) {
            attributeMap.get(key)
        } else if (parent != null) {
            parent.getOrDefault(key, def)
        } else {
            def
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
    open fun put(key: String?, value: Any?): Any? {
        val res = attributeMap.put(key, value)
        if (res != value) {
            fireStateChanged()
        }
        return res
    }

    /**
     * Adds a value, only if the key is not already present.
     * @param key the key
     * @param value the attribute value (may be null)
     */
    fun putIfAbsent(key: String?, value: Any?) {
        if (!attributeMap.containsKey(key)) {
            put(key, value)
        }
    }

    /**
     * Set all of the attributes in the provided map.
     * @param attr map of attributes to set
     */
    fun putAll(attr: MutableMap<String?, *>?) {
        val old = getAttributeMap()
        attributeMap.putAll(attr)
        if (attributeMap != old) {
            fireStateChanged()
        }
    }

    /**
     * Remove attribute with the given key.
     * @param key the key
     * @return the removed value, null if none
     */
    open fun remove(key: String?): Any? {
        if (attributeMap.containsKey(key)) {
            val res = attributeMap.remove(key)
            fireStateChanged()
            return res
        }
        return null
    }
    //endregion
    //region TYPED ACCESSORS
    /**
     * Get the string value associated with the key
     * @param key key
     * @return string value, or null if there is none
     */
    fun getString(key: String?): String? {
        return getString(key, null)
    }

    /**
     * Get the string value associated with the key.
     * @param key key
     * @param def default value
     * @return string value, or def if there is none
     */
    fun getString(key: String?, def: String?): String? {
        return AttributeSetCoder.Companion.convertValue<String?>(get(key), String::class.java, def)
    }

    /**
     * Get the boolean value associated with the key
     * @param key key
     * @return boolean value, or null if there is none
     * @throws UnsupportedOperationException if attribute is present but not a boolean
     */
    fun getBoolean(key: String?): Boolean? {
        return getBoolean(key, null)
    }

    /**
     * Get the boolean value associated with the key.
     * @param key key
     * @param def default value
     * @return boolean value, or def if there is none
     * @throws UnsupportedOperationException if attribute is present but not a boolean
     */
    fun getBoolean(key: String?, def: Boolean?): Boolean? {
        return AttributeSetCoder.Companion.convertValue<Boolean?>(get(key), Boolean::class.java, def)
    }

    /**
     * Retrieve given attribute as an integer.
     * @param key attribute key
     * @return integer, or null if not present
     * @throws UnsupportedOperationException if attribute is present but not a integer
     */
    fun getInteger(key: String?): Int? {
        return getInteger(key, null)
    }

    /**
     * Get the integer value associated with the key.
     * @param key key
     * @param def default value
     * @return integer value, or def if there is none
     * @throws UnsupportedOperationException if attribute is present but not an integer
     */
    fun getInteger(key: String?, def: Int?): Int? {
        return AttributeSetCoder.Companion.convertValue<Int?>(get(key), Int::class.java, def)
    }

    /**
     * Retrieve given attribute as a float.
     * @param key attribute key
     * @return float, or null if key is not present or value is not convertible to a float
     */
    fun getFloat(key: String?): Float? {
        return getFloat(key, null)
    }

    /**
     * Get the float value associated with the key.
     * @param key key
     * @param def default value
     * @return float value, or def if key is not present or value is not convertible to a float
     */
    fun getFloat(key: String?, def: Float?): Float? {
        return AttributeSetCoder.Companion.convertValue<Float?>(get(key), Float::class.java, def)
    }

    /**
     * Retrieve given attribute as a double.
     * @param key attribute key
     * @return double, or null if key is not present or value is not convertible to a double
     */
    fun getDouble(key: String?): Double? {
        return getDouble(key, null)
    }

    /**
     * Get the double value associated with the key.
     * @param key key
     * @param def default value
     * @return integer value, or def if key is not present or value is not convertible to a double
     */
    fun getDouble(key: String?, def: Double?): Double? {
        return AttributeSetCoder.Companion.convertValue<Double?>(get(key), Double::class.java, def)
    }

    /**
     * Retrieve given attribute as a color.
     * @param key attribute key
     * @return color, or null if key is not present or value is not convertible to a color
     */
    fun getColor(key: String?): Color? {
        return getColor(key, null)
    }

    /**
     * Get the color value associated with the key.
     * @param key key
     * @param def default value
     * @return color value, or def if key is not present or value is not convertible to a color
     */
    fun getColor(key: String?, def: Color?): Color? {
        return AttributeSetCoder.Companion.convertValue<Color?>(get(key), Color::class.java, def)
    }

    /**
     * Retrieve given attribute as a point.
     * @param key attribute key
     * @return point, or null if key is not present or value is not convertible to a point
     */
    fun getPoint(key: String?): Point? {
        return getPoint(key, null)
    }

    /**
     * Get the point value associated with the key.
     * @param key key
     * @param def default value
     * @return point value, or def if key is not present or value is not convertible to a point
     */
    fun getPoint(key: String?, def: Point?): Point? {
        return AttributeSetCoder.Companion.convertValue<Point?>(get(key), Point::class.java, def)
    }

    /**
     * Retrieve given attribute as a point.
     * @param key attribute key
     * @return point, or null if key is not present or value is not convertible to a point
     */
    fun getPoint2D(key: String?): Point2D? {
        return getPoint2D(key, null)
    }

    /**
     * Get the point value associated with the key.
     * @param key key
     * @param def default value
     * @return point value, or def if key is not present or value is not convertible to a point
     */
    fun getPoint2D(key: String?, def: Point2D?): Point2D? {
        return AttributeSetCoder.Companion.convertValue<Point2D?>(get(key), Point2D::class.java, def)
    }

    //endregion
    //region EVENTS
    fun addChangeListener(l: ChangeListener?) {
        listenerList.add(ChangeListener::class.java, l)
    }

    fun removeChangeListener(l: ChangeListener?) {
        listenerList.remove(ChangeListener::class.java, l)
    }

    /** Notify interested listeners of an (unspecified) change in the plottable.  */
    fun fireStateChanged() {
        val listeners = listenerList.getListenerList()
        var i = listeners.size - 2
        while (i >= 0) {
            if (listeners[i] === ChangeListener::class.java) {
                (listeners[i + 1] as ChangeListener).stateChanged(changeEvent)
            }
            i -= 2
        }
    } //endregion

    companion object {
        /** Constant representing the empty attribute set  */
        val EMPTY = copyOf(AttributeSet())
        //region FACTORY METHODS
        /**
         * Create new attribute set with elements of given map.
         * @param map key-value map
         * @return new attribute set
         */
        fun create(map: MutableMap<String?, *>?): AttributeSet? {
            val res = AttributeSet()
            res.putAll(map)
            return res
        }

        /**
         * Create new attribute set with given parent.
         * @param parent the parent
         * @return new attribute set
         */
        fun withParent(parent: AttributeSet?): AttributeSet? {
            val res = AttributeSet()
            res.parent = parent
            return res
        }

        /**
         * Create copy of attribute set, with all values copies as well.
         * @param set to copy
         * @return copy
         */
        fun copyOf(set: AttributeSet?): AttributeSet? {
            val res = withParent(set.getParent().orElse(null))
            set.getAttributeMap().forEach(BiConsumer { k: String?, v: Any? -> res.put(k, copyValue<Any?>(v)) })
            return res
        }

        /**
         * Create flat copy of attribute set (including all parent attributes), with all values copies as well.
         * The resulting set has no parent attribute set.
         * @param set to copy
         * @return copy
         */
        fun flatCopyOf(set: AttributeSet?): AttributeSet? {
            val res = AttributeSet()
            for (k in set.getAllAttributes()) {
                res.put(k, copyValue<Any?>(set.get(k)))
            }
            return res
        }

        /**
         * Create a partial copy of the attribute set, with only those values matching
         * the given keys.
         * @param sty style to copy from
         * @param keys keys to copy
         * @return copied style
         */
        fun copy(sty: AttributeSet?, vararg keys: String?): AttributeSet? {
            val res = AttributeSet()
            for (k in keys) {
                if (sty.contains(k)) {
                    res.put(k, sty.get(k))
                }
            }
            return res
        }

        /**
         * Copies a value in an attribute set, returning a new instance if the value
         * is not an immutable object.
         * @param <P> value type
         * @param val value to copy
         * @return new value instance
        </P> */
        private fun <P> copyValue(`val`: P?): P? {
            return if (`val` is Point2D) {
                (`val` as Point2D?).clone() as P
            } else {
                `val`
            }
        }

        /**
         * Generate attribute set with given key/value pair
         * @param k1 the key
         * @param v1 the value
         * @return created set
         */
        fun of(k1: String?, v1: Any?): AttributeSet? {
            return create(Collections.singletonMap(k1, v1))
        }

        /**
         * Generate attribute set with given key/value pairs.
         * @param k1 first key
         * @param v1 first value
         * @param k2 second key
         * @param v2 second value
         * @return created set
         */
        fun of(k1: String?, v1: Any?, k2: String?, v2: Any?): AttributeSet? {
            return of(k1, v1).and(k2, v2)
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
        fun of(
                k1: String?, v1: Any?, k2: String?, v2: Any?,
                k3: String?, v3: Any?
        ): AttributeSet? {
            return of(k1, v1).and(k2, v2).and(k3, v3)
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
        fun of(
                k1: String?, v1: Any?, k2: String?, v2: Any?,
                k3: String?, v3: Any?, k4: String?, v4: Any?
        ): AttributeSet? {
            return of(k1, v1).and(k2, v2).and(k3, v3).and(k4, v4)
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
        fun of(
                k1: String?, v1: Any?, k2: String?, v2: Any?,
                k3: String?, v3: Any?, k4: String?, v4: Any?,
                k5: String?, v5: Any?
        ): AttributeSet? {
            return of(k1, v1).and(k2, v2).and(k3, v3).and(k4, v4).and(k5, v5)
        }
    }
}