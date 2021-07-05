package com.googlecode.blaisemath.style.ui

import com.google.common.base.Preconditions
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.firestarter.property.PropertyModelSupport
import com.googlecode.blaisemath.firestarter.property.PropertySheet
import com.googlecode.blaisemath.graphics.testui.AnchorTestFrame
import com.googlecode.blaisemath.palette.ui.PaletteEditorTestUi
import com.googlecode.blaisemath.palette.ui.PaletteIconsTestUi
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.ui.AttributeSetPropertyModelTestFrame
import java.awt.Component

/*
* #%L
* BlaiseGraphics
* --
* Copyright (C) 2019 - 2021 Elisha Peterson
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
 * Describes editable attributes of an [AttributeSet], along with their types.
 * This is useful when you want to make sure an attribute set has a given set
 * of attributes, e.g. when saving/restoring the set, or when editing the attributes
 * of the set.
 *
 * @author Elisha Peterson
 */
class AttributeSetPropertyModel(aSet: AttributeSet?, typeMap: MutableMap<String?, Class<*>?>?) : PropertyModelSupport() {
    /** List of expected attribute names  */
    private val attributes: MutableList<String?>? = Lists.newArrayList()

    /** Mapping of expected attribute names and types  */
    private val typeMap: MutableMap<String?, Class<*>?>? = Maps.newLinkedHashMap()

    /** The attribute set for editing  */
    private val aSet: AttributeSet?
    override fun getSize(): Int {
        return attributes.size
    }

    override fun getElementAt(index: Int): String? {
        return attributes.get(index)
    }

    override fun getPropertyType(i: Int): Class<*>? {
        return typeMap.get(attributes.get(i))
    }

    override fun isWritable(i: Int): Boolean {
        return true
    }

    override fun getPropertyValue(i: Int): Any? {
        return aSet.get(attributes.get(i))
    }

    override fun setPropertyValue(i: Int, o: Any?) {
        aSet.put(attributes.get(i), o)
    }

    companion object {
        /**
         * Create and return panel for editing an attribute set, using the specified
         * collection of editable attributes.
         * @param model describes edit object and parameters
         * @return property component for editing the attribute set
         */
        fun editPane(model: AttributeSetPropertyModel?): Component? {
            return PropertySheet(model)
        }
    }

    init {
        this.aSet = Preconditions.checkNotNull(aSet)
        this.typeMap.putAll(typeMap)
        attributes.addAll(typeMap.keys)
    }
}