package com.googlecode.blaisemath.graphics.editor

import com.googlecode.blaisemath.firestarter.PropertyModelSupport
import com.googlecode.blaisemath.style.AttributeSet

/**
 * Describes editable attributes of an [AttributeSet], along with their types. This is useful when you want to make sure
 * an attribute set has a given set of attributes, e.g. when saving/restoring the set, or when editing the attributes
 * of the set.
 */
class AttributeSetPropertyModel(val attr: AttributeSet, val typeMap: Map<String, Class<*>>) : PropertyModelSupport() {

    /** List of expected attribute names  */
    private val attributes = typeMap.keys.toList()

    override fun getSize() = attributes.size
    override fun getElementAt(index: Int) = attributes[index]
    override fun getPropertyType(i: Int) = typeMap[attributes[i]]
    override fun isWritable(i: Int) = true

    override fun getPropertyValue(i: Int) = attr[attributes[i]]
    override fun setPropertyValue(i: Int, o: Any) { attr.put(attributes[i], o) }

}