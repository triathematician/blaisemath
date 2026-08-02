package com.googlecode.blaisemath.style

import com.google.common.base.Joiner
import java.awt.Color
import java.awt.Point
import java.awt.geom.Point2D
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
*/

/**
 * Provides a collection of key-value pairs for style elements, similar to what
 * one finds in CSS style attributes. Values are allowed to be null.
 */
open class AttributeSet {

    /** The parent attribute set  */
    var parent: AttributeSet? = null
    /** The map of style key/value pairs. May contain null values.  */
    val attributeMap = mutableMapOf<String, Any?>()

    /** Attribute keys within this set. */
    val attributes
        get() = attributeMap.keys
    /** Attribute keys, including those in ancestor sets. */
    val allAttributes
        get() = attributes + (parent?.attributes ?: setOf())

    override fun toString(): String {
        return "{ " + Joiner.on("; ").withKeyValueSeparator(":").useForNull("").join(attributeMap) + " }"
    }

    //region BASIC ATTRIBUTE ACCESS

    /** Return true if this set or its ancestors contain the given key. */
    operator fun contains(key: String): Boolean = attributeMap.containsKey(key) || (parent?.contains(key) ?: false)

    /** Gets attribute, returning null if not found. */
    operator fun get(key: String) = getOrDefault(key, null)

    /**
     * Get attribute, or return default if not found.
     * Returns "null" if this class has an explicit entry with a null value for the attribute.
     */
    fun getOrDefault(key: String, def: Any?): Any? {
        if (attributeMap.containsKey(key)) return attributeMap[key]
        parent?.let { return it.getOrDefault(key, def) }
        return def
    }

    /** Gets a filtered set view of all attributes. */
    fun attributes(filter: (String) -> Boolean) = allAttributes.filter(filter)

    /** Return attributes of the given type, whether in this set or the parent set. */
    fun <C> attributesOfType(type: Class<C>): Set<String> {
        return attributeMap.filterValues { type.isInstance(it) }.keys + (parent?.attributesOfType(type) ?: setOf())
    }

    //endregion

    //region ATTRIBUTE MUTATORS

    /** Add the given attribute to this attribute set, returning the old value. */
    open fun put(key: String, value: Any?): Any? {
        val res = attributeMap.put(key, value)
        if (res != value) {
            fireStateChanged()
        }
        return res
    }

    /** Remove attribute with the given key, returning the prior value. */
    open fun remove(key: String): Any? {
        if (attributeMap.containsKey(key)) {
            val res = attributeMap.remove(key)
            fireStateChanged()
            return res
        }
        return null
    }

    /**
     * Adds a value, only if the key is not already present. Will not replace an existing "null" value.
     */
    fun putIfAbsent(key: String, value: Any?) {
        if (!attributeMap.containsKey(key)) {
            put(key, value)
        }
    }

    /** Adds all pairs in the given map. */
    fun putAll(attr: Map<String, Any?>) {
        val old = attributeMap.toMap()
        attributeMap.putAll(attr)
        if (attributeMap != old) {
            fireStateChanged()
        }
    }

    //endregion

    //region TYPED ACCESSORS

    /** Get the string value associated with the key. */
    fun getString(key: String, def: String? = null): String? = AttributeSetCoder.convertValue(get(key), def)
    /** Get the boolean value associated with the key. */
    fun getBoolean(key: String, def: Boolean? = null): Boolean? = AttributeSetCoder.convertValue(get(key), def)
    /** Get the integer value associated with the key. */
    fun getInteger(key: String, def: Int? = null): Int? = AttributeSetCoder.convertValue(get(key), def)
    /** Get the float value associated with the key. */
    fun getFloat(key: String, def: Float? = null): Float? = AttributeSetCoder.convertValue(get(key), def)
    /** Get the double value associated with the key. */
    fun getDouble(key: String, def: Double? = null): Double? = AttributeSetCoder.convertValue(get(key), def)
    /** Get the color value associated with the key. */
    fun getColor(key: String, def: Color? = null): Color? = AttributeSetCoder.convertValue(get(key), def)
    /** Get the point value associated with the key. */
    fun getPoint(key: String, def: Point? = null): Point? = AttributeSetCoder.convertValue(get(key), def)
    /** Get the point value associated with the key. */
    fun getPoint2D(key: String, def: Point2D? = null): Point2D? = AttributeSetCoder.convertValue(get(key), def)

    //endregion

    //region COPIERS

    /** Get an immutable version of this [AttributeSet]. */
    fun immutable(): AttributeSet = ImmutableAttributeSet.immutableCopyOf(this)

    /** Get an immutable version of this [AttributeSet] with an alternate parent. */
    fun immutableWithParent(alternateParent: AttributeSet?): AttributeSet = ImmutableAttributeSet.immutableCopyOf(this, alternateParent)

    /** Create copy, with result having the same parent. */
    fun copy() = AttributeSet().also { copy ->
        copy.parent = parent
        attributeMap.forEach { (key, value) -> copy.attributeMap[key] = copyValue(value) }
    }

    /** Creates a copy of the attribute set, flattening any content from parents. */
    fun flatCopy() = flatCopy(*allAttributes.toTypedArray())

    /** Creates a copy of the given subset of attributes, flattening any content from parents. */
    fun flatCopy(vararg keys: String) = AttributeSet().also { copy ->
        keys.forEach { key -> copy.attributeMap[key] = copyValue(get(key)) }
    }

    //endregion

    //region EVENTS

    private val changeEvent = ChangeEvent(this)
    private val listenerList = EventListenerList()

    fun addChangeListener(l: ChangeListener) = listenerList.add(ChangeListener::class.java, l)
    fun removeChangeListener(l: ChangeListener) = listenerList.remove(ChangeListener::class.java, l)

    /** Notify interested listeners of an (unspecified) change in the plottable.  */
    fun fireStateChanged() {
        val listeners = listenerList.listenerList
        var i = listeners.size - 2
        while (i >= 0) {
            if (listeners[i] === ChangeListener::class.java) {
                (listeners[i + 1] as ChangeListener).stateChanged(changeEvent)
            }
            i -= 2
        }
    }

    //endregion

    companion object {

        /** Constant representing the empty attribute set. */
        val EMPTY = AttributeSet()

        /** Factory method. */
        fun of(key: String, value: Any?) = AttributeSet().apply {
            put(key, value)
        }

        /** Factory method. */
        fun of(key: String, value: Any?, key2: String, value2: Any?) = AttributeSet().apply {
            put(key, value)
            put(key2, value2)
        }

        /** Factory method. */
        fun of(key: String, value: Any?, key2: String, value2: Any?, key3: String, value3: Any?) = AttributeSet().apply {
            put(key, value)
            put(key2, value2)
            put(key3, value3)
        }

        /** Factory method. */
        fun of(key: String, value: Any?, key2: String, value2: Any?, key3: String, value3: Any?, key4: String, value4: Any?) = AttributeSet().apply {
            put(key, value)
            put(key2, value2)
            put(key3, value3)
            put(key4, value4)
        }

        /** Factory method. */
        fun of(key: String, value: Any?, key2: String, value2: Any?, key3: String, value3: Any?, key4: String, value4: Any?, key5: String, value5: Any?) = AttributeSet().apply {
            put(key, value)
            put(key2, value2)
            put(key3, value3)
            put(key4, value4)
            put(key5, value5)
        }

        /** Copies a value in an attribute set, returning a new instance if the value is not an immutable object. */
        private fun <P> copyValue(value: P): P = when {
            value is Point2D -> value.clone() as P
            else -> value
        }

    }
}