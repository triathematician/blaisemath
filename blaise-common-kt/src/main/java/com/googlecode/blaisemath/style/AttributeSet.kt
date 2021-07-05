package com.googlecode.blaisemath.style
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

import com.googlecode.blaisemath.util.type.TypeConverter
import java.awt.Color
import java.awt.Point
import java.awt.geom.Point2D
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import javax.swing.event.EventListenerList

/**
 * Provides a collection of key-value pairs for style elements, similar to what one finds in CSS style attributes.
 * Values are allowed to be null.
 */
open class AttributeSet(val parent: AttributeSet? = null) {

    /** Create [AttributeSet] with given pairs. */
    constructor(parent: AttributeSet?, vararg attributes: Pair<String, Any?>) : this(parent) {
        attributeMap.putAll(attributes)
    }
    /** Create [AttributeSet] with given pairs. */
    constructor(vararg attributes: Pair<String, Any?>) : this() {
        attributeMap.putAll(attributes)
    }

    /** The map of style key/value pairs */
    val attributeMap = mutableMapOf<String, Any?>()
    /** Keys in attribute set */
    val attributes: Set<String>
        get() = attributeMap.keys
    /** Keys in attributes, plus those in parent if present. */
    val allAttributes: Set<String>
        get() = attributes + (parent?.allAttributes ?: setOf())
    /** All key/value pairings, including those in parents. */
    val allAttributeMap: Map<String, Any?>
        get() = allAttributes.map { it to get(it) }.toMap()

    private val changeEvent = ChangeEvent(this)
    private val listenerList = EventListenerList()

    override fun toString() = "{ ${AttributeSetCoder().encode(this)} }"

    //region COPIES

    /** Exact copy */
    fun copy() = AttributeSet(parent).also {
        it.attributeMap.putAll(attributeMap.mapValues { copyValue(it.value) })
    }
    /** Copy with all attributes "flattened", and no parent. */
    fun flatCopy() = AttributeSet().also {
        it.attributeMap.putAll(allAttributeMap.mapValues { copyValue(it.value) }.toMap())
    }
    /** Create a partial copy of the attribute set, with only those values matching the given keys. */
    fun copyOnly(vararg keys: String) = AttributeSet().also {
        it.attributeMap.putAll(allAttributeMap.filter { it.key in keys }.mapValues { copyValue(it.value) }.toMap())
    }

    /** Immutable copy. */
    fun immutable(): AttributeSet = ImmutableAttributeSet(parent).also {
        it.attributeMap.putAll(attributeMap)
    }
    /** Immutable copy, with the parent replaced by the given parent. */
    fun immutableWithParent(otherParent: AttributeSet?): AttributeSet = ImmutableAttributeSet(otherParent).also {
        it.attributeMap.putAll(attributeMap)
    }

    //endregion

    //region QUERIES

    /** Filtered view of attributes. */
    fun allAttributes(filter: (String) -> Boolean) = allAttributes.filter(filter)
    /** Attributes based on value type. */
    fun allAttributes(type: Class<*>): Set<String> = attributeMap.filterValues { type.isInstance(it) }.keys + (parent?.allAttributes(type) ?: setOf())

    /** Check if attribute is in the map. */
    operator fun contains(key: String): Boolean = attributeMap.containsKey(key) || (parent?.contains(key) ?: false)
    /** Get attribute. */
    operator fun get(key: String) = getOrDefault(key, null)
    /** Get attribute, or return default value. */
    fun getOrDefault(key: String, defaultValue: Any?): Any? = when {
        attributeMap.containsKey(key) -> attributeMap[key]
        parent != null -> parent.getOrDefault(key, defaultValue)
        else -> defaultValue
    }

    //endregion

    //region MUTATORS

    /** Set all of the attributes in the provided and return this. */
    fun and(vararg attr: Pair<String, *>) = this.also { putAll(attr.toMap()) }

    /** Add the given attribute to this attribute set, returning the old value. Notifies listeners if changed. */
    open fun put(key: String, value: Any?): Any? {
        return attributeMap.put(key, value).also {
            if (it != value) fireStateChanged()
        }
    }

    /** Adds a value, only if the key is not already present. */
    fun putIfAbsent(key: String, value: Any?) {
        if (!attributeMap.containsKey(key)) put(key, value)
    }

    /** Set all of the attributes in the provided map. */
    fun putAll(attr: Map<String, *>) {
        val old = attributeMap.toMap()
        attributeMap.putAll(attr)
        if (attributeMap != old) fireStateChanged()
    }

    /** Remove attribute with the given key, returning the prior value */
    open fun remove(key: String): Any? {
        val wasPresent = attributeMap.containsKey(key)
        return attributeMap.remove(key).also {
            if (wasPresent) fireStateChanged()
        }
    }

    //endregion

    //region TYPED ACCESSORS

    fun getStringOrNull(key: String) = TypeConverter.convert<String>(get(key))
    fun getBooleanOrNull(key: String) = TypeConverter.convert<Boolean>(get(key))
    fun getIntegerOrNull(key: String) = TypeConverter.convert<Int>(get(key))
    fun getFloatOrNull(key: String) = TypeConverter.convert<Float>(get(key))
    fun getDoubleOrNull(key: String) = TypeConverter.convert<Double>(get(key))
    fun getColorOrNull(key: String) = TypeConverter.convert<Color>(get(key))
    fun getPointOrNull(key: String) = TypeConverter.convert<Point>(get(key))
    fun getPoint2DOrNull(key: String) = TypeConverter.convert<Point2D>(get(key))

    fun getString(key: String, defaultValue: String) = TypeConverter.convert(get(key)) ?: defaultValue
    fun getBoolean(key: String, defaultValue: Boolean) = TypeConverter.convert(get(key)) ?: defaultValue
    fun getInteger(key: String, defaultValue: Int) = TypeConverter.convert(get(key)) ?: defaultValue
    fun getFloat(key: String, defaultValue: Float) = TypeConverter.convert(get(key)) ?: defaultValue
    fun getDouble(key: String, defaultValue: Double) = TypeConverter.convert(get(key)) ?: defaultValue
    fun getColor(key: String, defaultValue: Color) = TypeConverter.convert(get(key)) ?: defaultValue
    fun getPoint(key: String, defaultValue: Point) = TypeConverter.convert(get(key)) ?: defaultValue
    fun getPoint2D(key: String, defaultValue: Point2D) = TypeConverter.convert(get(key)) ?: defaultValue

    //endregion

    //region EVENTS

    /** Notify interested listeners of an (unspecified) change in the plottable.  */
    private fun fireStateChanged() {
        val listeners = listenerList.listenerList
        var i = listeners.size - 2
        while (i >= 0) {
            if (listeners[i] === ChangeListener::class.java) {
                (listeners[i + 1] as ChangeListener).stateChanged(changeEvent)
            }
            i -= 2
        }
    }

    fun addChangeListener(l: ChangeListener) = listenerList.add(ChangeListener::class.java, l)
    fun removeChangeListener(l: ChangeListener) = listenerList.remove(ChangeListener::class.java, l)

    //endregion

    companion object {
        /** Copy a value in an attribute set. */
        private fun <P> copyValue(value: P): P? = when (value) {
            is Point2D -> value.clone() as P
            else -> value
        }
    }

    /** Throws exceptions if there are attempts to modify content. */
    private class ImmutableAttributeSet(parent: AttributeSet?) : AttributeSet(parent) {
        override fun remove(key: String) = notSupported()
        override fun put(key: String, value: Any?) = notSupported()
        private fun notSupported(): Nothing = throw UnsupportedOperationException("ImmutableAttributeSet cannot be modified.")
    }
}